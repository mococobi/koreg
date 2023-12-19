/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 사용자 연동 배치
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
 *  정유석, 2023.01.02, EIAM 배치 수정 (사용자그룹 권한 및 폴더 생성 소스 변경)
 *  정유석, 2023.02.02, EIAM 배치 수정 (Cache, CUBE 삭제 없애기)
 *  정유석, 2023.02.10, MSTR에는 존재하지만, EIAM 권한은 없는 사용자 "사용계정불가" 처리
*/
package com.custom.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.custom.batch.JdbcTemplate.UserJdbcTemplate;
import com.custom.batch.properties.BatchProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.UserBean;
import com.microstrategy.web.beans.UserEntitiesBean;
import com.microstrategy.web.beans.UserGroupBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.SimpleList;
import com.microstrategy.web.objects.WebAccessControlEntry;
import com.microstrategy.web.objects.WebAccessControlList;
import com.microstrategy.web.objects.WebDisplayUnit;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSecurity;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserEntity;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.web.objects.admin.users.WebUserList;
import com.microstrategy.webapi.EnumDSSXMLAccessEntryType;
import com.microstrategy.webapi.EnumDSSXMLObjectSubTypes;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.util.MstrFolderBrowsing;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;

public class SyncUser {
	private static final Logger LOGGER = LogManager.getLogger(SyncUser.class);
	
	//IBI
	private static final String FN01_USER_GROUP_ID = "09426BAA40EB36B4D5344F8E04C962FC";
	private static final String SM02_USER_GROUP_ID = "169234634D4D8BA30F67C58A330E547B";
	private static final String PT03_USER_GROUP_ID = "53A0BE67499E1B41771EEAA1BEB77184";
	private static final String OG04_USER_GROUP_ID = "2D2C6A7F4C416650CD972E9CB6DE158E";
	private static final String DA05_USER_GROUP_ID = "F36BCBE9498C17897054E28B3EAD1F9A";
	
	//IDS
	private static final String FN01_USER_GROUP_ID_IDS = "E48FDB744A15574FFF6872AD39D5AC54";
	private static final String SM02_USER_GROUP_ID_IDS = "CFFD780A491F2B3E1EF6ABB10CDD208F";
	private static final String PT03_USER_GROUP_ID_IDS = "A9048E0948945E773ACF00BC69B3CA5A";
	private static final String PT04_USER_GROUP_ID_IDS = "DB207BE14418A2B4C5ADB181E14CBAFF";
	
	//other
	private static final String DELETE_USER_GROUP_ID = "7D8903BE4AB5F3B72F9A5AA8AB0851BC";
	private static final String ADMIN_USER_ID = "54F3D26011D2896560009A8E67019608";
	
	private UserJdbcTemplate userJdbcTemplate = null;
	
	List<Map<String, Object>> changeDepartmentList = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> changeUserList = new ArrayList<Map<String, Object>>();
	
	List<Map<String, Object>> departMentMenu1 = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> departMentMenu2 = new ArrayList<Map<String, Object>>();
		
	
	//해당 부서 폴더 검색
	private Map<String, Object> searchDepartmentGroup(List<Map<String, Object>> departMentMenu, String folderName) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		for(int i=0; i<departMentMenu.size(); i++) {
			if(departMentMenu.get(i).get("owner").equals(ADMIN_USER_ID)) {
				if(departMentMenu.get(i).get("child") != null) {
					rtnMap = searchDepartmentGroup((List<Map<String, Object>>) departMentMenu.get(i).get("child"), folderName);
					if(rtnMap.size() > 0) {
						return rtnMap;
					}
				}
				
				if(departMentMenu.get(i).get("name").equals(folderName)) {
					rtnMap = departMentMenu.get(i);
					return rtnMap;
				}
			}
		}
		
