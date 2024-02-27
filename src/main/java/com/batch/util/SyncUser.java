package com.batch.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.batch.jdbc.UserJdbcTemplate;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
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

/**
 * 사용자 배치
 * @author mococo
 *
 */
@Component
public class SyncUser {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(SyncUser.class);
	
	/**
	 * simpleBizDao
	 */
	/* default */ @Autowired /* default */ SimpleBizDao simpleBizDao;
	
	/**
	 * userJdbcTemplate
	 */
	private UserJdbcTemplate userJdbcTemplate;
	
	/**
	 * SPRING_CONFIG_XML
	 */
	private static final String[] SPRING_CONFIG_XML = { "spring/batch-context-" + CustomProperties.getHostIp() + ".xml" };
	
	
	
	/**
	 * orgMstrUser
	 */
	private static Map<String, String> orgMstrUser = new ConcurrentHashMap<>();
	
	/**
	 * orgUserAuth
	 */
	private static Map<String, String> orgUserAuth = new ConcurrentHashMap<>();
	
	/**
	 * orgMstrGroup
	 */
	private static Map<String, String> orgMstrGroup = new ConcurrentHashMap<>();
	
	/**
	 * userList
	 */
	private static List<Map<String, Object>> userList = new ArrayList<>();
	
	
	/**
	 * 기존 부서 그룹 ROOT ID
	 */
	private static final String DEPT_ROOT_ID = CustomProperties.getProperty("mstr.dept.root.id");
	
	/**
	 * 기존 권한 그룹 ROOT ID
	 */
	private static final String AUTH_ROOT_ID = CustomProperties.getProperty("mstr.auth.root.id");
	
	/**
	 * 비활성 유저
	 */
	private static final String DELETE_GROUP_ID = CustomProperties.getProperty("mstr.delete.user.group.id");
	
	/**
	 * active
	 */
	private static boolean active;
	
	
	/**
	 * jobStart
	 */
	public static void jobStart() { active = true; logger.info("[!!! 사용자동기화 작업을 시작합니다.]"); }
	
	
	/**
	 * jobEnd
	 */
	public static void jobEnd() { active = false; logger.info("[!!! 사용자동기화 작업을 종료합니다.]"); }
	
	
	/**
	 * isActive
	 * @return
	 */
	public static boolean isActive() { return active; }
	
	
    /**
     * SyncUser
     */
    public SyncUser() {
    	logger.debug("SyncUser");
    }
	
	
	private void setDao() {
		ApplicationContext context = null;
				
		try {
			context = new ClassPathXmlApplicationContext(SPRING_CONFIG_XML);
			userJdbcTemplate = (UserJdbcTemplate)context.getBean("userJdbcTemplate");
		} catch (BeansException e) {
			if (context != null) {
				((ConfigurableApplicationContext)context).close();
			}
			
			logger.error("setDao Exception", e);
		}
	}
	
