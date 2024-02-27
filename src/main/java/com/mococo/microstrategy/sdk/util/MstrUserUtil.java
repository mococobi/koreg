package com.mococo.microstrategy.sdk.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

/**
 * MstrUserUtil
 * @author mococo
 *
 */
public class MstrUserUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(MstrUserUtil.class);
	
	
    /**
     * MstrUserUtil
     */
    public MstrUserUtil() {
    	logger.debug("MstrUserUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("MstrUserUtil");
    }
    
    
    /**
     * 사용자 생성
     * @param session
     * @param userId
     * @param name
     * @param pwd
     * @return
     */
    public static final WebUser createUser(final WebIServerSession session, final String userId, final String name, final String pwd) throws WebObjectsException, WebBeanException {
    	final UserBean userBean = (UserBean) BeanFactory.getInstance().newBean("UserBean");
        userBean.setSessionInfo(session);
        userBean.InitAsNew();

        final WebUser webUser = (WebUser) userBean.getUserEntityObject();

        webUser.setLoginName(userId);
        webUser.setFullName(name);

        final WebStandardLoginInfo standardLoginInfo = webUser.getStandardLoginInfo();
        standardLoginInfo.setPassword(pwd);

        final WebSimpleSecurityPluginLoginInfo securityLoginInfo = webUser.getSimpleSecurityPluginLoginInfo();
        securityLoginInfo.setUid(userId);

        userBean.save();

        return webUser;
    }
    
    
    /**
     * 사용자를 추가하고 메일정보를 추가
     */
    public static final WebUser createUserWithMail(final WebIServerSession session, final Map<String, Object> userData) throws WebObjectsException, WebBeanException {
    	final String userId = (String) userData.get("userId");
    	final String name = (String) userData.get("name");
    	final String pwd = (String) userData.get("pwd");
    	final String email = (String) userData.get("email");
    	final String deviceId = (String) userData.get("deviceId");
    	
    	final UserBean userBean = (UserBean) BeanFactory.getInstance().newBean("UserBean");
        userBean.setSessionInfo(session);
        userBean.InitAsNew();

        final WebUser webUser = (WebUser) userBean.getUserEntityObject();

        webUser.setLoginName(userId);
        webUser.setFullName(name);

        final WebStandardLoginInfo standardLoginInfo = webUser.getStandardLoginInfo();
        standardLoginInfo.setPassword(pwd);

        final WebSimpleSecurityPluginLoginInfo securityLoginInfo = webUser.getSimpleSecurityPluginLoginInfo();
        securityLoginInfo.setUid(userId);

        if (StringUtils.isNotEmpty(email)) {
            webUser.populate();

            final WebSubscriptionUserAddresses addresses = webUser.getAddresses();
            final WebSubscriptionAddress address = addresses.addNewAddress(EnumDSSXMLSubscriptionDeliveryType.DssXmlDeliveryTypeEmail);
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
     * @param session
     * @param userGroupName
     * @param parentObjectId
     * @return
     */
    public static final WebUserGroup createUserGroup(final WebIServerSession session, final String userGroupName, final String parentObjectId) throws WebBeanException, WebObjectsException {
        return createUserGroup(session, userGroupName, parentObjectId, null);
    }
    
    
    /**
     * 사용자그룹을 생성하고 주석을 설정
     * @param session
     * @param userGroupName
     * @param parentObjectId
     * @param desc
     * @return
     */
    public static final WebUserGroup createUserGroup(final WebIServerSession session, final String userGroupName,
    		final String parentObjectId, final String desc) throws WebBeanException, WebObjectsException {
    	final WebSearch search = getUserGroupSearch(session.getFactory().getObjectSource());
        WebUserGroup webUserGroup = searchUserGroup(search, userGroupName);

        if (webUserGroup == null) {
        	final UserGroupBean group = (UserGroupBean) BeanFactory.getInstance().newBean("UserGroupBean");
            group.setSessionInfo(session);
            group.InitAsNew();
            group.getUserEntityObject().setFullName(userGroupName);
            group.getUserEntityObject().setDescription(desc);

            final UserGroupBean parentGroup = (UserGroupBean) BeanFactory.getInstance().newBean("UserGroupBean");
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
     * @param objectSource
     * @param userId
     * @return
     * @throws WebObjectsException
     */
    public static final WebObjectInfo disableUser(final WebObjectSource objectSource, final String userId) throws WebObjectsException {
    	final WebUser user = searchUser(objectSource, userId);
    	WebObjectInfo rtnObject = null; 
    	
        if (user != null) {
            user.setEnabled(false);
            rtnObject = objectSource.save(user);
        }
        
        return rtnObject;
    }
    
    
    /**
     * 사용자그룹 오브젝트ID로 사용자그룹 객체를 반환
     * @param objectSource
     * @param userGroupObjectId
     * @return
     * @throws WebObjectsException
     */
    public static final WebUserGroup getUserGroup(final WebObjectSource objectSource, final String userGroupObjectId) throws WebObjectsException {
        return (WebUserGroup) objectSource.getObject(userGroupObjectId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
    }
    
    
    /**
     * 파라미터로 전달된 사용자가 소속된 사용자그룹들의 오브젝트ID 리스트를 반환
     * @param user
     * @return
     */
    public static final List<String> getUserGroupIdList(final WebUser user) {
    	final List<String> list = new ArrayList<>();

        if (user != null) {
        	final WebUserList userList = user.getParents();
            for (final Enumeration<WebObjectInfo> e = userList.elements(); e.hasMoreElements();) {
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
     * @param user
     * @return
     */
    public static final List<String> getUserGroupNameList(final WebUser user) {
    	final List<String> list = new ArrayList<>();

        if (user != null) {
        	final WebUserList userList = user.getParents();
            for (final Enumeration<WebObjectInfo> e = userList.elements(); e.hasMoreElements();) {
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
     * @param user
     * @return
     */
    public static final List<WebUserGroup> getUserGroupList(final WebUser user) {
    	final List<WebUserGroup> list = new ArrayList<>();

        if (user != null) {
        	final WebUserList userList = user.getParents();
            for (final Enumeration<WebObjectInfo> enumObj = userList.elements(); enumObj.hasMoreElements();) {
                final WebObjectInfo info = enumObj.nextElement();

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
     * @param objectSource
     * @param user
     * @param userGroupObjectId
     * @throws WebObjectsException
     */
    public static final void addToUserGroup(final WebObjectSource objectSource, final WebUser user, final String userGroupObjectId) throws WebObjectsException {
    	final WebUserGroup userGroup = (WebUserGroup) objectSource.getObject(userGroupObjectId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
        userGroup.getMembers().add(user);
        objectSource.save(userGroup);
    }
    
    
    /**
     * 파라미터로 전달된 사용자를 사용자그룹에서 삭제
     * @param objectSource
     * @param user
     * @param userGroupObjectId
     * @throws WebObjectsException
     */
    public static final void removeFromUserGroup(final WebObjectSource objectSource, final WebUser user, final String userGroupObjectId) throws WebObjectsException {
    	final WebUserGroup userGroup = (WebUserGroup) objectSource.getObject(userGroupObjectId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);
        userGroup.getMembers().remove(user);
        objectSource.save(userGroup);
    }
    
    
    /**
     * 사용자 비밀번호를 변경
     * @param session
     * @param userId
     * @param password
     * @throws WebObjectsException
     */
    public static final void changeUserPassword(final WebIServerSession session, final String userId, final String password) throws WebObjectsException {
    	final WebObjectSource objectSource = session.getFactory().getObjectSource();
    	final WebUser user = searchUser(objectSource, userId);
        final WebStandardLoginInfo standardLoginInfo = user.getStandardLoginInfo();
        standardLoginInfo.setPassword(password);
        objectSource.save(user);
    }
    
    
    /**
     * 파라미터로 전달된 사용자ID로 사용자를 검색하고 비밀번호를 변경
     * @param session
     * @param userId
     * @param password
     * @return
     * @throws WebObjectsException
     */
    public static boolean findUserAndchangeUserPassword(final WebIServerSession session, final String userId, final String password) throws WebObjectsException {
        boolean result = false;

        final WebObjectSource objectSource = session.getFactory().getObjectSource();
        final WebUser user = searchUser(objectSource, userId);
        if (user != null) {
        	final WebStandardLoginInfo standardLoginInfo = user.getStandardLoginInfo();
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
    public static final WebUser searchUser(final WebObjectSource objectSource, final String userId) throws WebObjectsException {
    	final WebSearch search = getUserSearch(objectSource);
        search.setAbbreviationPattern(userId);
        search.submit();
        final WebFolder folder = search.getResults();

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
    public static final WebUser searchUser(final WebSearch search, final String userId) throws WebObjectsException {
        search.setAbbreviationPattern(userId);
        search.submit();
        final WebFolder folder = search.getResults();

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
    public static final WebUser getUser(final WebObjectSource objectSource, final String userObjectId) throws WebObjectsException {
        return (WebUser)objectSource.getObject(userObjectId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser, true);
    }
    
    
    /**
     * 사용자그룹명으로 사용자그룹 검색
     * 
     * @param search
     * @param userGroupName
     * @return
     * @throws WebObjectsException
     */
    public static final WebUserGroup searchUserGroup(final WebSearch search, final String userGroupName) throws WebObjectsException {
        search.setNamePattern(userGroupName);
        search.submit();
        final WebFolder folder = search.getResults();

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
    public static final WebSearch getUserSearch(final WebObjectSource objectSource) {
    	final WebSearch search = objectSource.getNewSearchObject();
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
    public static final WebSearch getUserGroupSearch(final WebObjectSource objectSource) {
    	final WebSearch search = objectSource.getNewSearchObject();
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
    public static final int getEnableCount(final WebObjectSource objectSource) throws WebObjectsException {
    	final WebSearch search = objectSource.getNewSearchObject();
        search.setAsync(false);
        search.setSearchFlags(search.getSearchFlags() + EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard);
        search.types().add(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser);
        search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);
        search.setNamePattern("*");
        search.submit();
        final WebFolder folder = search.getResults();

        int totalCount = 0;
        for (int i = 0; i < folder.size(); i++) {
        	final WebUser user = (WebUser) folder.get(i);
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
    public static final Map<String, String> getAllUserGroup(final WebObjectSource objectSource) throws WebObjectsException {
    	final WebSearch search = objectSource.getNewSearchObject();
        search.setAsync(false);
        search.setSearchFlags(search.getSearchFlags() + EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard);
        search.types().add(EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup);
        search.setDomain(EnumDSSXMLSearchDomain.DssXmlSearchDomainRepository);
        search.setNamePattern("*");
        search.submit();
        final WebFolder folder = search.getResults();

        final Map<String, String> result = new ConcurrentHashMap<>();
        for (int i = 0; i < folder.size(); i++) {
            result.put(folder.get(i).getDisplayName(), folder.get(i).getID());
        }
        return result;
    }
    
    
    /**
     * 파라미터로 전달된 오브젝트ID를 갖는 사용자그룹의 하위 사용자그룹, 사용자 정보를 사용자그룹명/사용자그룹 오브젝트ID로 구성된 맵,
     * 사용자ID/사용자 오브젝트ID로 구성된 맵, 사용자그룸명/소속 사용자 오브젝트ID 리스스로 구성된 맵을 반환
     * @throws  
     * @throws WebObjectsException 
     */
    public static Map<String, Map<String, ?>> getSubUserObject(final WebObjectSource source, final String baseMstrGroupId) throws WebObjectsException {
    	final Map<String, String> mstrGroupMap = new ConcurrentHashMap<>();
    	final Map<String, String> mstrUserMap = new ConcurrentHashMap<>();
        final Map<String, Set<String>> mstrGroupOfUser = new ConcurrentHashMap<>();

        final WebUserGroup baseGroup = (WebUserGroup) source.getObject(baseMstrGroupId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);

        Enumeration<?> currentElements = baseGroup.getMembers().elements();
        final LinkedList<Enumeration<?>> stack = new LinkedList<>();
        
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

            final Object element = currentElements.nextElement();
            if (element instanceof WebUserGroup) {
            	final WebUserGroup group = (WebUserGroup) element;
                group.populate();

                stack.push(currentElements);
                currentElements = group.getMembers().elements();

                final Set<String> userIdSet = new HashSet<>();
                for (final Enumeration<?> e = group.getMembers().elements(); e.hasMoreElements();) {
                	final Object object = e.nextElement();
                    if (object instanceof WebUser) {
                        userIdSet.add(((WebUser) object).getAbbreviation());
                    }
                }
                mstrGroupOfUser.put(group.getDisplayName(), userIdSet);

                mstrGroupMap.put(group.getDisplayName(), group.getID());
            } else if (element instanceof WebUser) {
            	final WebUser user = (WebUser) element;

                if (!mstrUserMap.containsKey(user.getAbbreviation())) {
                    mstrUserMap.put(user.getAbbreviation(), user.getID());
                }
            }
        }

        final Map<String, Map<String, ?>> result = new ConcurrentHashMap<>();
        result.put("mstrGroupMap", mstrGroupMap);
        result.put("mstrUserMap", mstrUserMap);
        result.put("mstrGroupOfUser", mstrGroupOfUser);

        return result;
    }
    
    
    /**
     * 파라미터로 전달된 오브젝트ID를 갖는 사용자그룹의 하위 사용자그룹, 사용자 정보를 사용자그룹명/사용자그룹 오브젝트ID로 구성된 맵을 반환
     */
    public static Map<String, String> getSubUserGroup(final WebObjectSource source, final String baseMstrGroupId) throws WebObjectsException {
    	final Map<String, String> mstrGroupMap = new ConcurrentHashMap<>();
    	final WebUserGroup baseGroup = (WebUserGroup) source.getObject(baseMstrGroupId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup, true);

        Enumeration<?> currentElements = baseGroup.getMembers().elements();
        final LinkedList<Enumeration<?>> stack = new LinkedList<>();

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

            final Object element = currentElements.nextElement();
            if (element instanceof WebUserGroup) {
            	final WebUserGroup group = (WebUserGroup) element;
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
	public static Map<String, String> applyIsImpttTerminal(final WebIServerSession session, final WebUser user, final String remoteAddr) throws WebObjectsException {
		final Map<String, String> groupIdMap = new ConcurrentHashMap<>();
		
		if (user != null) {
//			List<WebUserGroup> groupList = MstrUserUtil.getUserGroupList(user);
			
//			String groupExceptId = CustomProperties.getProperty("mstr.group.except.id");
			final String groupExceptId = "";
			final List<WebUserGroup> newGroupList = getUserGroupList(user);
			for (final WebUserGroup group : newGroupList) {
				final String groupId = group.getID();
				String groupNm = group.getDisplayName();
				
				if(groupNm.indexOf('.') > -1) {
					final String[] groupNmSplit = groupNm.split("\\.");
					groupNm = groupNmSplit[1];
				}
				
				if(groupExceptId.indexOf(groupId) == -1) {
					groupIdMap.put(groupId, groupNm);
				}
			}
		}
		
		return groupIdMap;
	}
}