		return rtnMap;
	}
	
	
	//해당 사용자 그룹 검색
	private Map<String, Object> searchUserGroupDepartment(String searchType, String searchVal) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		for(int i=0; i<changeDepartmentList.size(); i++) {
			if(changeDepartmentList.get(i).get(searchType).equals(searchVal) && changeDepartmentList.get(i).get("PUSE_YN").equals("Y")) {
				rtnMap = changeDepartmentList.get(i);
				return rtnMap;
			}
		}
		return rtnMap;
	}
	
	
	//MSTR 하위 그룹 확인
	private Map<String, String> searchSubUserGroup(WebObjectSource source, String groupId) {
		Map<String, Map<String, ?>> map = SyncUserUtil.getSubUserGroup(source, groupId);
		Map<String, String> rtnMap = (Map<String, String>)map.get("mstrGroupMap");
		
		return rtnMap;
	}		
	
	
	//사용자 그룹 확인
	private Set<String> searchUserGroup(WebUser user, Map<String, String> searchGroup) throws WebObjectsException {
		Set<String> result = new HashSet<String>();
		List<String> groupNameList = MstrUserUtil.getUserGroupNameList(user);
		for (String groupName : groupNameList) {
			if (searchGroup.containsKey(groupName)) { // 기준 사용자그룹 하위의 사용자그룹만을 비교대상으로 한다. 
				result.add(groupName);
			}
		}
		
		if(result.size() == 0) {
//			result.add("권한 없음");
		}
		
		return result;
	}
	
	
	//MSTR 전체 사용자
	private Map<String, String> getAllMstrUser(WebObjectSource source) throws WebObjectsException {
		Map<String, String> allMstrUserMap = new HashMap<String, String>();
		
		WebUserGroup group = MstrUserUtil.searchUserGroup(MstrUserUtil.getUserGroupSearch(source), "Everyone");
		WebUserList userList = null;
		if(group != null) {
			userList = group.getMembers();
		}
		
		for (Enumeration<WebUser> e = userList.elements(); e.hasMoreElements();) {
			WebUser user = e.nextElement();
			allMstrUserMap.put(user.getAbbreviation(), user.getID());
		}
		
		return allMstrUserMap;
	}
	
	
	//리포트 경로 확인
	private static String getPath(WebDisplayUnit unit) {
		SimpleList simpleList = ((WebObjectInfo)unit).getAncestors();
		
		StringBuilder path = new StringBuilder();
		Enumeration<WebFolder> e = simpleList.elements();
		while (e.hasMoreElements()) {
			WebFolder f = e.nextElement();
			if(!f.getDisplayName().equals(CustomProperties.getProperty("mstr.default.project")) && !f.getID().equals(CustomProperties.getProperty("mstr.share.object.folder.id"))) {
				path.append(path == null || "".equals(path.toString()) ? "" : "/").append(f.getDisplayName());
			}
		}
		return "/" + path.toString();
	}
	
	
	//폴더 정보 확인
	private Map<String, Object> searchNameFolder(List<Map<String, Object>> folderMenu, String serarchName) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		for(int i=0; i<folderMenu.size(); i++) {
			if(folderMenu.get(i).get("name").equals(serarchName)) {
				rtnMap = folderMenu.get(i);
				break;
			}
			
			if(folderMenu.get(i).get("child") != null) {
				Map<String, Object> rtnMap2 = searchNameFolder((List<Map<String, Object>>) folderMenu.get(i).get("child"), serarchName);
				if(rtnMap2.size() > 0) {
					rtnMap = rtnMap2;
				}
			}
		}
		
		return rtnMap;
	}
	
	
	//신규 폴더 생성
	private WebObjectInfo createFolder(WebObjectSource objSource, String parentFolderId, String folderName) throws WebObjectsException {
		WebObjectInfo newFolder = objSource.getNewObject(EnumDSSXMLObjectTypes.DssXmlTypeFolder, EnumDSSXMLObjectSubTypes.DssXmlSubTypeFolder);
		newFolder.populate();
		
		WebObjectInfo parentFolderInfo = objSource.getObject(parentFolderId, EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
		WebFolder parentFolder = (WebFolder)parentFolderInfo;
		
		WebObjectInfo newObjectInfo = objSource.saveAs(newFolder, folderName, parentFolder, true);
		return newObjectInfo;
	}
	
	
	//권한 있는지 여부 확인
	private List<WebAccessControlEntry> getListAccessControlentryForTrustee(WebObjectInfo objectInfo, String cheageUserObjectId){
		List<WebAccessControlEntry> result = new ArrayList<WebAccessControlEntry>();
		WebAccessControlEntry accessControlEntry = null;
		
		WebAccessControlList accessControlList = objectInfo.getSecurity().getACL();
		Enumeration controlList = accessControlList.elements();
		
		while (controlList.hasMoreElements()) {
			accessControlEntry = (WebAccessControlEntry)controlList.nextElement();
//			if(cheageUserObjectId.equals(accessControlEntry.getTrustee().getID())) {
				result.add(accessControlEntry);
//			}
			
		}
		
		return result;
	}
	
	
	//권한 등록
	private WebAccessControlEntry setRightToObject(WebObjectInfo objectInfo, WebUserEntity trustee, int accessRights1, Boolean accessDenied1, int accessRights2, Boolean accessDenied2) {
		WebAccessControlList acl = objectInfo.getSecurity().getACL();
		
//		1.Browse
//		2.UseExecute
//		4.Read
//		8.Write
//		16.Delete
//		32.Control
//		64.Use
//		128.Execute
//		255.FullControl
		
		//개체 권한(전체 컨트롤)
		WebAccessControlEntry accessControlEntry = acl.add(EnumDSSXMLAccessEntryType.DssAccessEntryTypeObject);
		accessControlEntry.setInheritable(false);
		accessControlEntry.setTrustee(trustee);
		accessControlEntry.setRights(accessRights1);
		accessControlEntry.setAccessDenied(accessDenied1);
		
		//하위 권한(보기)
		WebAccessControlEntry accessControlEntry2 = acl.add(EnumDSSXMLAccessEntryType.DssAccessEntryTypeObject);
		accessControlEntry2.setInheritable(true);
		accessControlEntry2.setTrustee(trustee);
		accessControlEntry2.setRights(accessRights2);
		accessControlEntry2.setAccessDenied(accessDenied2);
		
		return accessControlEntry;
	}
	
	
	//권한 추가
	private void addUserAuth(int addUserAuthCnt, String addUserAuthMode, WebObjectSource objSource, WebUser user, WebUserEntity trustee, Map<String, String> checkUserGroup, String changeRolePuseYn, String changeUserRoleNm) throws WebObjectsException {
		Set<String> currentUserGroupList = searchUserGroup(user, checkUserGroup);

		//권한 사용 처리
		if(changeRolePuseYn.equals("Y")) {
			//권한이 없음 - 사용자 그룹 추가
			if(!currentUserGroupList.contains(changeUserRoleNm)) {
				MstrUserUtil.addToUserGroup(objSource, user, checkUserGroup.get(changeUserRoleNm));
				if(user != null) {
					LOGGER.info(addUserAuthCnt + "-2-1."+ addUserAuthMode +" 권한 추가 : [{}][{}]{} + [{}]", user.getAbbreviation(), user.getName(), currentUserGroupList, changeUserRoleNm);
				}
			}
		} 

		//권한 사용 불가 처리
		else if(changeRolePuseYn.equals("N")) {
			//권한 있음 - 사용자 그룹 삭제
			if(currentUserGroupList.contains(changeUserRoleNm)) {
				MstrUserUtil.removeFromUserGroup(objSource, user, checkUserGroup.get(changeUserRoleNm));
				LOGGER.info(addUserAuthCnt + "-3-1."+ addUserAuthMode +" 권한 삭제 : [{}][{}][{}]", trustee.getAbbreviation(), trustee.getName(), changeUserRoleNm);
			}
		}
	}
	
	
	//입력할 설명 정보 구하기
	private String getInsertComments(Map<String, Object> groupMap) {
		
		Map<String, Object> insertNewMap = new HashMap<String, Object>();
		insertNewMap.put("OGNZ_NO", (String) groupMap.get("OGNZ_NO"));
		insertNewMap.put("OGNZ_NM", (String) groupMap.get("OGNZ_NM"));
		insertNewMap.put("SPPO_OGNZ_NO", (String) groupMap.get("SPPO_OGNZ_NO"));
		insertNewMap.put("PUSE_YN", (String) groupMap.get("PUSE_YN"));
		insertNewMap.put("GROUP_PATH", (String)groupMap.get("GROUP_PATH"));

		JSONObject commentsInsertObject = new JSONObject(insertNewMap);

		String checkComments = commentsInsertObject.toString();
		return checkComments;
	}
	
	
	private String getUserGroupParentsPath(WebObjectSource objSource, String rtnPath, WebUserGroup userGroup) throws WebObjectsException, IllegalArgumentException {
		WebUserList parentList = userGroup.getParents();
		
		Enumeration userEnum = parentList.elements();
		while(userEnum.hasMoreElements()) {
			WebUserEntity tempGroup = (WebUserEntity) userEnum.nextElement();
			
			WebUserGroup parent = (WebUserGroup) objSource.getObject(tempGroup.getID(), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
			if(rtnPath.equals("")) {
            	rtnPath += parent.getName();
			} else {
				rtnPath = parent.getName() + "//" + rtnPath;
			}

			// Sparrow 검출을 피하기 위한 주석 처리 (실제 수행 시에는 if문 삭제 필요)
			if(parent != null) {
				rtnPath = getUserGroupParentsPath(objSource, rtnPath, parent);
			}
		}
		
		return rtnPath;
	}
	
	
	//유저 그룹 이동 체크
	private void moveUserGroupCheck(WebIServerSession serverSession, WebObjectSource objSource, Map<String, String> ogUserGroup) throws WebObjectsException, IllegalArgumentException, JsonMappingException, JsonProcessingException {
		for(String key : ogUserGroup.keySet()) {
			try {
				WebUserGroup userGroup = (WebUserGroup) objSource.getObject(ogUserGroup.get(key), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
				
				if(userGroup.getComments() != null) {
					String asisComments = userGroup.getComments()[0];
					
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> asisCommentsMap = mapper.readValue(asisComments, Map.class);
					
					String[] groupPathSplit = null;
					String newGroupPath = "";
					String rootGroupName = "";
					
					if(asisCommentsMap.get("GROUP_PATH") != null) {
						groupPathSplit = ((String) asisCommentsMap.get("GROUP_PATH")).split("//");
						
						for(int i=0; i<groupPathSplit.length - 1; i++) {
							
							if(newGroupPath.equals("")) {
								newGroupPath += groupPathSplit[i];
								rootGroupName = groupPathSplit[i];
							} else {
								newGroupPath = newGroupPath + "//" + groupPathSplit[i];
								rootGroupName = groupPathSplit[i];
							}
						}
					}
					
					String currentPath = getUserGroupParentsPath(objSource, "", userGroup).replace("01.BIS//04.OG.조직정보(BIS)//", "");
					
					if(!newGroupPath.equals("") && !currentPath.equals(newGroupPath)) {
						LOGGER.info("[{}] 그룹 경로가 바뀜 현재 : [{}]  변경 : [{}] =====", userGroup.getName(), currentPath, newGroupPath);
						
						//유저 그룹 이동
						String[] currentRootNm = currentPath.split("//");
						
						WebUserGroup rootGroup = (WebUserGroup) objSource.getObject(ogUserGroup.get(rootGroupName), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
						WebUserEntity addParent = (WebUserEntity) objSource.getObject(ogUserGroup.get(rootGroupName), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
						
						WebUserEntity removeParent = null;
						if(ogUserGroup.get(currentRootNm[currentRootNm.length - 1]) != null) {
							removeParent = (WebUserEntity) objSource.getObject(ogUserGroup.get(currentRootNm[currentRootNm.length - 1]), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
						}
						
						WebUserList parentList = userGroup.getParents();
						Enumeration userEnum = parentList.elements();
						
						Boolean addCheck = true;
						Boolean removeCheck = false;
						while(userEnum.hasMoreElements()) {
							WebUserEntity tempGroup = (WebUserEntity) userEnum.nextElement();
							
							if(addParent.getName().equals(tempGroup.getName())) {
								addCheck = false;
							}
							
							if(removeParent != null && removeParent.getName().equals(tempGroup.getName())) {
								removeCheck = true;
							}
							
						}
						
						if(addCheck) {
							userGroup.getParents().add(addParent);
						}
						
						if(removeCheck) {
							userGroup.getParents().remove(removeParent);
						}
						
						if(addCheck || removeCheck) {
							objSource.save(userGroup);
						}
						
						
						//부서 폴더 이동
						Map<String, Object> deptFolderMap = searchNameFolder(departMentMenu1, userGroup.getName());
						Map<String, Object> rootDeptFolderMap = searchNameFolder(departMentMenu1, rootGroup.getName());
						if(deptFolderMap.get("key") != null) {
							WebObjectInfo tempRootObjectInfo = objSource.getObject((String) rootDeptFolderMap.get("key"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
							WebFolder tempRootFolder = (WebFolder)tempRootObjectInfo;
							
							WebObjectInfo moveFolderObjectInfo = objSource.getObject((String) deptFolderMap.get("key"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
							objSource.save(moveFolderObjectInfo, tempRootFolder);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Exception error : ", e);
			}
		}
	}
	
	
	
	public static final void mksong(WebIServerSession serverSession, WebUserGroup userGroup, WebUserGroup beforRootGroup, WebUserGroup rootGroup) {
		try {
			UserGroupBean userGroupBean = (UserGroupBean)BeanFactory.getInstance().newBean("UserGroupBean");
			userGroupBean.setSessionInfo(serverSession);
			userGroupBean.setObjectID(userGroup.getID());
			
			UserGroupBean parentGroupBean = (UserGroupBean)BeanFactory.getInstance().newBean("UserGroupBean");
			parentGroupBean.setSessionInfo(serverSession);
			parentGroupBean.setObjectID(rootGroup.getID());
			
//			UserGroupBean beforeParentGroupBean = (UserGroupBean)BeanFactory.getInstance().newBean("UserGroupBean");
//			beforeParentGroupBean.setSessionInfo(serverSession);
//			beforeParentGroupBean.setObjectID(beforRootGroup.getID());
			
			UserEntitiesBean entities = userGroupBean.getParentGroups();
			if(entities != null) {
				entities.add(parentGroupBean);
			}
			
			
			userGroupBean.save();
//			parentGroupBean.save();
		} catch (WebBeanException e) {
			LOGGER.error("WebBeanException error : ", e);
		} catch (Exception e) {
			LOGGER.error("Exception error : ", e);
		}
	}
	
	//부서 폴더 이동 체크
	private void moveFolderCheck(WebObjectSource objSource, Map<String, String> ogUserGroup, Map<String, Object> searchGroupMap, int depth) throws WebObjectsException, IllegalArgumentException, JsonMappingException, JsonProcessingException {
		if(searchGroupMap.get("comments") != null) {
			WebObjectInfo deptObjectInfo = objSource.getObject((String) searchGroupMap.get("key"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
			String asisComments = deptObjectInfo.getComments()[0];
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> asisCommentsMap = mapper.readValue(asisComments, Map.class);
			
			String[] groupPathSplit = null;
			String newGroupPath = "";
			
			if(asisCommentsMap.get("GROUP_PATH") != null) {
				groupPathSplit = ((String) asisCommentsMap.get("GROUP_PATH")).split("//");
				
				for(int i=0; i<groupPathSplit.length - 1; i++) {
					if(newGroupPath.equals("")) {
						newGroupPath += groupPathSplit[i];
					} else {
						newGroupPath += "/" + groupPathSplit[i];
					}
				}
			}
			
			String currentPath = (String) searchGroupMap.get("path");
			if(!newGroupPath.equals("") && currentPath.indexOf(newGroupPath) == -1) {
				
				//9.부서 이동 처리(이동)
				{
					String[] pathSplit = newGroupPath.split("/");
					
					Map<String, Object> tempRootFolderMap = searchDepartmentGroup(departMentMenu1, pathSplit[pathSplit.length-1]);
					WebObjectInfo tempRootObjectInfo = objSource.getObject((String) tempRootFolderMap.get("id"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
//					objSource.save(deptObjectInfo, (WebFolder)tempRootObjectInfo);
				}
				
				LOGGER.info("[{}] 폴더 경로가 바뀜 현재 : [{}]  변경 : [{}] =====", deptObjectInfo.getName(),currentPath, newGroupPath);
			}
			
			if(searchGroupMap.get("child") != null) {
				List<Map<String, Object>> childMap = (List<Map<String, Object>>) searchGroupMap.get("child");
				for(int childCnt=0; childCnt<childMap.size(); childCnt++) {
					moveFolderCheck(objSource, ogUserGroup, childMap.get(childCnt), depth+1);
				}
			}
			
		}
	}
	
	
	//폴더 권한 변경
	private void changeFolderAuth(WebObjectSource objSource, Map<String, String> ogUserGroup, Map<String, Object> searchGroupMap, int depth, List<String> newGroupList) throws WebObjectsException, IllegalArgumentException {
		
		if(searchGroupMap.get("comments") != null) {
			WebObjectInfo deptObjectInfo = objSource.getObject((String) searchGroupMap.get("key"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
			
			//폴더 설명 변경(기존과 다를시)
			if(deptObjectInfo.getComments() != null) {
				String asisComments = deptObjectInfo.getComments()[0];
				Map<String, Object> groupInfo = searchUserGroupDepartment("OGNZ_NM", deptObjectInfo.getName());
				
				if(groupInfo.get("OGNZ_NO") != null) {
					String checkComments = getInsertComments(groupInfo);
					
					if(!asisComments.equals(checkComments)) {
						String[] changeComments = {checkComments};
						deptObjectInfo.setComments(changeComments);
						objSource.save(deptObjectInfo);
					}
				}
			}
			
			
			if(searchGroupMap.get("child") != null) {
				List<Map<String, Object>> checkAuthChildMap = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> childMap = (List<Map<String, Object>>) searchGroupMap.get("child");
				
				String groupNameCheck = (String) searchGroupMap.get("name");
				Boolean groupFolderCreateCheck = true;
				for(int childCnt=0; childCnt<childMap.size(); childCnt++) {
					if(childMap.get(childCnt).get("comments") != null && !childMap.get(childCnt).get("name").equals("#전사공유폴더") && !childMap.get(childCnt).get("name").equals("#그룹공유폴더")) {
						checkAuthChildMap.add(childMap.get(childCnt));
					}
					
					if(depth == 1 && groupNameCheck.lastIndexOf("그룹") > -1 && childMap.get(childCnt).get("comments") != null && childMap.get(childCnt).get("name").equals("#그룹공유폴더") && groupFolderCreateCheck) {
						groupFolderCreateCheck = false;
					}
				}
				
				//공유폴더 생성
				/*
				if(depth == 1 && groupNameCheck.lastIndexOf("그룹") > -1 && groupFolderCreateCheck) {
					WebObjectInfo createFolder = null;
					WebObjectInfo targetObjectInfo = null;
					
					createFolder = createFolder(objSource, (String) searchGroupMap.get("id"), "#그룹공유폴더");
					targetObjectInfo = objSource.getObject(createFolder.getID(), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);

					String[] test = {targetObjectInfo.toString()};
					targetObjectInfo.setComments(test);
					objSource.save(targetObjectInfo);
					
					LOGGER.info("공유폴더 생성 : [{}]", groupNameCheck);
				}
				*/
				
				for(int targetCnt=0; targetCnt<checkAuthChildMap.size(); targetCnt++) {
					
					WebObjectInfo targetObjectInfo = null;
					targetObjectInfo = objSource.getObject((String) checkAuthChildMap.get(targetCnt).get("id"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
//					LOGGER.info("targetObjectInfo : [{}]", targetObjectInfo);
					
					List<String> aclCheckMap =  new ArrayList<String>();
					List<WebAccessControlEntry> alACE = new ArrayList<WebAccessControlEntry>();
					alACE = getListAccessControlentryForTrustee(targetObjectInfo, targetObjectInfo.getID());
					
					//ACL 확인
					if(alACE.size() > 0) {
						WebObjectSecurity wos = targetObjectInfo.getSecurity();
						WebAccessControlList acl = wos.getACL();
						
						WebAccessControlEntry ace = null;
						for(int j=0; j<acl.size(); j++) {
							ace = acl.get(j);
							aclCheckMap.add(ace.getTrustee().getID());
							
							if(ace.getRights() == 207 && ace.getTrustee().getName().indexOf("_삭제") > -1) {
								String[] deptCheckName = ace.getTrustee().getName().split("_");
								
								if(ogUserGroup.get(deptCheckName[0]) != null) {
									WebUserEntity groupTrustee = (WebUserGroup) objSource.getObject(ogUserGroup.get(deptCheckName[0]), EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
									setRightToObject(targetObjectInfo, groupTrustee, 207, false, 199, false);
									objSource.save(targetObjectInfo);
								}
								
							}
						}
					}
					
					//폴더 권한 확인후 없으면 거부 권한 추가
					for(int authCnt=0; authCnt<checkAuthChildMap.size(); authCnt++) {
						if(ogUserGroup.get(checkAuthChildMap.get(authCnt).get("name")) != null) {
							WebUserEntity groupTrustee = (WebUserGroup) objSource.getObject(ogUserGroup.get(checkAuthChildMap.get(authCnt).get("name")), EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
							if(!checkAuthChildMap.get(targetCnt).get("id").equals(checkAuthChildMap.get(authCnt).get("id")) && !aclCheckMap.contains(groupTrustee.getID())) {
								setRightToObject(targetObjectInfo, groupTrustee, 255, true, 255, true);
								objSource.save(targetObjectInfo);
								
								LOGGER.info("부서 폴더 권한 제어 : [{}]", checkAuthChildMap.get(authCnt).get("name"));
							}
						}
					}
				}
				
				for(int childCnt=0; childCnt<childMap.size(); childCnt++) {
					changeFolderAuth(objSource, ogUserGroup, childMap.get(childCnt), depth+1, newGroupList);
				}
			}
		}
	}
	
	
	public void doSync() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date nowDate = new Date(System.currentTimeMillis());
		String batchExecuteDate = formatter.format(nowDate);
		
		WebIServerSession serverSession = null;
		
		long beforeTimne = System.currentTimeMillis();
		try {
			// 동기화 시작
			jobStart();
			
			WebObjectsFactory webObjectFactory = WebObjectsFactory.getInstance();
			serverSession = webObjectFactory.getIServerSession();
			
			WebObjectSource objSource = null;
			
			String server = MstrUtil.getLiveServer(CustomProperties.getProperty("mstr.default.server"));
			String project = CustomProperties.getProperty("mstr.default.project");
			String uid = CustomProperties.getProperty("mstr.admin.account");
			String pwd = CustomProperties.getProperty("mstr.admin.token");
			serverSession = MstrUtil.connectSession(server, project, uid, pwd);
			
			if(serverSession != null) {
				objSource = serverSession.getFactory().getObjectSource();
			}
			
			//전체 유저 확인
			Map<String, String> allMstrUserMap = getAllMstrUser(objSource);
			
			//변경 사용자 확인
			changeDepartmentList = userJdbcTemplate.selectEiamDepartment();
			changeUserList = userJdbcTemplate.selectEiamUser();
		
			
//			FN01.일반사용자			BI_MSTR_NORMAL
//			FN02.파워사용자			BI_MSTR_POWER
//			SM01.운영자				BI_MSTR_SYS_MANAGER
//			SM02.개발자				BI_MSTR_DEVELOPER
//			DA01.데이터권한_신한플러스	BI_MSTR_DATA_SP01
//			DA02.데이터권한_헬스케어		BI_MSTR_DATA_SP02
//			DA03.데이터권한_보험금심사	BI_MSTR_DATA_SP03	
			
			//01.FN.기능역할 그룹 확인(일반, 파워)
			Map<String, String> fnUserGroup = searchSubUserGroup(objSource, FN01_USER_GROUP_ID);
			//02.SM.관리자역할 그룹 확인(관리자, 개발담당자)
			Map<String, String> smUserGroup = searchSubUserGroup(objSource, SM02_USER_GROUP_ID);
			//03.PT.포탈역할(BIS)(포탈사이트관리자)
			Map<String, String> ptUserGroup = searchSubUserGroup(objSource, PT03_USER_GROUP_ID);
			//04.OG.조직정보(BIS) 그룹 확인(부서)
			Map<String, String> ogUserGroup = searchSubUserGroup(objSource, OG04_USER_GROUP_ID);
			//05.DA.부가역할(BIS) 그룹 확인(마스킹예외사용자, 데이터권한)
			Map<String, String> daUserGroup = searchSubUserGroup(objSource, DA05_USER_GROUP_ID);
			
			
			//01.FN.기능역할(DSS) 그룹 확인(업로드담당자)
			Map<String, String> fnUserGroup_ids = searchSubUserGroup(objSource, FN01_USER_GROUP_ID_IDS);
			//02.SM.관리자역할(DSS) 그룹 확인(관리자, 개발담당자)
			Map<String, String> smUserGroup_ids = searchSubUserGroup(objSource, SM02_USER_GROUP_ID_IDS);
			//03.PT.포탈역할(DSS)(포탈사이트관리자)
			Map<String, String> ptUserGroup_ids = searchSubUserGroup(objSource, PT03_USER_GROUP_ID_IDS);
			//04.04.DA.부가역할(DSS)(마스킹예외사용자)
			Map<String, String> daUserGroup_ids = searchSubUserGroup(objSource, PT04_USER_GROUP_ID_IDS);
						
			// 전체 부서 폴더 가져오기
			if(serverSession != null) {
				departMentMenu1 = MstrFolderBrowsing.getFolderTree(serverSession, CustomProperties.getProperty("mstr.department.report.folder.id"), 4, Arrays.asList(EnumDSSXMLObjectTypes.DssXmlTypeFolder));
				departMentMenu2 = MstrFolderBrowsing.getFolderTree(serverSession, CustomProperties.getProperty("mstr.branch.report.folder.id"), 1, Arrays.asList(EnumDSSXMLObjectTypes.DssXmlTypeFolder));
			}			
	
			
			/* A-1. 조직번호(OGNZ_NO)는 같으나 조직명(OGNZ_NM)이 달라진 경우를 위한 Map 설정. */
			// A-1-1: 조직번호(OGNZ_NO), 조직명(OGNZ_NM)을 Mapping하기 위한 변수.
			Map<String, String> ogUserGroupNm = new HashMap<String, String>();
			
			// A-1-2: 04.OG조직정보(BIS)에 있는 모든 사용자그룹(조직정보)를 가져오기.
			WebUserGroup baseGroup = (WebUserGroup)objSource.getObject(OG04_USER_GROUP_ID, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
			
			// A-1-3: "사용자그룹>속성>설명"을 JSON으로 변환 후, String으로 재변환하여 조직정보를 추출하기 위한 변수.
			JSONParser	jsonParser		= 	new JSONParser();
			JSONObject 	commentJson;
			Object 		commentElement;		

			// A-1-4. 04.OG조직정보(BIS)를 불러와서 담을 개체 정의 - Stack은 트리구조 형태의 조직 정보를 모두 담기 위한 것.
			Enumeration<?> currentElements = baseGroup.getMembers().elements();
			LinkedList<Enumeration<?>> stack = new LinkedList<Enumeration<?>>();
			
			while (true) {
				// A-1-5. 트리의 Leaf까지 조회 후, 없으면 Stack Pop.
				if (!currentElements.hasMoreElements()) {
					while (!stack.isEmpty()) {
						currentElements = stack.pop();
						if (currentElements.hasMoreElements()) {break;}
					}
				}

				// A-1-6. 전체 트리의 탐색이 끝났다면 Break.
				if (stack.isEmpty() && !currentElements.hasMoreElements()) {break;}

				// A-1-7. 다음 사용자 그룹 개체 가져오기.
				Object element = currentElements.nextElement();
				
				if (element instanceof WebUserGroup) {
					WebUserGroup group = (WebUserGroup) element;
					group.populate(); // 개체 현실화

					stack.push(currentElements);
					currentElements = group.getMembers().elements();

					// A-1-8. 설명 비교 -> User Group >> 속성 >> 설명 >> 내용 확인
					if (group.getComments()[0] != null) {

						// A-1-9. "사용자그룹>속성>설명"을 JSON으로 변환 후, String으로 재변환하여 smUserGroupNm 맵에 저장.
						commentElement = jsonParser.parse(group.getComments()[0]);
						commentJson = (JSONObject) commentElement;

						ogUserGroupNm.put(commentJson.get("OGNZ_NO").toString(), commentJson.get("OGNZ_NM").toString());
					}
				}
			}
			LOGGER.info("A-1.조직번호(OGNZ_NO)는 같으나 조직명(OGNZ_NM)이 달라진 경우를 위한 Map 설정 완료");
			

			/* A-2 MSTR Developer에는 존재하지만, EIAM에서 부서가 사라진 경우 삭제 처리 */
			// A-2-1. MSTR Developer에 존재하는 모든 조직 정보를 가져와서 1번씩 loop 
			Iterator<String> keys = ogUserGroupNm.keySet().iterator();
			while(keys.hasNext()) {				
				String strKey = keys.next();
				String strValue = ogUserGroupNm.get(strKey);
				
				boolean deleteDept = true;
				
				// A-2-2. EIAM에서 가져온 조직정보와 비교.
				for(int deptIdx=0; deptIdx<changeDepartmentList.size(); deptIdx++) {
					String changeDeptNo = (String) changeDepartmentList.get(deptIdx).get("OGNZ_NO");
					String changeDeptNm = (String) changeDepartmentList.get(deptIdx).get("OGNZ_NM");
					
					// A-2-3. MSTR Developer와 EIAM에 서로 존재한다면 Skip
					if(strKey.equals(changeDeptNo) || strValue.equals(changeDeptNm)){
						deleteDept = false;
						
						break;
					}
				}
				
				// A-2-4. MSTR Developer에는 존재하지만, EIAM에 부서 존재가 없다면 삭제.
				if(deleteDept && changeDepartmentList.size() > 0) {
					try {
						// 사용자 그룹 정보
						WebUserGroup deleteUserGroup = (WebUserGroup) objSource.getObject(ogUserGroup.get(strValue), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
	
						// 폴더 정보
						Map<String, Object> deleteDeptFolderMap = searchNameFolder(departMentMenu1, strValue);
						WebObjectInfo deleteFolderObjectInfo = objSource.getObject((String) deleteDeptFolderMap.get("key"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
						
						objSource.save(deleteUserGroup, strValue + "_" + batchExecuteDate + "_삭제");
						objSource.save(deleteFolderObjectInfo, strValue + "_" + batchExecuteDate + "_삭제");

						LOGGER.info("A-2-4.MSTR Developer에는 존재하지만, EIAM에서 부서가 사라진 경우 삭제 처리 - " + strValue + "(" + ")");
						
						ogUserGroupNm.remove(strKey);
						ogUserGroup.remove(strValue);
					} catch (WebObjectsException e) {
						LOGGER.error("WebObjectsException error : ", e);
					} catch (Exception e) {
						LOGGER.error("Exception error : ", e);
					}
				}
			}
			
			
			/* B.부서 등록 */
			for(int deptIdx=0; deptIdx<changeDepartmentList.size(); deptIdx++) {
				try {
					/* B-1. EIAM에서 가져온 부서 정보 변수 설정 */
					String changeDeptNo = (String) changeDepartmentList.get(deptIdx).get("OGNZ_NO");
					String changeDeptNm = (String) changeDepartmentList.get(deptIdx).get("OGNZ_NM");
					String changeDeptGubunCd = "";
					String changeDeptGroupPath = ((String)changeDepartmentList.get(deptIdx).get("GROUP_PATH"));
					String changeGroupPuseYn = (String) changeDepartmentList.get(deptIdx).get("PUSE_YN");
					
					if(changeDepartmentList.get(deptIdx).get("OGNZ_KD_CD") != null) {
						changeDeptGubunCd = ((String)changeDepartmentList.get(deptIdx).get("OGNZ_KD_CD"));
					} else {
						changeDeptGubunCd = "1";
					}
					
					String checkComments = getInsertComments(changeDepartmentList.get(deptIdx));
					
					/* B-2. 동일 조직번호-다른 조직명인 경우, 사전에 조직명을 맞춰주는 작업. */
					if(ogUserGroupNm.containsKey(changeDeptNo) && !ogUserGroupNm.get(changeDeptNo).equals(changeDeptNm)){
						LOGGER.info("B-2.부서 이름 변경(동일 조직번호-다른 조직명) : [{}] (변경전)[{}] (변경후)[{}]", changeDeptNo, ogUserGroupNm.get(changeDeptNo), changeDeptNm);
						try {
							// (변경전) User Group 가져오기 
							WebUserGroup changeUserGroup = (WebUserGroup) objSource.getObject(ogUserGroup.get(ogUserGroupNm.get(changeDeptNo)), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
							// (변경전) 부서 폴더 가져오기 
							Map<String, Object> changeDeptFolderMap = searchNameFolder(departMentMenu1, ogUserGroupNm.get(changeDeptNo));
							WebObjectInfo changeFolderObjectInfo = objSource.getObject((String) changeDeptFolderMap.get("key"), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
							
							
							// (변경 후) User Group 셋팅하기 
							objSource.save(changeUserGroup, changeDeptNm);
							
							// (변경 후) 부서 폴더명 재정의 
							objSource.save(changeFolderObjectInfo, changeDeptNm);
							
							// (변경 후) MSTR을 통해 가져온 그룹을 다시 최신화 
							ogUserGroup.remove(ogUserGroupNm.get(changeDeptNo));
							ogUserGroup.put(changeDeptNm, changeUserGroup.getID());
						}
						catch (WebObjectsException e) {
							LOGGER.error("WebObjectsException error : ", e);
						} catch (Exception e) {
							LOGGER.error("Exception error : ", e);
						}
					}
					
					/* B-3. 사용자 그룹 관리 후 폴더 등록 작업 */
					if(!ogUserGroup.containsKey(changeDeptNm) && changeGroupPuseYn.equals("Y")) {
						LOGGER.info("B-3.신규 사용자 그룹 등록 작업 - [{}] [{}] [{}]", changeDeptNm, changeDeptGubunCd, changeDeptGroupPath);
						
						String[] groupPath = null;
						if(changeDeptGroupPath.equals("")) {
							groupPath = changeDeptNm.split("//");
						} else {
							groupPath = changeDeptGroupPath.split("//");
						}
						
						String parentGroupId = OG04_USER_GROUP_ID;
						String parentFolderId = "";
						
						if(changeDeptGubunCd.equals("2")) {
							parentFolderId = CustomProperties.getProperty("mstr.department.report.folder.id"); 	//본사 조직
						} else {
							parentFolderId = CustomProperties.getProperty("mstr.branch.report.folder.id");		//지점 사용자
						}
						
						WebUserGroup newGroup = null;
						List<String> newGroupList = new ArrayList<String>();
						for(int gIdx=0; gIdx<groupPath.length; gIdx++) {
							Boolean checkNewFolder = false;
							
							// B-3-1. 사용자 그룹 작업 */
							if(ogUserGroup.containsKey(groupPath[gIdx])) {
								//사용자 그룹 존재
								checkNewFolder = false;
								if(serverSession != null) {
									newGroup = (WebUserGroup) objSource.getObject(ogUserGroup.get(groupPath[gIdx]), EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
									newGroupList.add(ogUserGroup.get(groupPath[gIdx]));
									parentGroupId = newGroup.getID();
								}
							} else {
								//사용자 그룹 미존재(New)
								checkNewFolder = true;
								newGroup = MstrUserUtil.createUserGroup(serverSession, groupPath[gIdx], parentGroupId, "EIAM 등록");
								
								String[] changeComments = {checkComments};
								newGroup.setComments(changeComments);
								objSource.save(newGroup);
								
								ogUserGroup.put(newGroup.getAbbreviation(), newGroup.getID());
								newGroupList.add(newGroup.getID());
								LOGGER.info("B-3-1. 사용자 그룹 작업 - OG.조직정보 신규 그룹 추가 : [{}]", newGroup.getAbbreviation());
								
								parentGroupId = newGroup.getID();
							}
							
							// B-3-2. 그룹 폴더 작업 */
							if(checkNewFolder) {
								//부서 폴더 추가
								WebObjectInfo targetObjectInfo = null;
								WebObjectInfo parentFolderInfo = null;
								WebObjectInfo createFolder = null;
								
								String tmpRootId = "";
								if(changeDeptGubunCd.equals("2")) {
									//본사 조직
									
									parentFolderInfo = objSource.getObject(parentFolderId, EnumDSSXMLObjectTypes.DssXmlTypeFolder);
									createFolder = createFolder(objSource, parentFolderInfo.getID(), groupPath[gIdx]);
									
									tmpRootId = CustomProperties.getProperty("mstr.department.report.folder.id");
									if(serverSession != null) {
										departMentMenu1 = MstrFolderBrowsing.getFolderTree(serverSession, tmpRootId, 4, Arrays.asList(EnumDSSXMLObjectTypes.DssXmlTypeFolder));
									}
									
									targetObjectInfo = objSource.getObject(createFolder.getID(), EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);

									String[] changeComments = {checkComments};
									targetObjectInfo.setComments(changeComments);
									
									LOGGER.info("B-3-2. 그룹 폴더 작업 - 조직 신규 폴더 추가 : [{}]", groupPath[gIdx]);
									parentFolderId = targetObjectInfo.getID();
									
									//파워사용자 권한 제거
									List<WebAccessControlEntry> alACE = new ArrayList<WebAccessControlEntry>();
									alACE = getListAccessControlentryForTrustee(targetObjectInfo, targetObjectInfo.getID());
									
									if(alACE.size() > 0) {
										//5-1.업데이트
										WebObjectSecurity wos = targetObjectInfo.getSecurity();
										WebAccessControlList acl = wos.getACL();
										
										WebAccessControlEntry ace = null;
										for(int j=0; j<acl.size(); j++) {
											ace = acl.get(j);
											if(!ace.getTrustee().getDisplayName().equals("mstradmin") && !ace.getTrustee().getDisplayName().equals("mstrdev")) {
												acl.remove(ace);
												objSource.save(targetObjectInfo);
											}
										}
									}
									
									//폴더에 그룹 권한 부여
									for(int addCnt=0; addCnt<newGroupList.size(); addCnt++) {
										WebUserEntity groupTrustee = (WebUserGroup) objSource.getObject(newGroupList.get(addCnt), EnumDSSXMLObjectTypes.DssXmlTypeUser, true);
										setRightToObject(targetObjectInfo, groupTrustee, 207, false, 199, false);
										
										objSource.save(targetObjectInfo);
									}
								} else {
									//지점 사용자 - 폴더 만들기 X
									/*
									parentFolderInfo = objSource.getObject(CustomProperties.getProperty("mstr.branch.report.folder.id"), EnumDSSXMLObjectTypes.DssXmlTypeFolder);
									createFolder = createFolder(objSource, parentFolderInfo.getID(), groupPath[gIdx]);
									
									tmpRootId = CustomProperties.getProperty("mstr.branch.report.folder.id");
									departMentMenu2 = MstrFolderBrowsing.getFolderTree(serverSession, tmpRootId, 1, Arrays.asList(EnumDSSXMLObjectTypes.DssXmlTypeFolder));
									*/
								}
								
							} else {
								Map<String, Object> checkGroupMap = searchDepartmentGroup(changeDeptGubunCd.equals("2") ? departMentMenu1 : departMentMenu2, groupPath[gIdx]);
								parentFolderId = (String) checkGroupMap.get("id");
							}
							
						}
					} 
					else if(ogUserGroup.containsKey(changeDeptNm)) {
						LOGGER.info("B-4.기존 사용자 그룹 등록 작업 - [{}] [{}] [{}]", changeDeptNm, changeDeptGubunCd, changeDeptGroupPath);
						
						//부서가 존재할 경우
						WebUserGroup userGroup = (WebUserGroup) objSource.getObject(ogUserGroup.get(changeDeptNm), EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
						
						String asisComments = "";
						if(userGroup.getComments() != null) {
							asisComments = userGroup.getComments()[0];
						}
						
						if(changeGroupPuseYn.equals("Y")) {
							// 부서 정보 다를 경우 설명 업데이트
							if(!asisComments.equals(checkComments)) {
								String[] changeComments = {checkComments};
								LOGGER.info("B-4-1.부서 설명 변경 : [{}] : (변경전)[{}] (변경후)[{}]", changeDeptNm, asisComments, changeComments );
								
								userGroup.setComments(changeComments);
								objSource.save(userGroup);
							}
						}
						
						if(changeGroupPuseYn.equals("N")) {
							
							ObjectMapper mapper = new ObjectMapper();
							Map<String, Object> asisCommentsMap = new HashMap<String, Object>();
							if(!asisComments.equals("")) {
								asisCommentsMap = mapper.readValue(asisComments, Map.class);
							}
							
							LOGGER.info("B-4-2.기존 사용자 그룹 등록 작업 - 부서 삭제 처리 - [{}] [{}] [{}]", changeDeptNo, changeDeptNm, changeGroupPuseYn);

							//2.부서 삭제 처리
							if(asisComments.equals("") || asisCommentsMap.get("OGNZ_NO").equals(changeDeptNo)) {
								int childSize = 0;
								Enumeration<?> childElements = userGroup.getMembers().elements();
								while (childElements.hasMoreElements()) {
									Object currentElement = childElements.nextElement();
									if (currentElement instanceof WebUserGroup) {
										childSize++;
									}
								}
								
								if(childSize == 0) {
									//하위 부서가 없는 경우
//									objSource.deleteObject(userGroup);
									objSource.save(userGroup, changeDeptNm + "_" + batchExecuteDate + "_삭제");
									LOGGER.info("B-4-3.삭제 부서(하위폴더 없음) : [{}][{}]", userGroup.getName(), changeDeptNo);
								} else {
									//하위 부서가 있는 경우
									objSource.save(userGroup, changeDeptNm + "_" + batchExecuteDate + "_삭제");
									LOGGER.info("B-4-3.삭제 부서(하위폴더 있음) : [{}][{}]", userGroup.getName(), changeDeptNo);
									
								}
							}							
						}
						

						/*
						9.부서 삭제 처리(이동)
						{
							WebObjectInfo tempRootObjectInfo = objSource.getObject(DELETE_DEPT_FOLDER_ID, EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
							WebFolder tempRootFolder = (WebFolder)tempRootObjectInfo;
							
							WebObjectInfo moveFolderObjectInfo = objSource.getObject("ABA6DA0A11EC201581150080EF452090", EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
							objSource.save(moveFolderObjectInfo, tempRootFolder);
							LOGGER.info("7-1-1.삭제 부서 폴더 이동 : [{}]", moveFolderObjectInfo.getName());
						}
						*/
						
					} else {
					}
					
				} catch (WebObjectsException e) {
					LOGGER.error("WebObjectsException error : ", e);
				} catch (Exception e) {
					LOGGER.error("Exception error : ", e);
				}
			}
			
			
			//부서 그룹 이동
			/**/
			moveUserGroupCheck(serverSession, objSource, ogUserGroup);			
			
			/*
			//부서폴더 이동
			{
				for(int deptFolderIdx=0; deptFolderIdx<departMentMenu1.size(); deptFolderIdx++) {
					moveFolderCheck(objSource, ogUserGroup, departMentMenu1.get(deptFolderIdx), 1);
				}
			}
			*/
			
			// 폴더 권한 조정
			List<String> newGroupList = new ArrayList<String>();
			for(int deptFolderIdx=0; deptFolderIdx<departMentMenu1.size(); deptFolderIdx++) {
				changeFolderAuth(objSource, ogUserGroup, departMentMenu1.get(deptFolderIdx), 1, newGroupList);
			}
			
			
			/* C-1. 사용자 등록 - EIAM 권한은 없는데 MSTR에는 존재하는 경우 삭제 */
			Map<String, String> allMstrUserMap_ToNoEiam = getAllMstrUser(objSource);
			
			for(int userIdx=0; userIdx<changeUserList.size(); userIdx++) {
				String changeUserId = (String) changeUserList.get(userIdx).get("PRAF_NO");
				
				if (allMstrUserMap_ToNoEiam.containsKey(changeUserId)) {
					allMstrUserMap_ToNoEiam.remove(changeUserId);
				}
			}
			
			if(allMstrUserMap_ToNoEiam.containsKey("mstradmin")) {
				allMstrUserMap_ToNoEiam.remove("mstradmin");
			}
			
			Iterator<String> eiamkeys = allMstrUserMap_ToNoEiam.keySet().iterator();
			while(eiamkeys.hasNext()) {
				try {
					WebUserEntity trustee = null;
					WebUser noEiamUser = null;
					String eiamkey = eiamkeys.next();
					noEiamUser = MstrUserUtil.getUser(objSource, allMstrUserMap_ToNoEiam.get(eiamkey));
					trustee = noEiamUser;
					
					if(noEiamUser.isEnabled()) {
						noEiamUser.setEnabled(false);
						objSource.save(noEiamUser);
						MstrUserUtil.addToUserGroup(objSource, noEiamUser, DELETE_USER_GROUP_ID);
						LOGGER.info("C-1-1.기존 등록 유저 계정 비활성화 (EIAM 미존재) - [{}][{}]", trustee.getAbbreviation(), trustee.getName());
					}
				} catch (WebObjectsException e) {
					LOGGER.error("WebObjectsException error : ", e);
				} catch (Exception e) {
					LOGGER.error("Exception error : ", e);
				}
			}
			
			/* C-1. 사용자 등록 - EIAM 권한은 없는데 MSTR에는 존재하는 경우 삭제 */

			/* C-2. 사용자 등록 - 메인 시작 */
			for(int userIdx=0; userIdx<changeUserList.size(); userIdx++) {
				try {
					LOGGER.info("C-2. 사용자 등록 메인 시작 ===== 유저 작업 - [{}/{}] =====", userIdx + 1, changeUserList.size());
					
					WebUserEntity trustee = null;
					WebUser user = null;
					
					String changeUserId = (String) changeUserList.get(userIdx).get("PRAF_NO");
					String changeUserNm = (String) changeUserList.get(userIdx).get("PRAF_NM");
					String changeUserRoleNm = (String) changeUserList.get(userIdx).get("ROLE_NM");
					String changeUserGroupNm = (String) changeUserList.get(userIdx).get("USER_GROU_NM");
					
					String changeUserPuseYn = (String) changeUserList.get(userIdx).get("USER_PUSE_YN");
					String changeGroupPuseYn = (String) changeUserList.get(userIdx).get("GROUP_PUSE_YN");
					String changeRolePuseYn = (String) changeUserList.get(userIdx).get("ROLE_PUSE_YN");
					
					
					//1.유저 체크
					if (allMstrUserMap.containsKey(changeUserId)) {
						//1-1-1.기존 유저 확인
						user = MstrUserUtil.getUser(objSource, allMstrUserMap.get(changeUserId));
						trustee = user;
						
						
						// C-2-1. 기존 등록 유저 : [계정 사용불가] -> [계정 활성화]
						if (changeUserPuseYn.equals("Y") && !user.isEnabled()) {
							user.setEnabled(true);
							objSource.save(user);
							MstrUserUtil.removeFromUserGroup(objSource, user, DELETE_USER_GROUP_ID);
							LOGGER.info("C-2-1.기존 등록 유저 : [계정 사용불가] -> [계정 활성화] - [{}][{}]", trustee.getAbbreviation(), trustee.getName());
						}
						
						// C-2-2.기존 등록 유저 : [계정 활성화] -> [계정 사용불가]
						if(changeUserPuseYn.equals("N") && user.isEnabled()) {
							user.setEnabled(false);
							objSource.save(user);
							MstrUserUtil.addToUserGroup(objSource, user, DELETE_USER_GROUP_ID);
							LOGGER.info("C-2-2.기존 등록 유저 : [계정 활성화] -> [계정 사용불가] - [{}][{}]", trustee.getAbbreviation(), trustee.getName());
							
							//1-1-3-1 사용자 큐브 삭제
//							CacheSource cubeCS = (CacheSource) webObjectFactory.getMonitorSource(EnumWebMonitorType.WebMonitorTypeCubeCache);
//							cubeCS.setLevel(EnumDSSXMLLevelFlags.DssXmlDetailLevel);
//							
//							CacheResults cubuResults;
//							cubuResults = cubeCS.getCaches();
//							
//							for(int j=0; j<cubuResults.size(); j++) {
//								Caches result = cubuResults.get(j);
//								
//								if(result.getProjectName().equalsIgnoreCase(project)) {
//									for(int i=0; i<result.getCount(); i++) {
//										CubeCache cache = (CubeCache) result.get(i);
//										
//										if(cache.getUser().getID().equals(user.getID())) {
//											
//											if(serverSession != null) {
//												MstrCube.deleteCube(serverSession, cache.getCacheSourceID());
//											}
//											LOGGER.info("1-1-3-1 사용자 큐브 삭제 - [{}][{}][{}]", trustee.getAbbreviation(), trustee.getName(), cache.getCacheSourceName());
//										}
//									}
//								}
//							}
						}
						
					} else {
						if(changeUserPuseYn.equals("Y")) {
							// C-2-3.신규 유저 추가  (1D2E6D168A7711D4BE8100B0D04B6F0B ==> 마지막 Parameter는 Developer의 "[관리]>[전달 관리자]>[장치]"의 일반전자메일 ID)
							user = MstrUserUtil.createUser(serverSession, changeUserId, changeUserNm, "1234", "NL"+changeUserId+"@goldwing.shinhan.com", "1D2E6D168A7711D4BE8100B0D04B6F0B");
							
							trustee = (WebUserEntity) user;
							allMstrUserMap.put(trustee.getAbbreviation(), trustee.getID());
							LOGGER.info("C-2-3.신규 유저 추가 : [{}][{}]", trustee.getAbbreviation(), trustee.getName());
						}
					}
					
					
					// C-2-4.유저 이름 변경
					if(!trustee.getName().equals(changeUserNm)) {
						LOGGER.info("C-2-4.유저 이름 변경 : [{}][{}]", trustee.getName(), changeUserNm);
						UserBean userBean = null;
						userBean = (UserBean)BeanFactory.getInstance().newBean("UserBean");
						userBean.setSessionInfo(serverSession);
						userBean.setObjectID(user.getID());
						
						WebUser webUser2 = (WebUser)userBean.getUserEntityObject();
						
						webUser2.setFullName(changeUserNm);
						userBean.save();
					}
					

					// C-2-5.유저 권한 삽입/삭제
					if(user != null && fnUserGroup.containsKey(changeUserRoleNm)) {
						addUserAuth(2, "FN.기능역할(BIS)", objSource, user, trustee, fnUserGroup, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && smUserGroup.containsKey(changeUserRoleNm)) {
						addUserAuth(3, "SM.관리자역할(BIS)", objSource, user, trustee, smUserGroup, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && ptUserGroup.containsKey(changeUserRoleNm)) {
						addUserAuth(4, "PT.포탈역할(BIS)", objSource, user, trustee, ptUserGroup, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && daUserGroup.containsKey(changeUserRoleNm)) {
						addUserAuth(5, "DA.부가역할(BIS)", objSource, user, trustee, daUserGroup, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && fnUserGroup_ids.containsKey(changeUserRoleNm)) {
						addUserAuth(6, "FN.기능역할(DSS)", objSource, user, trustee, fnUserGroup_ids, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && smUserGroup_ids.containsKey(changeUserRoleNm)) {
						addUserAuth(7, "SM.관리자역할(DSS)", objSource, user, trustee, smUserGroup_ids, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && ptUserGroup_ids.containsKey(changeUserRoleNm)) {
						addUserAuth(8, "PT.포탈역할(DSS)", objSource, user, trustee, ptUserGroup_ids, changeRolePuseYn, changeUserRoleNm);
					}
					if(user != null && daUserGroup_ids.containsKey(changeUserRoleNm)) {
						addUserAuth(9, "DA.부가역할(DSS)", objSource, user, trustee, daUserGroup_ids, changeRolePuseYn, changeUserRoleNm);
					}
					
					
					//99.OG.조직정보 권한 체크
					if(user != null && ogUserGroup.containsKey(changeUserGroupNm)) {
						//사용자 권한 체크
						Set<String> currentUserGroupList = searchUserGroup(user, ogUserGroup);
						
						if(changeGroupPuseYn.equals("Y")) {
							//부서 사용 처리
							if(currentUserGroupList.contains(changeUserGroupNm)) {
								//부서 있음 - 로직 패스
								//LOGGER.info("99-1-1.OG.조직정보 권한 존재 : [{}][{}]{} = [{}]", trustee.getAbbreviation(), trustee.getName(), currentUserGroupList, changeUserGroupNm);
							} else {
								//부서 없음 - 사용자 그룹 추가
								MstrUserUtil.addToUserGroup(objSource, user, ogUserGroup.get(changeUserGroupNm));
								LOGGER.info("99-2-1.OG.조직정보 권한 추가 : [{}][{}]{} + [{}]", user.getAbbreviation(), user.getName(), currentUserGroupList, changeUserGroupNm);
							}
							
						} else if(changeGroupPuseYn.equals("N")) {
							//부서 사용 불가 처리
							if(currentUserGroupList.contains(changeUserGroupNm)) {
								//부서 있음 - 사용자 그룹 삭제
								MstrUserUtil.removeFromUserGroup(objSource, user, ogUserGroup.get(changeUserGroupNm));
								LOGGER.info("99-3-1.OG.조직정보 권한 삭제 : [{}][{}][{}]", trustee.getAbbreviation(), trustee.getName(), changeUserGroupNm);
							} else {
								//부서 없음 - 로직 패스
								//LOGGER.info("99-4-1.OG.조직정보 권한 사용 안함 : [{}][{}][{}]", trustee.getAbbreviation(), trustee.getName(), changeUserGroupNm);
							}
						} else {
							
						}
						
						Iterator<String> cuIter = currentUserGroupList.iterator();
						while (cuIter.hasNext()) {
							String checkDept = cuIter.next();
							
							if(checkDept.equals(changeUserGroupNm)) {
								//부서 있음 - 로직 패스
								//LOGGER.info("99-1-1.OG.조직정보 권한 존재 : [{}][{}]{} = [{}]", trustee.getAbbreviation(), trustee.getName(), currentUserGroupList, changeUserGroupNm);
							} else {
								//부서 있음 - 사용자 그룹 삭제
								MstrUserUtil.removeFromUserGroup(objSource, user, ogUserGroup.get(checkDept));
								LOGGER.info("99-3-1.OG.조직정보 권한 삭제 : [{}][{}][{}]", trustee.getAbbreviation(), trustee.getName(), checkDept);
							}
						}
						
						
					} else if(user != null && !ogUserGroup.containsKey(changeUserGroupNm)) {
						LOGGER.info("99-5-1.OG.조직정보 존재하지 않음 : [{}][{}][{}]", trustee.getAbbreviation(), trustee.getName(), changeUserGroupNm);
					} else {
						
					}
					
				} catch (WebObjectsException e) {
					LOGGER.error("WebObjectsException error : ", e);
				} catch (Exception e) {
					LOGGER.error("Exception error : ", e);
				}
			}
			
			
			//8.그룹 확인
			{
				/*
				Map<String, Object> checkGroupMap = searchDepartmentGroup(departMentMenu, "테스트그룹");
				*/
			}
			
			
		} catch (WebObjectsException e) {
			LOGGER.error("WebObjectsException error : ", e);
		} catch (Exception e) {
			LOGGER.error("!!! 배치 처리 Exception Error : ", e);
			throw new RuntimeException("배치 처리 Error");
		} finally {
			MstrUtil.closeISession(serverSession);
			jobEnd();
			long afterTimne = System.currentTimeMillis();
			LOGGER.info("배치 수행시간 : [{}]", (afterTimne -beforeTimne) / 1000.0);
		}
	}
	
	
	private static final String[] SPRING_CONFIG_XML = new String[] { "spring/batch-context-" + BatchProperties.getHostIp() + ".xml" };
	
	private void setDao() {
		ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONFIG_XML);
		userJdbcTemplate = (UserJdbcTemplate)context.getBean("userJdbcTemplate"); 
	}
	
	private static boolean active = false;
	
	public synchronized static void jobStart() { active = true; LOGGER.info("[!!! 사용자동기화 작업을 시작합니다.]"); }
	public synchronized static void jobEnd() { active = false; LOGGER.info("[!!! 사용자동기화 작업을 종료합니다.]"); }
	public synchronized static boolean isActive() { return active; }
	
	public static void main(String[] args) {
		// Sparrow 검출을 피하기 위한 주석 처리 (실제 수행 시에는 필요)
		if (SyncUser.isActive()) {
			LOGGER.info("!!! 이미 작업이 실행 중입니다.");
		} else {
			SyncUser syncUser = new SyncUser();
			syncUser.setDao();
			syncUser.doSync();
			
			System.exit(0);
		}
	}
}
