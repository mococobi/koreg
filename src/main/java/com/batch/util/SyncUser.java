/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2024.01.99.
 * 최종변경일 : 2024.01.99.
 * 목적 : 사용자 연동 배치
 * 개정이력 :
 * 	송민권, 2024.01.99, 신규 동기화 배치 작성
*/
package com.batch.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batch.JdbcTemplate.UserJdbcTemplate;
import com.batch.properties.BatchProperties;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserEntity;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.EncryptUtil;

@Component
public class SyncUser {
	
	@Autowired
    SimpleBizDao simpleBizDao;
	
	private static final Logger LOGGER = LogManager.getLogger(SyncUser.class);
	
	private UserJdbcTemplate userJdbcTemplate = null;
	
	private static final String[] SPRING_CONFIG_XML = new String[] { "spring/batch-context-" + BatchProperties.getHostIp() + ".xml" };
	
	private void setDao() {
		ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONFIG_XML);
		userJdbcTemplate = (UserJdbcTemplate)context.getBean("userJdbcTemplate");
	}
	
	private static boolean active = false;
	
	public synchronized static void jobStart() { active = true; LOGGER.info("[!!! 사용자동기화 작업을 시작합니다.]"); }
	public synchronized static void jobEnd() { active = false; LOGGER.info("[!!! 사용자동기화 작업을 종료합니다.]"); }
	public synchronized static boolean isActive() { return active; }
	
	//기존 부서 그룹 ROOT ID
	private static final String DEPT_ROOT_ID = CustomProperties.getProperty("mstr.dept.root.id");
	
	//기존 권한 그룹 ROOT ID
	private static final String AUTH_ROOT_ID = CustomProperties.getProperty("mstr.auth.root.id");
	
	//비활성 유저
	private static final String DELETE_USER_GROUP_ID = CustomProperties.getProperty("mstr.delete.user.group.id");
	
	
