/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 사용자 연동 배치 유틸 함수
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.batch.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microstrategy.web.objects.WebAccessControlEntry;
import com.microstrategy.web.objects.WebAccessControlList;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.web.objects.admin.users.WebUserList;
import com.microstrategy.webapi.EnumDSSXMLAccessEntryType;
import com.microstrategy.webapi.EnumDSSXMLAccessRightFlags;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.microstrategy.webapi.EnumDSSXMLObjectSubTypes;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.web.util.CustomProperties;

public class SyncUserUtil {
	private static final Logger LOGGER = LogManager.getLogger(SyncUserUtil.class);
	
/***************************************************************************
 비지니스로직 관련 부분
 **************************************************************************/
	public static final String EMP_NO_KEY = "직원번호";
	public static final String EMP_NM_KEY = "한글직원명";
	public static final String DEPT_CD_KEY = "인사부점구분코드";
	public static final String DEPT_NM_KEY = "한글부점명";
	public static final String ROLE_CD_KEY = "사용자권한관리업무역할내용";
	public static final String CHANGE_TYPE = "변경구분";
	
	public static final String MOBILE_ROLE_NM = "사용자권한관리";
	public static final String MOBILE_ROLE_ID = "사용자권한관리ID";
	
	
	/**
	 * <pre>
	 * 목적 : 변경할 그룹 찾기
	 * 매개변수 : 
	 * 	HttpServletRequest request
	 * 	Map&lt;String, Object&gt; param
	 * 반환값 : java.util.List : List&lt;Map&lt;String, Object&gt;&gt;
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static Set<String> getTargetGroupNamePattern(Map<String, Object> dbChangeUser, Map<String, String> roleIdMap) {
		Set<String> patterns = new HashSet<String>();
		
		// 역할에 의한 사용자 그룹 산출
		for (String key : roleIdMap.keySet()) {
			if (containRoleId(key, (String)dbChangeUser.get(ROLE_CD_KEY))) {
				patterns.add(roleIdMap.get(key));
			}
		}
		
		// 부점명에 의한 사용자 그룹 산출
		patterns.add((String)dbChangeUser.get(DEPT_NM_KEY));
		
		return patterns;
	}
	
	public static boolean isPowerUser(Map<String, Object> dbChangeUser) {
		return containRoleId("EWBC0015", (String)dbChangeUser.get(ROLE_CD_KEY));
	}
	
	public static boolean containRoleId(String roleId, String dbRoleIdStr) {
		return StringUtils.isNotEmpty(dbRoleIdStr) && dbRoleIdStr.matches("^.*\\|{0,1}" + roleId + "\\|{0,1}.*$");
	}
	
	public static Map<String, String> getRoleIdMap() {
		String roleIdMapStr = CustomProperties.getProperty("user.role.id.map.list");
		
		Map<String, String> roleIdMap = new HashMap<String, String>();
		
		if (StringUtils.isNotEmpty(roleIdMapStr)) {
			String[] tokens = roleIdMapStr.split(";");
			
			for (String token : tokens) {
				String[] each = token.split(",");
				if (each.length == 3) {
					roleIdMap.put(each[0], each[1]);
				}
			}
		}
		
		return roleIdMap;
	}
	
	public static Map<String, Long> getRoleIdFactorMap() {
		String roleIdMapStr = CustomProperties.getProperty("user.role.id.map.list");
		Map<String, Long> roleIdFactorMap = new HashMap<String, Long>();
		
		if (StringUtils.isNotEmpty(roleIdMapStr)) {
			String[] tokens = roleIdMapStr.split(";");
			
			for (String token : tokens) {
				String[] each = token.split(",");
				if (each.length == 3) {
					roleIdFactorMap.put(each[0], Long.parseLong(each[2]));
				}
			}
		}
		
		return roleIdFactorMap;
	}	
	
/***************************************************************************
 MSTR 기능 관련 부분
 **************************************************************************/
	public static Set<String> getRemoveGroupIdSet(Set<String> oldGroupSet, Set<String> newGroupSet) {
		Set<String> result = new HashSet<String>(oldGroupSet);
		
		result.removeAll(newGroupSet);
		
		return result;
	}
	
