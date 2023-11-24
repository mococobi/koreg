package com.mococo.microstrategy.sdk.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.UserBean;
import com.microstrategy.web.beans.UserGroupBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebSearch;
import com.microstrategy.web.objects.WebSubscriptionAddress;
import com.microstrategy.web.objects.admin.users.WebSimpleSecurityPluginLoginInfo;
import com.microstrategy.web.objects.admin.users.WebStandardLoginInfo;
import com.microstrategy.web.objects.admin.users.WebSubscriptionUserAddresses;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.web.objects.admin.users.WebUserList;
import com.microstrategy.webapi.EnumDSSXMLObjectSubTypes;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLSearchDomain;
import com.microstrategy.webapi.EnumDSSXMLSearchFlags;
import com.microstrategy.webapi.EnumDSSXMLSubscriptionDeliveryType;

public class MstrUserUtil {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(MstrUserUtil.class);

    /**
     * 사용자 생성
     * 
     * @param session
     * @param userId
     * @param name
     * @throws WebObjectsException
     * @throws WebBeanException
     */
    public static final WebUser createUser(WebIServerSession session, String userId, String name, String pwd)
            throws WebObjectsException, WebBeanException {
        UserBean userBean = (UserBean) BeanFactory.getInstance().newBean("UserBean");
        userBean.setSessionInfo(session);
        userBean.InitAsNew();

        WebUser webUser = (WebUser) userBean.getUserEntityObject();

        webUser.setLoginName(userId);
        webUser.setFullName(name);

        WebStandardLoginInfo standardLoginInfo = webUser.getStandardLoginInfo();
        standardLoginInfo.setPassword(pwd);

        WebSimpleSecurityPluginLoginInfo securityLoginInfo = webUser.getSimpleSecurityPluginLoginInfo();
        securityLoginInfo.setUid(userId);

        userBean.save();

        return webUser;
    }

    /**
     * 사용자를 추가하고 메일정보를 추가
     * 
     * @param session
     * @param userId
     * @param name
     * @param pwd
     * @param email
     * @param deviceId
     * @return
     * @throws WebObjectsException
     * @throws WebBeanException
     */
    public static final WebUser createUserWithMail(WebIServerSession session, String userId, String name, String pwd,
            String email, String deviceId) throws WebObjectsException, WebBeanException {
        UserBean userBean = (UserBean) BeanFactory.getInstance().newBean("UserBean");
        userBean.setSessionInfo(session);
        userBean.InitAsNew();

        WebUser webUser = (WebUser) userBean.getUserEntityObject();

        webUser.setLoginName(userId);
        webUser.setFullName(name);

        WebStandardLoginInfo standardLoginInfo = webUser.getStandardLoginInfo();
        standardLoginInfo.setPassword(pwd);

        WebSimpleSecurityPluginLoginInfo securityLoginInfo = webUser.getSimpleSecurityPluginLoginInfo();
        securityLoginInfo.setUid(userId);

        if (StringUtils.isNotEmpty(email)) {
            webUser.populate();

            WebSubscriptionUserAddresses addresses = webUser.getAddresses();
            WebSubscriptionAddress address = addresses
                    .addNewAddress(EnumDSSXMLSubscriptionDeliveryType.DssXmlDeliveryTypeEmail);
            address.setName(name + "-email");
            address.setValue(email);
            address.setDevice(deviceId); // MSTR '일반 전자 메일' 객체는 '1D2E6D168A7711D4BE8100B0D04B6F0B'
            address.save();
            addresses.saveAddress(address);
        }

        userBean.save();

        return webUser;
    }

    /**
     * 사용자그룹을 생성
     * 
     * @param session
     * @param userGroupName
     * @param parentObjectId
     * @return
     * @throws WebBeanException
     * @throws WebObjectsException
     */
    public static final WebUserGroup createUserGroup(WebIServerSession session, String userGroupName,
            String parentObjectId) throws WebBeanException, WebObjectsException {
        return createUserGroup(session, userGroupName, parentObjectId, null);
    }