	/**
	 * batchDo
	 * @param args
	 */
//	@Scheduled(cron = "0 0/1 * * * *")
//	public static void batchDo() {
	public static void main(String[] args) {
		logger.info("배치 실행");
		
//		if (SyncUser.isActive()) {
//			logger.info("!!! 이미 작업이 실행 중입니다.");
//		} else {
			final SyncUser syncUser = new SyncUser();
			syncUser.setDao();
			syncUser.doSync();
			
//			System.exit(0);
//		}
	}
	
	
	/**
	 * doSync
	 */
	public void doSync() {
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		final Date nowDate = new Date(System.currentTimeMillis());
		final String batchExecuteDate = formatter.format(nowDate);
		final long beforeTimne = System.currentTimeMillis();
		
		WebIServerSession session = null;
		
		try {
			//00.동기화 시작
			jobStart();
			logger.debug("00.배치 동기화 시작 : [{}]", batchExecuteDate);
			
			
			//01.MSTR Admin 세션 생성
			final WebObjectsFactory webObjectFactory = WebObjectsFactory.getInstance();
			session = webObjectFactory.getIServerSession();
			
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", CustomProperties.getProperty("mstr.server.name"));
			connData.put("project", CustomProperties.getProperty("mstr.default.project.name"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", CustomProperties.getProperty("mstr.admin.user.id"));
			connData.put("pwd", EncryptUtil.decrypt(CustomProperties.getProperty("mstr.admin.user.pwd")));
			session = MstrUtil.connectStandardSession(connData);
		    
		    WebObjectSource objSource = null;
			if(session != null) {
				objSource = session.getFactory().getObjectSource();
			}
			logger.debug("01.MSTR 생성 : [{}]", session);
			
			
			//02.기존 MSTR 유저 확인
//			final Map<String, String> orgMstrUser = SyncUserUtil.getAllMstrUser(objSource);
			orgMstrUser = SyncUserUtil.getAllMstrUser(objSource);
			final Iterator<String> orgMstrUserKeys = orgMstrUser.keySet().iterator();
			while(orgMstrUserKeys.hasNext()) {
				final String strKey = orgMstrUserKeys.next();
				String strValue = orgMstrUser.get(strKey);
			}
			final int logTmp1 = orgMstrUser.size();
			logger.debug("02.기존 MSTR 사용자 정보 : [{}]", logTmp1);
			
			
			//03.기존 권한 확인
			orgUserAuth = SyncUserUtil.searchGroup(objSource, AUTH_ROOT_ID);
			final Iterator<String> orgUserAuthKeys = orgUserAuth.keySet().iterator();
			while(orgUserAuthKeys.hasNext()) {
				final String strKey = orgUserAuthKeys.next();
				String strValue = orgUserAuth.get(strKey);
			}
			final int logTmp2 = orgUserAuth.size();
			logger.debug("03.기존 MSTR 권한 정보 : [{}]", logTmp2);
			
			
			//04.기존 부서 확인
			orgMstrGroup = SyncUserUtil.searchGroup(objSource, DEPT_ROOT_ID);
			final Iterator<String> orgMstrGroupKeys = orgMstrGroup.keySet().iterator();
			while(orgMstrGroupKeys.hasNext()) {
				final String strKey = orgMstrGroupKeys.next();
				String strValue = orgMstrGroup.get(strKey);
			}
			final int logTmp3 = orgMstrGroup.size();
			logger.debug("04.기존 MSTR 부서 정보 : [{}]", logTmp3);
			
			
			batchDept(session, objSource);
			
			
			//07.체크할 사용자 정보 가져오기
//			final List<Map<String, Object>> userList = userJdbcTemplate.selectEiamUser();
			userList = userJdbcTemplate.selectEiamUser();
			final int logTmp7 = userList.size();
			logger.debug("07.체크할 유저정보 : [{}]", logTmp7);
			
			
			batchUser(session, objSource);
			
		} catch (WebObjectsException e) {
			logger.error("batch WebObjectsException", e);
		} finally {
			MstrUtil.closeISession(session);
			jobEnd();
			final long afterTimne = System.currentTimeMillis();
			final double logTmp1 = (afterTimne - beforeTimne) / 1000.0;
			logger.info("배치 수행시간 : [{}]", logTmp1);
		}
	}
	
	
	
	private void batchDept(final WebIServerSession serverSession, final WebObjectSource objSource) {
		//05.체크할 부서 정보 가져오기
		final List<Map<String, Object>> departmentList = userJdbcTemplate.selectEiamDepartment();
		final int logTmp4 = departmentList.size();
		logger.debug("05.체크할 부서정보 : [{}]", logTmp4);
		
		
		//06.부서 등록
		for(final Map<String, Object> deptInfo : departmentList) {
			try {
				if(!orgMstrGroup.containsKey(deptInfo.get("부서명").toString())) {
					//미등록 부서
					final String[] groupPath = deptInfo.get("부서경로").toString().split("//");
					String parentGroupId = DEPT_ROOT_ID;
					
					WebUserGroup newGroup;
					for(final String groupPathIdx : groupPath) {
						if(orgMstrGroup.containsKey(groupPathIdx)) {
							//사용자 그룹 존재
							if(serverSession != null) {
								newGroup = (WebUserGroup) objSource.getObject(orgMstrGroup.get(groupPathIdx), EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
								parentGroupId = newGroup.getID();
							}
						} else {
							//사용자 그룹 미존재(New)
							newGroup = MstrUserUtil.createUserGroup(serverSession, groupPathIdx, parentGroupId, "사용자 배치 등록");
							
							final String[] changeComments = {};
							newGroup.setComments(changeComments);
							objSource.save(newGroup);
							
							orgMstrGroup.put(newGroup.getAbbreviation(), newGroup.getID());
							final String logTmp5 = newGroup.getAbbreviation();
							logger.info("06.사용자 그룹 작업 - 신규 부서 추가 : [{}]", logTmp5);
							
							parentGroupId = newGroup.getID();
						}
					}
				}
			} catch (WebObjectsException | WebBeanException e) {
				final String logTmp5 = deptInfo.get("부서명").toString();
				logger.debug("06.부서 에러 - [{}]", logTmp5);
				logger.error("06.departmentList Error", e);
			}
		}
	}
	
	
	private void batchUser(final WebIServerSession serverSession, final WebObjectSource objSource) {
		//08.사용자 등록
		for(final Map<String, Object> userInfo : userList) {
			try {
				WebUserEntity trustee;
				WebUser user;
				
				final String crtUsrId = userInfo.get("사용자사번").toString();
				final String crtUsrNm = userInfo.get("사용자이름").toString();
				final String crtUsrDeptNm = userInfo.get("부서명").toString();
				final String crtUsrAuthNm = userInfo.get("권한명").toString();
				final String crtUsrCheck = userInfo.get("계정상태").toString();
				
				logger.debug("08.처리 사용자 [{}][{}] ==================================", crtUsrId, crtUsrNm);
				if (orgMstrUser.containsKey(crtUsrId)) {
					//기존 유저 확인
					user = MstrUserUtil.getUser(objSource, orgMstrUser.get(crtUsrId));
					trustee = user;
					//유저 활성화 / 비활성화
					changeUserDisable(objSource, user, trustee, crtUsrCheck);
				} else {
					user = MstrUserUtil.createUser(serverSession, crtUsrId, crtUsrNm, CustomProperties.getProperty("mstr.user.default.pwd"));
					
					trustee = user;
					orgMstrUser.put(trustee.getAbbreviation(), trustee.getID());
					
					final String logTmp8 = trustee.getAbbreviation();
					final String logTmp9 = trustee.getName();
					logger.info("08.신규 유저 추가 : [{}][{}]", logTmp8, logTmp9);
				}
				
				
				//권한 판단(일반 파워)
				final Map<String, String> userAuthList = SyncUserUtil.searchUserGroup(user, orgUserAuth);
				logger.debug("08.유저 AS-IS 권한 : [{}]", userAuthList);
				logger.debug("08.유저 TO-BE 권한 : [{}]", crtUsrAuthNm);
				final Iterator<String> userAuthListKeys = userAuthList.keySet().iterator();
				while(userAuthListKeys.hasNext()) {
					final String strKey = userAuthListKeys.next();
					final String strValue = userAuthList.get(strKey);
					
					if(!strKey.equals(crtUsrAuthNm)) {
						//다른 경우 제거
						MstrUserUtil.removeFromUserGroup(objSource, user, strValue);
						logger.info("08.AS-IS 권한 제거 : [{}]", strKey);
					}
				}
				
				if(!userAuthList.containsKey(crtUsrAuthNm)) {
					if(orgUserAuth.containsKey(crtUsrAuthNm)) {
						//권한 없는 경우 추가
						MstrUserUtil.addToUserGroup(objSource, user, orgUserAuth.get(crtUsrAuthNm));
						logger.info("08.TO-BE 권한 추가 : [{}]", crtUsrAuthNm);
					} else {
						logger.info("08.TO-BE 권한 존재하지 않음 : [{}]", crtUsrAuthNm);
					}
				}
				
				
				//부서 판단
				final Map<String, String> userGroupList = SyncUserUtil.searchUserGroup(user, orgMstrGroup);
				logger.debug("08.유저 AS-IS 부서 : [{}]", userGroupList);
				logger.debug("08.유저 TO-BE 부서 : [{}]", crtUsrDeptNm);
				final Iterator<String> userGroupListKeys = userGroupList.keySet().iterator();
				while(userGroupListKeys.hasNext()) {
					final String strKey = userGroupListKeys.next();
					final String strValue = userGroupList.get(strKey);
					
					if(!strKey.equals(crtUsrDeptNm)) {
						//다른 경우 제거
						MstrUserUtil.removeFromUserGroup(objSource, user, strValue);
						logger.info("08.AS-IS 부서 제거 : [{}]", strKey);
					}
				}
				
				if(!userGroupList.containsKey(crtUsrDeptNm)) {
					//부서 없는 경우 추가
					if(orgMstrGroup.containsKey(crtUsrDeptNm)) {
						MstrUserUtil.addToUserGroup(objSource, user, orgMstrGroup.get(crtUsrDeptNm));
						logger.info("08.TO-BE 부서 추가 : [{}]", crtUsrDeptNm);
					} else {
						logger.info("08.TO-BE 부서 존재하지 않음 : [{}]", crtUsrDeptNm);
					}
				}
				
				logger.debug("==================================================================");
			} catch (WebBeanException | WebObjectsException e) {
				final String logTmp5 = userInfo.get("사용자사번").toString();
				logger.debug("08.사용자 에러 - [{}]", logTmp5);
				logger.error("08.userList Error", e);
			}
		}
	}
	
	
	/**
	 * 유저 활성화 / 비활성화
	 * @param objSource
	 * @param user
	 * @param trustee
	 * @param crtUsrCheck
	 * @throws WebObjectsException
	 */
	private void changeUserDisable(final WebObjectSource objSource, final WebUser user, final WebUserEntity trustee, final String crtUsrCheck) throws WebObjectsException {
		
		final Map<String, String> userAuthList = SyncUserUtil.searchUserGroup(user, orgUserAuth);
		
		//기존 등록 유저 : [계정 사용불가] -> [계정 활성화]
		if ("Y".equals(crtUsrCheck) && !user.isEnabled()) {
			user.setEnabled(true);
			objSource.save(user);
			
			if(userAuthList.containsValue(DELETE_GROUP_ID)) {
				MstrUserUtil.removeFromUserGroup(objSource, user, DELETE_GROUP_ID);
			}
			
			final String logTmp5 = trustee.getAbbreviation();
			final String logTmp6 = trustee.getName();
			logger.info("08.[계정 사용불가] -> [계정 활성화] - [{}][{}]", logTmp5, logTmp6);
		}
		
		//기존 등록 유저 : [계정 활성화] -> [계정 사용불가]
		if("N".equals(crtUsrCheck) && user.isEnabled()) {
			user.setEnabled(false);
			objSource.save(user);
			
			if(!userAuthList.containsValue(DELETE_GROUP_ID)) {
				MstrUserUtil.addToUserGroup(objSource, user, DELETE_GROUP_ID);
			}
			
			final String logTmp5 = trustee.getAbbreviation();
			final String logTmp6 = trustee.getName();
			logger.info("08.[계정 활성화] -> [계정 사용불가] - [{}][{}]", logTmp5, logTmp6);
		}
	}
}