	public static Set<String> getAddGroupIdSet(Set<String> oldGroupSet, Set<String> newGroupSet) {
		Set<String> result = new HashSet<String>(newGroupSet);
		
		result.removeAll(oldGroupSet);
		
		return result;
	}
	
	
	public static Map<String, Map<String, ?>> getSubUserGroup(WebObjectSource source, String baseMstrGroupId) {
		Map<String, String> mstrGroupMap = new HashMap<String, String>();
		Map<String, String> mstrUserMap = new HashMap<String, String>();
		HashMap<String, Set<String>> mstrGroupOfUser = new HashMap<String, Set<String>>();
		
		try {
			WebUserGroup baseGroup = (WebUserGroup)source.getObject(baseMstrGroupId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
			
			Enumeration<?> currentElements = baseGroup.getMembers().elements();
			LinkedList<Enumeration<?>> stack = new LinkedList<Enumeration<?>>();
			
			while (true) {
				if (!currentElements.hasMoreElements()) {
					while (!stack.isEmpty()) {
						currentElements = stack.pop();
						if (currentElements.hasMoreElements()) { break; }
					}
				}
				
				if (stack.isEmpty() && !currentElements.hasMoreElements()) { break; }
				
				Object element = currentElements.nextElement();
				if (element instanceof WebUserGroup) {
					WebUserGroup group = (WebUserGroup)element;
					group.populate();
					
					stack.push(currentElements);
					currentElements = group.getMembers().elements();
					
					Set<String> userIdSet = new HashSet<String>();
					for (Enumeration<?> e = group.getMembers().elements(); e.hasMoreElements(); ) {
						Object object = e.nextElement();
						if (object instanceof WebUser) {
							userIdSet.add(((WebUser)object).getAbbreviation());
						}
					}
					mstrGroupOfUser.put(group.getDisplayName(), userIdSet);
					
					mstrGroupMap.put(group.getDisplayName(), group.getID());
				} else if (element instanceof WebUser) {
					WebUser user = (WebUser)element;

					if (!mstrUserMap.containsKey(user.getAbbreviation())) {
						mstrUserMap.put(user.getAbbreviation(), user.getID()); 
					}
				} else {
					
				}
			}
		} catch (WebObjectsException | IllegalArgumentException e) {
			LOGGER.error("!!! error", e);
		}
		
		Map<String, Map<String, ?>> result = new HashMap<String, Map<String, ?>>();
		result.put("mstrGroupMap", mstrGroupMap);
		result.put("mstrUserMap", mstrUserMap);
		result.put("mstrGroupOfUser", mstrGroupOfUser);		
		
		return result;
	}
	
	public static String getEmailDeviceId() {
		return CustomProperties.getProperty("mstr.default.email.device.id");
	}

	private static String getAccessEntryType(int type) {
		String result = "unknown";
		
		switch (type) {
		case EnumDSSXMLAccessEntryType.DssAccessEntryTypeAudit: result = "audit"; break;
		case EnumDSSXMLAccessEntryType.DssAccessEntryTypeObject: result = "object"; break;
		case EnumDSSXMLAccessEntryType.DssAccessEntryTypeReserved: result = "reserved"; break;
		}
		return result;
	}
	
	private static String getAccessRightsStatus(Set<Integer> rights) {
		StringBuffer result = new StringBuffer();
		
		if (rights != null) {  
			for (Integer right : rights) {
				result.append( StringUtils.isEmpty(result.toString()) ? "" : "," );
				
				switch (right) {
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightFullControl :
					result.append("fullControl");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse : 
					result.append("browse");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightControl : 
					result.append("right");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightDelete :
					result.append("delete");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute :
					result.append("execute");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightInheritable :
					result.append("Inheritable");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead :
					result.append("read");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse :
					result.append("use");
					break;
				case EnumDSSXMLAccessRightFlags.DssXmlAccessRightWrite :
					result.append("wirte");
					break;
				}
			}
		}
		
		return result.toString();
	}
	
	private static Set<Integer> getAccessRights(int rights) {
		Set<Integer> result = new HashSet<Integer>();
		
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightFullControl & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightFullControl) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightFullControl);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightControl & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightControl) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightControl);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightDelete & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightDelete) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightDelete);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightInheritable & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightInheritable) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightInheritable);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse);
		}
		if ((EnumDSSXMLAccessRightFlags.DssXmlAccessRightWrite & rights) == EnumDSSXMLAccessRightFlags.DssXmlAccessRightWrite) {
			result.add(EnumDSSXMLAccessRightFlags.DssXmlAccessRightWrite);
		}
		
		return result;
	}
	
	private static void showRights(WebAccessControlEntry entry) {
		LOGGER.info(
			String.format("=> trustee:[%s], type:[%s], grant:[%s], rights:[%s]", 
					entry.getTrustee().getName(), 
					getAccessEntryType(entry.getType()),
					entry.isAccessDenied() ? "denied" : "granted",
					getAccessRightsStatus(getAccessRights(entry.getRights()))
			)
		);
	}
	
	private static WebAccessControlEntry getEveryoneAccessControlEntry(WebObjectInfo info) {
		WebAccessControlEntry target = null;
		
		try {
			WebAccessControlList acl = info.getSecurity().getACL();
			Enumeration<?> e = acl.elements();
			while (e.hasMoreElements()) {
				WebAccessControlEntry entry = (WebAccessControlEntry)e.nextElement();
				
				if (StringUtils.equalsIgnoreCase(entry.getTrustee().getName(), "everyone") && entry.getType() == EnumDSSXMLAccessEntryType.DssAccessEntryTypeObject) {
					target = entry;
					break;
				}
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("!!! error", e);
		}
		
		return target;
	}
	
	public static void controlRights(WebIServerSession session, WebUser user, boolean isAllow) {
		WebObjectSource source = session.getFactory().getObjectSource();
		
		try {
			WebAccessControlEntry entry = getEveryoneAccessControlEntry(user);
			
//			Set<Integer> rights = getAccessRights(entry.getRights());
			Set<Integer> rights = null;
			if(entry != null) {
				showRights(entry);
				rights = getAccessRights(entry.getRights());
			}
			
			LOGGER.info("=> 현재 권한 : [{}]", getAccessRightsStatus(rights));
			
			if (isAllow) {
				if (
					!rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute) || 
					!rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse) ||
					!rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead) ||
					!rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse)
				) {
					if(entry != null) {
						entry.setRights(
								EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute | EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse | EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead | EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse 
								);
						source.setFlags(user.getFlags() & ~EnumDSSXMLObjectFlags.DssXmlObjectDefn);
						source.save(user);
					}
				}
			} else {
				if (
					rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightExecute) || 
					rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightUse) ||
					rights.contains(EnumDSSXMLAccessRightFlags.DssXmlAccessRightFullControl)
				) {
					if(entry != null) {
						entry.setRights(
								EnumDSSXMLAccessRightFlags.DssXmlAccessRightRead | EnumDSSXMLAccessRightFlags.DssXmlAccessRightBrowse 
								);
						source.setFlags(user.getFlags() & ~EnumDSSXMLObjectFlags.DssXmlObjectDefn);
						source.save(user);
					}
				}
			}
			
			if(entry != null) {
				LOGGER.info("=> 변경 후 권한 : [{}]", getAccessRightsStatus(getAccessRights(entry.getRights())));
			}

		} catch (WebObjectsException e) {
			LOGGER.error("!!! error", e);
		}
	}
	
	
	//MSTR 하위 그룹 확인
	@SuppressWarnings("unchecked")
	public static Map<String, String> searchGroup(WebObjectSource source, String groupId) {
		Map<String, Map<String, ?>> map = getSubUserGroup(source, groupId);
		Map<String, String> rtnMap = (Map<String, String>)map.get("mstrGroupMap");
		
		return rtnMap;
	}
	
	
	//사용자 그룹 확인
	public static Map<String, String> searchUserGroup(WebUser user, Map<String, String> searchGroup) throws WebObjectsException {
		Map<String, String> result = new HashMap<String, String>();
		List<WebUserGroup> groupNameList = MstrUserUtil.getUserGroupList(user);
		
		for(int i=0; i<groupNameList.size(); i++) {
			String groupId = groupNameList.get(i).getID();
			String groupName = groupNameList.get(i).getName();
			
			if (searchGroup.containsKey(groupName)) { // 기준 사용자그룹 하위의 사용자그룹만을 비교대상으로 한다.
//				System.out.println("groupNameList : " + groupNameList.get(i));
				result.put(groupName, groupId);
			}
		}
		
		if(result.size() == 0) {
//			result.add("권한 없음");
		}
		
		return result;
	}

	
	//MSTR 전체 사용자
	@SuppressWarnings("unchecked")
	public static Map<String, String> getAllMstrUser(WebObjectSource source) throws WebObjectsException {
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
}