    /**
     * 사용자그룹을 생성하고 주석을 설정
     * 
     * @param session
     * @param userGroupName
     * @param parentObjectId
     * @param desc
     * @return
     * @throws WebBeanException
     * @throws WebObjectsException
     */
    public static final WebUserGroup createUserGroup(WebIServerSession session, String userGroupName,
            String parentObjectId, String desc) throws WebBeanException, WebObjectsException {
        WebSearch search = getUserGroupSearch(session.getFactory().getObjectSource());
        WebUserGroup webUserGroup = searchUserGroup(search, userGroupName);

        if (webUserGroup == null) {
            UserGroupBean group = (UserGroupBean) BeanFactory.getInstance().newBean("UserGroupBean");
            group.setSessionInfo(session);
            group.InitAsNew();
            group.getUserEntityObject().setFullName(userGroupName);
            group.getUserEntityObject().setDescription(desc);

            UserGroupBean parentGroup = (UserGroupBean) BeanFactory.getInstance().newBean("UserGroupBean");
            parentGroup.setSessionInfo(session);
            parentGroup.setObjectID(parentObjectId);
            group.getParentGroups().add(parentGroup);

            group.save();
            webUserGroup = (WebUserGroup) group.getUserEntityObject();
        }

        return webUserGroup;
    }

    /**
     * 사용자 비활성화
     * 
     * @param objectSource
     * @param userId
     * @return
     * @throws WebObjectsException
     */
    public static final WebObjectInfo disableUser(WebObjectSource objectSource, String userId)
            throws WebObjectsException {
        WebUser user = searchUser(objectSource, userId);

        if (user != null) {
            user.setEnabled(false);
            WebObjectInfo object = objectSource.save(user);
            return object;
        } else {
            return null;
        }
    }

    /**
     * 사용자그룹 오브젝트ID로 사용자그룹 객체를 반환
     * 
     * @param objectSource
     * @param userGroupObjectId
     * @return
     * @throws WebObjectsException
     */
    public static final WebUserGroup getUserGroup(WebObjectSource objectSource, String userGroupObjectId)
            throws WebObjectsException {
        WebUserGroup user = (WebUserGroup) objectSource.getObject(userGroupObjectId,
                EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);

        return user;
    }

    /**
     * 파라미터로 전달된 사용자가 소속된 사용자그룹들의 오브젝트ID 리스트를 반환
     * 
     * @param user
     * @return
     */
    public static final List<String> getUserGroupIdList(WebUser user) {
        List<String> list = new ArrayList<String>();

        if (user != null) {
            WebUserList userList = user.getParents();
            for (Enumeration<WebObjectInfo> e = userList.elements(); e.hasMoreElements();) {
                final WebObjectInfo info = e.nextElement();

                if (info.getSubType() != EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup) {
                    continue;
                }

                list.add(info.getID());
            }
        }

        return list;
    }

    /**
     * 파라미터로 전달된 사용자가 소속된 사용자그룹들의 표시명 리스트를 반환
     * 
     * @param user
     * @return
     */
    public static final List<String> getUserGroupNameList(WebUser user) {
        List<String> list = new ArrayList<String>();

        if (user != null) {
            WebUserList userList = user.getParents();
            for (Enumeration<WebObjectInfo> e = userList.elements(); e.hasMoreElements();) {
                final WebObjectInfo info = e.nextElement();

                if (info.getSubType() != EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup) {
                    continue;
                }

                list.add(info.getDisplayName());
            }
        }

        return list;
    }

    /**
     * 파라미터로 전달된 사용자가 소속된 사용자그룹들의 객체 리스트를 반환
     * 
     * @param user
     * @return
     */
    public static final List<WebUserGroup> getUserGroupList(WebUser user) {
        List<WebUserGroup> list = new ArrayList<WebUserGroup>();

        if (user != null) {
            WebUserList userList = user.getParents();
            for (Enumeration<WebObjectInfo> e = userList.elements(); e.hasMoreElements();) {
                final WebObjectInfo info = e.nextElement();

                if (info.getSubType() != EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup) {
                    continue;
                }

                list.add((WebUserGroup) info);
            }
        }

        return list;
    }