//	@Scheduled(cron = "0 0/1 * * * *")
//	public static void batchDo() {
	public static void main(String[] args) {
		LOGGER.info("배치 실행");
		// Sparrow 검출을 피하기 위한 주석 처리 (실제 수행 시에는 필요)
		if (SyncUser.isActive()) {
			LOGGER.info("!!! 이미 작업이 실행 중입니다.");
		} else {
			SyncUser syncUser = new SyncUser();
			syncUser.setDao();
			syncUser.doSync();
			
//			System.exit(0);
		}
	}
	
	
	public void doSync() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date nowDate = new Date(System.currentTimeMillis());
		String batchExecuteDate = formatter.format(nowDate);
		
		WebIServerSession serverSession = null;
		
		long beforeTimne = System.currentTimeMillis();
		
		try {
			//00.동기화 시작
			jobStart();
			LOGGER.debug("00.배치 동기화 시작 : [{}]", batchExecuteDate);
			
			
			//01.MSTR Admin 세션 생성
			WebObjectsFactory webObjectFactory = WebObjectsFactory.getInstance();
			serverSession = webObjectFactory.getIServerSession();
			
			serverSession = MstrUtil.connectSession(
	    		  CustomProperties.getProperty("mstr.server.name")
	    		, CustomProperties.getProperty("mstr.default.project.name")
	    		, Integer.parseInt(CustomProperties.getProperty("mstr.server.port"))
	    		, Integer.parseInt(CustomProperties.getProperty("mstr.session.locale"))
	    		, CustomProperties.getProperty("mstr.admin.user.id")
	    		, EncryptUtil.decrypt(CustomProperties.getProperty("mstr.admin.user.pwd"))
	    	);
		    
		    WebObjectSource objSource = null;
			if(serverSession != null) {
				objSource = serverSession.getFactory().getObjectSource();
			}
			LOGGER.debug("01.MSTR 생성 : [{}]", serverSession);

			
			//02.기존 MSTR 유저 확인
			Map<String, String> orgMstrUser = SyncUserUtil.getAllMstrUser(objSource);
			Iterator<String> orgMstrUserKeys = orgMstrUser.keySet().iterator();
			while(orgMstrUserKeys.hasNext()) {
				String strKey = orgMstrUserKeys.next();
				String strValue = orgMstrUser.get(strKey);
			}
			LOGGER.debug("02.기존 MSTR 사용자 정보 : [{}]", orgMstrUser.size());
			
			
			//03.기존 권한 확인
			Map<String, String> orgUserAuth = SyncUserUtil.searchGroup(objSource, AUTH_ROOT_ID);
			Iterator<String> orgUserAuthKeys = orgUserAuth.keySet().iterator();
			while(orgUserAuthKeys.hasNext()) {
				String strKey = orgUserAuthKeys.next();
				String strValue = orgUserAuth.get(strKey);
			}
			LOGGER.debug("03.기존 MSTR 권한 정보 : [{}]", orgUserAuth.size());
			
			
			//04.기존 부서 확인
			Map<String, String> orgMstrGroup = SyncUserUtil.searchGroup(objSource, DEPT_ROOT_ID);
			Iterator<String> orgMstrGroupKeys = orgMstrGroup.keySet().iterator();
			while(orgMstrGroupKeys.hasNext()) {
				String strKey = orgMstrGroupKeys.next();
				String strValue = orgMstrGroup.get(strKey);
			}
			LOGGER.debug("04.기존 MSTR 부서 정보 : [{}]", orgMstrGroup.size());
			
			
			//05.체크할 부서 정보 가져오기
			List<Map<String, Object>> departmentList = new ArrayList<Map<String, Object>>();
			departmentList = userJdbcTemplate.selectEiamDepartment();
			LOGGER.debug("05.체크할 부서정보 : [{}]", departmentList.size());
			
			
			//06.부서 등록
			for(int i=0; i<departmentList.size(); i++) {
				try {
					if(orgMstrGroup.containsKey(departmentList.get(i).get("부서명").toString())) {
						//등록부서인지 체크
					} else {
						//미등록 부서
						String[] groupPath = departmentList.get(i).get("부서경로").toString().split("//");
						String parentGroupId = DEPT_ROOT_ID;
						
						WebUserGroup newGroup = null;
						for(int gIdx=0; gIdx<groupPath.length; gIdx++) {
							if(orgMstrGroup.containsKey(groupPath[gIdx])) {
								//사용자 그룹 존재
								if(serverSession != null) {
									newGroup = (WebUserGroup) objSource.getObject(orgMstrGroup.get(groupPath[gIdx]), EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
									parentGroupId = newGroup.getID();
								}
							} else {
								//사용자 그룹 미존재(New)
								newGroup = MstrUserUtil.createUserGroup(serverSession, groupPath[gIdx], parentGroupId, "사용자 배치 등록");
								
								String[] changeComments = {};
								newGroup.setComments(changeComments);
								objSource.save(newGroup);
								
								orgMstrGroup.put(newGroup.getAbbreviation(), newGroup.getID());
								LOGGER.info("06.사용자 그룹 작업 - 신규 부서 추가 : [{}]", newGroup.getAbbreviation());
								
								parentGroupId = newGroup.getID();
							}
						}
					}
				} catch (Exception e) {
					LOGGER.debug("06.부서 에러 - [{}]", departmentList.get(i).get("부서명"));
					LOGGER.error("06.departmentList Error", e);
				}
			}
			
			
			//07.체크할 사용자 정보 가져오기
			List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
			userList = userJdbcTemplate.selectEiamUser();
			LOGGER.debug("07.체크할 유저정보 : [{}]", userList.size());
			
			
			//08.사용자 등록
			for(int i=0; i<userList.size(); i++) {
				try {
					WebUserEntity trustee = null;
					WebUser user = null;
					
					String createUserId = userList.get(i).get("사용자사번").toString();
					String createUserNm = userList.get(i).get("사용자이름").toString();
					String createUserDeptName = userList.get(i).get("부서명").toString();
					String createUserAuthName = userList.get(i).get("권한명").toString();
					String createUserCheck = userList.get(i).get("계정상태").toString();
					
					LOGGER.debug("08.처리 사용자 [{}][{}] ==================================", createUserId, createUserNm);
					if (orgMstrUser.containsKey(createUserId)) {
						//기존 유저 확인
						user = MstrUserUtil.getUser(objSource, orgMstrUser.get(createUserId));
						trustee = user;
						
						Map<String, String> userAuthList = SyncUserUtil.searchUserGroup(user, orgUserAuth);
						
						//기존 등록 유저 : [계정 사용불가] -> [계정 활성화]
						if (createUserCheck.equals("Y") && !user.isEnabled()) {
							user.setEnabled(true);
							objSource.save(user);
							
							if(userAuthList.containsValue(DELETE_USER_GROUP_ID)) {
								MstrUserUtil.removeFromUserGroup(objSource, user, DELETE_USER_GROUP_ID);
							}
							
							LOGGER.info("08.[계정 사용불가] -> [계정 활성화] - [{}][{}]", trustee.getAbbreviation(), trustee.getName());
						}
						
						//기존 등록 유저 : [계정 활성화] -> [계정 사용불가]
						if(createUserCheck.equals("N") && user.isEnabled()) {
							user.setEnabled(false);
							objSource.save(user);
							
							if(!userAuthList.containsValue(DELETE_USER_GROUP_ID)) {
								MstrUserUtil.addToUserGroup(objSource, user, DELETE_USER_GROUP_ID);
							}
							
							LOGGER.info("08.[계정 활성화] -> [계정 사용불가] - [{}][{}]", trustee.getAbbreviation(), trustee.getName());
						}
						
					} else {
						user = MstrUserUtil.createUser(serverSession, createUserId, createUserNm, CustomProperties.getProperty("mstr.user.default.pwd"));
						
						trustee = (WebUserEntity) user;
						orgMstrUser.put(trustee.getAbbreviation(), trustee.getID());
						LOGGER.info("08.신규 유저 추가 : [{}][{}]", trustee.getAbbreviation(), trustee.getName());
					}
					
					
					//권한 판단(일반 파워)
					Map<String, String> userAuthList = SyncUserUtil.searchUserGroup(user, orgUserAuth);
					LOGGER.debug("08.유저 AS-IS 권한 : [{}]", userAuthList);
					LOGGER.debug("08.유저 TO-BE 권한 : [{}]", createUserAuthName);
					Iterator<String> userAuthListKeys = userAuthList.keySet().iterator();
					while(userAuthListKeys.hasNext()) {
						String strKey = userAuthListKeys.next();
						String strValue = userAuthList.get(strKey);
						
						if(strKey.equals(createUserAuthName)) {
							//같은 경우 패스
						} else {
							//다른 경우 제거
							MstrUserUtil.removeFromUserGroup(objSource, user, strValue);
							LOGGER.info("08.AS-IS 권한 제거 : [{}]", strKey);
						}
					}
					
					if(!userAuthList.containsKey(createUserAuthName)) {
						if(orgUserAuth.containsKey(createUserAuthName)) {
							//권한 없는 경우 추가
							MstrUserUtil.addToUserGroup(objSource, user, orgUserAuth.get(createUserAuthName));
							LOGGER.info("08.TO-BE 권한 추가 : [{}]", createUserAuthName);
						} else {
							LOGGER.info("08.TO-BE 권한 존재하지 않음 : [{}]", createUserAuthName);
						}
					}
					
					
					//부서 판단
					Map<String, String> userGroupList = SyncUserUtil.searchUserGroup(user, orgMstrGroup);
					LOGGER.debug("08.유저 AS-IS 부서 : [{}]", userGroupList);
					LOGGER.debug("08.유저 TO-BE 부서 : [{}]", createUserDeptName);
					Iterator<String> userGroupListKeys = userGroupList.keySet().iterator();
					while(userGroupListKeys.hasNext()) {
						String strKey = userGroupListKeys.next();
						String strValue = userGroupList.get(strKey);
						
						if(strKey.equals(createUserDeptName)) {
							//같은 경우 패스
						} else {
							//다른 경우 제거
							MstrUserUtil.removeFromUserGroup(objSource, user, strValue);
							LOGGER.info("08.AS-IS 부서 제거 : [{}]", strKey);
						}
					}
					
					if(!userGroupList.containsKey(createUserDeptName)) {
						//부서 없는 경우 추가
						if(orgMstrGroup.containsKey(createUserDeptName)) {
							MstrUserUtil.addToUserGroup(objSource, user, orgMstrGroup.get(createUserDeptName));
							LOGGER.info("08.TO-BE 부서 추가 : [{}]", createUserDeptName);
						} else {
							LOGGER.info("08.TO-BE 부서 존재하지 않음 : [{}]", createUserDeptName);
						}
					}
					
					LOGGER.debug("==================================================================");
				} catch (Exception e) {
					LOGGER.debug("08.사용자 에러 - [{}]", userList.get(i).get("사용자사번"));
					LOGGER.error("08.userList Error", e);
				}
			}
			
			
		} catch (Exception e) {
			LOGGER.error("batch Exception", e);
		} finally {
			MstrUtil.closeISession(serverSession);
			jobEnd();
			long afterTimne = System.currentTimeMillis();
			LOGGER.info("배치 수행시간 : [{}]", (afterTimne - beforeTimne) / 1000.0);
		}
	}
}
