package com.batch.util;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

/**
 * SyncUserUtil
 * @author mococo
 *
 */
public class SyncUserUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(SyncUserUtil.class);
	
	
    /**
     * SyncUserUtil
     */
    public SyncUserUtil() {
    	logger.debug("SyncUserUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("SyncUserUtil");
    }
	
	
	/**
	 * MSTR 하위 그룹 확인
	 * @param source
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> searchGroup(final WebObjectSource source, final String groupId) {
		final Map<String, Map<String, ?>> map = getSubUserGroup(source, groupId);
		return (Map<String, String>)map.get("mstrGroupMap");
	}
	
	
	/**
	 * getSubUserGroup
	 * @param source
	 * @param baseMstrGroupId
	 * @return
	 */
	public static Map<String, Map<String, ?>> getSubUserGroup(final WebObjectSource source, final String baseMstrGroupId) {
		final Map<String, String> mstrGroupMap = new ConcurrentHashMap<>();
		final Map<String, String> mstrUserMap = new ConcurrentHashMap<>();
		final Map<String, Set<String>> mstrGroupOfUser = new ConcurrentHashMap<>();
		
		try {
			final WebUserGroup baseGroup = (WebUserGroup)source.getObject(baseMstrGroupId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
			
			Enumeration<?> currentElements = baseGroup.getMembers().elements();
			final LinkedList<Enumeration<?>> stack = new LinkedList<>();
			
			while (true) {
				if (!currentElements.hasMoreElements()) {
					while (!stack.isEmpty()) {
						currentElements = stack.pop();
						if (currentElements.hasMoreElements()) { break; }
					}
				}
				
				if (stack.isEmpty() && !currentElements.hasMoreElements()) { break; }
				
				final Object element = currentElements.nextElement();
				if (element instanceof WebUserGroup) {
					final WebUserGroup group = (WebUserGroup)element;
					group.populate();
					
					stack.push(currentElements);
					currentElements = group.getMembers().elements();
					
					final Set<String> userIdSet = new HashSet<>();
					for (final Enumeration<?> e = group.getMembers().elements(); e.hasMoreElements(); ) {
						final Object object = e.nextElement();
						if (object instanceof WebUser) {
							userIdSet.add(((WebUser)object).getAbbreviation());
						}
					}
					
					mstrGroupOfUser.put(group.getDisplayName(), userIdSet);
					mstrGroupMap.put(group.getDisplayName(), group.getID());
				} else if (element instanceof WebUser) {
					final WebUser user = (WebUser)element;

					if (!mstrUserMap.containsKey(user.getAbbreviation())) {
						mstrUserMap.put(user.getAbbreviation(), user.getID()); 
					}
				}
			}
		} catch (WebObjectsException | IllegalArgumentException e) {
			logger.error("!!! error", e);
		}
		
		final Map<String, Map<String, ?>> result = new ConcurrentHashMap<>();
		result.put("mstrGroupMap", mstrGroupMap);
		result.put("mstrUserMap", mstrUserMap);
		result.put("mstrGroupOfUser", mstrGroupOfUser);		
		
		return result;
	}
	
	
	/**
	 * 사용자 그룹 확인
	 * @param user
	 * @param searchGroup
	 * @return
	 * @throws WebObjectsException
	 */
	public static Map<String, String> searchUserGroup(final WebUser user, final Map<String, String> searchGroup) throws WebObjectsException {
		final Map<String, String> result = new ConcurrentHashMap<>();
		final List<WebUserGroup> groupNameList = MstrUserUtil.getUserGroupList(user);
		
//		for(int i=0; i<groupNameList.size(); i++) {
		for(final WebUserGroup group : groupNameList) {
			final String groupId = group.getID();
			final String groupName = group.getName();
			
			if (searchGroup.containsKey(groupName)) { // 기준 사용자그룹 하위의 사용자그룹만을 비교대상으로 한다.
				result.put(groupName, groupId);
			}
		}
		
		/*
		if(result.size() == 0) {
			result.put("권한 없음", "NO_AUTH");
		}
		*/
		
		return result;
	}
	
	
	/**
	 * MSTR 전체 사용자
	 * @param source
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getAllMstrUser(final WebObjectSource source) throws WebObjectsException {
		final Map<String, String> allMstrUserMap = new ConcurrentHashMap<>();
		
		final WebUserGroup group = MstrUserUtil.searchUserGroup(MstrUserUtil.getUserGroupSearch(source), "Everyone");
		WebUserList userList = null;
		if(group != null) {
			userList = group.getMembers();
		}
		
		for (final Enumeration<WebUser> e = userList.elements(); e.hasMoreElements();) {
			final WebUser user = e.nextElement();
			allMstrUserMap.put(user.getAbbreviation(), user.getID());
		}
		
		return allMstrUserMap;
	}
	
}