    /**
     * 파라미터로 전달된 사용자를 사용자그룹에 추가
     * 
     * @param objectSource
     * @param user
     * @param userGroupObjectId
     * @return
     * @throws WebObjectsException
     */
    public static final void addToUserGroup(WebObjectSource objectSource, WebUser user, String userGroupObjectId)
            throws WebObjectsException {
        WebUserGroup userGroup = (WebUserGroup) objectSource.getObject(userGroupObjectId,
                EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
        userGroup.getMembers().add(user);
        objectSource.save(userGroup);
    }

    /**
     * 파라미터로 전달된 사용자를 사용자그룹에서 삭제
     * 
     * @param objectSource
     * @param user
     * @param userGroupObjectId
     * @return
     * @throws WebObjectsException
     */
    public static final void removeFromUserGroup(WebObjectSource objectSource, WebUser user, String userGroupObjectId)
            throws WebObjectsException {
        WebUserGroup userGroup = (WebUserGroup) objectSource.getObject(userGroupObjectId,
                EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
        userGroup.getMembers().remove(user);
        objectSource.save(userGroup);
    }

    /**
     * 사용자 비밀번호를 변경
     * 
     * @param session
     * @param userId
     * @param password
     * @return
     * @throws WebObjectsException
     */
    public static final void changeUserPassword(WebIServerSession session, String userId, String password)
            throws WebObjectsException {
        WebObjectSource objectSource = session.getFactory().getObjectSource();
        WebUser user = searchUser(objectSource, userId);
        WebStandardLoginInfo standardLoginInfo = user.getStandardLoginInfo();
        standardLoginInfo.setPassword(password);
        objectSource.save(user);
    }

    /**
     * 파라미터로 전달된 사용자ID로 사용자를 검색하고 비밀번호를 변경
     * 
     * @param session
     * @param userId
     * @param password
     * @return 사용자검색 실패 시 false 반환
     * @throws WebObjectsException
     */
    public static boolean findUserAndchangeUserPassword(WebIServerSession session, String userId, String password)
            throws WebObjectsException {
        boolean result = false;

        WebObjectSource objectSource = session.getFactory().getObjectSource();
        WebUser user = searchUser(objectSource, userId);
        if (user != null) {
            WebStandardLoginInfo standardLoginInfo = user.getStandardLoginInfo();
            standardLoginInfo.setPassword(password);
            objectSource.save(user);
            result = true;
        }

        return result;
    }

    /**
     * 사용자ID로 사용자 검색
     * 
     * @param session
     * @param userId
     * @return
     * @throws WebObjectsException
     */
    public static final WebUser searchUser(WebObjectSource objectSource, String userId) throws WebObjectsException {
        WebSearch search = getUserSearch(objectSource);
        search.setAbbreviationPattern(userId);
        search.submit();
        WebFolder folder = search.getResults();

        WebUser user = null;
        if (folder != null && !folder.isEmpty()) {
            user = (WebUser) folder.get(0);
            user.populate();
        }

        return user;
    }

    /**
     * 사용자로 전달된 WebSearch객체로부터 사용자ID로 검색
     * 
     * @param search
     * @param userId
     * @return
     * @throws WebObjectsException
     */
    public static final WebUser searchUser(WebSearch search, String userId) throws WebObjectsException {
        search.setAbbreviationPattern(userId);
        search.submit();
        WebFolder folder = search.getResults();

        WebUser user = null;
        if (folder != null && !folder.isEmpty()) {
            user = (WebUser) folder.get(0);
            user.populate();
        }

        return user;
    }

    /**
     * 사용자객체의 오브젝트ID로 사용자 생성
     * 
     * @param objectSource
     * @param userObjectId
     * @return
     * @throws WebObjectsException
     */
    public static final WebUser getUser(WebObjectSource objectSource, String userObjectId) throws WebObjectsException {
        WebUser user = (WebUser) objectSource.getObject(userObjectId, EnumDSSXMLObjectTypes.DssXmlTypeUser,
                EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser, true);

        return user;
    }

    /**
     * 사용자그룹명으로 사용자그룹 검색
     * 
     * @param search
     * @param userGroupName
     * @return
     * @throws WebObjectsException
     */
    public static final WebUserGroup searchUserGroup(WebSearch search, String userGroupName)
            throws WebObjectsException {
        search.setNamePattern(userGroupName);
        search.submit();
        WebFolder folder = search.getResults();

        WebUserGroup userGroup = null;
        if (folder.size() > 0) {
            userGroup = (WebUserGroup) folder.get(0);
            userGroup.populate();
        }

        return userGroup;
    }

    /**
     * 사용자그룹 검색 객체 반환
     * 
     * @param objectSource
     * @return
     */
    public static final WebSearch getUserSearch(WebObjectSource objectSource) {
        WebSearch search = objectSource.getNewSearchObject();
        search.setAsync(false);
        search.types().add(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser);
        search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);

        return search;
    }

    /**
     * 사용자그룹 검색객체 반환
     * 
     * @param objectSource
     * @return
     */
    public static final WebSearch getUserGroupSearch(WebObjectSource objectSource) {
        WebSearch search = objectSource.getNewSearchObject();
        search.setAsync(false);
        search.types().add(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup);
        search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);

        return search;
    }

