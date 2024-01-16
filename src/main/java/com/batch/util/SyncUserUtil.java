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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.web.objects.admin.users.WebUserList;
import com.microstrategy.webapi.EnumDSSXMLObjectSubTypes;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;

public class SyncUserUtil {
	private static final Logger LOGGER = LogManager.getLogger(SyncUserUtil.class);
	
	
	//MSTR 하위 그룹 확인
	@SuppressWarnings("unchecked")
	public static Map<String, String> searchGroup(WebObjectSource source, String groupId) {
		Map<String, Map<String, ?>> map = getSubUserGroup(source, groupId);
		Map<String, String> rtnMap = (Map<String, String>)map.get("mstrGroupMap");
		
		return rtnMap;
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