    /**
     * 활성화된 사용자 수 반환
     * 
     * @param objectSource
     * @return
     * @throws WebObjectsException
     */
    public static final int getEnableCount(WebObjectSource objectSource) throws WebObjectsException {
        WebSearch search = objectSource.getNewSearchObject();
        search.setAsync(false);
        search.setSearchFlags(search.getSearchFlags() + EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard);
        search.types().add(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser);
        search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);
        search.setNamePattern("*");
        search.submit();
        WebFolder folder = search.getResults();

        int totalCount = 0;
        for (int i = 0; i < folder.size(); i++) {
            WebUser user = (WebUser) folder.get(i);
            user.populate();
            if (user.isEnabled()) {
                totalCount++;
            }
        }
        return totalCount;
    }

    /**
     * 모든 사용자그룹에 대하여 사용자그룹명, 사용자그룹 오브젝트ID 구성된 맵을 반환
     * 
     * @param objectSource
     * @return
     * @throws WebObjectsException
     */
    public static final Map<String, String> getAllUserGroup(WebObjectSource objectSource) throws WebObjectsException {
        WebSearch search = objectSource.getNewSearchObject();
        search.setAsync(false);
        search.setSearchFlags(search.getSearchFlags() + EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard);
        search.types().add(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup);
        search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);
        search.setNamePattern("*");
        search.submit();
        WebFolder folder = search.getResults();

        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < folder.size(); i++) {
            result.put(folder.get(i).getDisplayName(), folder.get(i).getID());
        }
        return result;
    }

    /**
     * 파라미터로 전달된 오브젝트ID를 갖는 사용자그룹의 하위 사용자그룹, 사용자 정보를 사용자그룹명/사용자그룹 오브젝트ID로 구성된 맵,
     * 사용자ID/사용자 오브젝트ID로 구성된 맵, 사용자그룸명/소속 사용자 오브젝트ID 리스스로 구성된 맵을 반환
     * 
     * @param source
     * @param baseMstrGroupId
     * @return
     * @throws IllegalArgumentException
     * @throws WebObjectsException
     */
    public static Map<String, Map<String, ?>> getSubUserObject(WebObjectSource source, String baseMstrGroupId)
            throws WebObjectsException, IllegalArgumentException {
        Map<String, String> mstrGroupMap = new HashMap<String, String>();
        Map<String, String> mstrUserMap = new HashMap<String, String>();
        HashMap<String, Set<String>> mstrGroupOfUser = new HashMap<String, Set<String>>();

        WebUserGroup baseGroup = (WebUserGroup) source.getObject(baseMstrGroupId, EnumDSSXMLObjectTypes.DssXmlTypeUser,
                EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);

        Enumeration<?> currentElements = baseGroup.getMembers().elements();
        LinkedList<Enumeration<?>> stack = new LinkedList<Enumeration<?>>();

        while (true) {
            if (!currentElements.hasMoreElements()) {
                while (!stack.isEmpty()) {
                    currentElements = stack.pop();
                    if (currentElements.hasMoreElements()) {
                        break;
                    }
                }
            }

            if (stack.isEmpty() && !currentElements.hasMoreElements()) {
                break;
            }

            Object element = currentElements.nextElement();
            if (element instanceof WebUserGroup) {
                WebUserGroup group = (WebUserGroup) element;
                group.populate();

                stack.push(currentElements);
                currentElements = group.getMembers().elements();

                Set<String> userIdSet = new HashSet<String>();
                for (Enumeration<?> e = group.getMembers().elements(); e.hasMoreElements();) {
                    Object object = e.nextElement();
                    if (object instanceof WebUser) {
                        userIdSet.add(((WebUser) object).getAbbreviation());
                    }
                }
                mstrGroupOfUser.put(group.getDisplayName(), userIdSet);

                mstrGroupMap.put(group.getDisplayName(), group.getID());
            } else if (element instanceof WebUser) {
                WebUser user = (WebUser) element;

                if (!mstrUserMap.containsKey(user.getAbbreviation())) {
                    mstrUserMap.put(user.getAbbreviation(), user.getID());
                }
            }
        }

        Map<String, Map<String, ?>> result = new HashMap<String, Map<String, ?>>();
        result.put("mstrGroupMap", mstrGroupMap);
        result.put("mstrUserMap", mstrUserMap);
        result.put("mstrGroupOfUser", mstrGroupOfUser);

        return result;
    }

    /**
     * 파라미터로 전달된 오브젝트ID를 갖는 사용자그룹의 하위 사용자그룹, 사용자 정보를 사용자그룹명/사용자그룹 오브젝트ID로 구성된 맵을 반환
     * 
     * @param source
     * @param baseMstrGroupId
     * @return
     * @throws IllegalArgumentException
     * @throws WebObjectsException
     */
    public static Map<String, String> getSubUserGroup(WebObjectSource source, String baseMstrGroupId)
            throws WebObjectsException, IllegalArgumentException {
        Map<String, String> mstrGroupMap = new HashMap<String, String>();

        WebUserGroup baseGroup = (WebUserGroup) source.getObject(baseMstrGroupId, EnumDSSXMLObjectTypes.DssXmlTypeUser,
                EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);

        Enumeration<?> currentElements = baseGroup.getMembers().elements();
        LinkedList<Enumeration<?>> stack = new LinkedList<Enumeration<?>>();

        while (true) {
            if (!currentElements.hasMoreElements()) {
                while (!stack.isEmpty()) {
                    currentElements = stack.pop();
                    if (currentElements.hasMoreElements()) {
                        break;
                    }
                }
            }

            if (stack.isEmpty() && !currentElements.hasMoreElements()) {
                break;
            }

            Object element = currentElements.nextElement();
            if (element instanceof WebUserGroup) {
                WebUserGroup group = (WebUserGroup) element;
                group.populate();

                stack.push(currentElements);
                currentElements = group.getMembers().elements();

                mstrGroupMap.put(group.getDisplayName(), group.getID());
            }
        }

        return mstrGroupMap;
    }
    
    
	/**
	 * MSTR 사용자 부서를 가지고 옴
	 * @param session
	 * @param user
	 * @param remoteAddr
	 * @return
	 * @throws WebObjectsException
	 */
	public static Map<String, String> applyIsImpttTerminal(WebIServerSession session, WebUser user, String remoteAddr) throws WebObjectsException {
		Map<String, String> groupIdMap = new HashMap<String, String>();
		
//		WebObjectSource objectSource = session.getFactory().getObjectSource();
		
		if (user != null) {
//			List<WebUserGroup> groupList = MstrUserUtil.getUserGroupList(user);
			
//			String groupExceptId = CustomProperties.getProperty("mstr.group.except.id");
			String groupExceptId = "";
			List<WebUserGroup> newGroupList = MstrUserUtil.getUserGroupList(user);
			for (WebUserGroup group : newGroupList) {
				String groupId = group.getID();
				String groupNm = group.getDisplayName();
				
				if(groupNm.indexOf(".") > -1) {
					String[] groupNmSplit = groupNm.split("\\.");
					groupNm = groupNmSplit[1];
				}
				
				if(groupExceptId.indexOf(groupId) == -1) {
					groupIdMap.put(groupId, groupNm);
				}
				
				/*
				Map<String, String> map = MstrUtil.getCommentsAsMap(session, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, groupId);
				groupIdMap.put(groupId, map != null ? map.get("dashboard") + ";" + group.getDisplayName() : group.getDisplayName());
				*/
			}
			
			LOGGER.debug("==> groupIdMap:[{}]", groupIdMap);
		}
		
		return groupIdMap;
	}
}
