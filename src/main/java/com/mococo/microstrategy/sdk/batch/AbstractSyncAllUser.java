package com.mococo.microstrategy.sdk.batch;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.web.objects.admin.users.WebUserList;
import com.mococo.microstrategy.sdk.batch.SyncUserUtil.UserInfo;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;

/**
 * 사용자정보 동기화 배치 (모든 사용장 처리) - targetUserList에는 모든 권한 사용자에 대한 기준정보가 설정 (변경분만에 대한
 * 처리가 아님)
 * 
 * @author hipark
 */
public abstract class AbstractSyncAllUser {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSyncAllUser.class);

    private WebObjectSource source = null;
    private List<UserInfo> targetUserList = null;
    private Map<String, String> mstrGroupMap = null;
    private Map<String, String> mstrUserMap = null;

    private void setTargetUserList() {
        targetUserList = getTargetUserInfo();
    }

    protected List<UserInfo> getTargetUserList() {
        return targetUserList;
    }

    protected Map<String, String> getMstrGroupMap() {
        return mstrGroupMap;
    }

    protected Map<String, String> getMstrUserMap() {
        return mstrUserMap;
    }

    public abstract List<UserInfo> getTargetUserInfo();

    // 모든 현재 적용되어야할 사용자목록 정보를 반환하는 메소드
    public abstract String getBaseUserGroupId();

    // 사용자조직명과 MSTR사용자그룹명으로 매칭 여부를 반환
    public abstract boolean isTargetGroup(String targetGroupName, String mstrGroupName);

    // 사용자 생성 시 지정할 비밀번호 반환
    public abstract String getPassword(UserInfo userInfo);

    // 모든 사용자목록 중에 포함되지 않은 MSTR 사용자ID가 발견되었을 경우, 비활성화 여부 반환
    public abstract boolean doDisable(WebUser webUser);

    // 메일정보 저장 시 지정할 메일 장치 오브젝트ID 반환
    public abstract String getMailDeviceId();

    private void setMstrGroupMap() throws WebObjectsException {
        mstrGroupMap = MstrUserUtil.getSubUserGroup(source, getBaseUserGroupId());
    }

    private void setMstrUserMap() throws WebObjectsException {
        mstrUserMap = new HashMap<String, String>();

        /* 모든 사용자의 정보가 포함된 MSTR "Everyone" 사용자그룹에 소속된 사용자 조회 */
        WebUserGroup group = MstrUserUtil.searchUserGroup(MstrUserUtil.getUserGroupSearch(source), "Everyone");
        WebUserList userList = group.getMembers();
        for (Enumeration<WebUser> e = userList.elements(); e.hasMoreElements();) {
            WebUser user = e.nextElement();
            mstrUserMap.put(user.getAbbreviation(), user.getID());
        }
    }

    /*
     * 파라미터로 전달된 user가 소속된 모든 사용자그룹의 목록을 반환 (mstrGroupMap에 포함된 처리대상 사용자그룹만 식별)
     */
    private Set<String> getCurrentMstrGroupIdSet(WebUser user) throws WebObjectsException {
        Set<String> result = new HashSet<String>();
        List<String> groupNameList = MstrUserUtil.getUserGroupNameList(user);
        for (String groupName : groupNameList) {
            if (mstrGroupMap.containsKey(groupName)) {
                result.add(mstrGroupMap.get(groupName));
            }
        }

        return result;
    }

    private Set<String> getTargetMstrGroupIdSet(Set<String> targetGroupNameSet) {
        Set<String> groupIdList = new HashSet<String>();

        for (String groupName : mstrGroupMap.keySet()) {
            for (String targetGroupName : targetGroupNameSet) {
                if (isTargetGroup(targetGroupName, groupName)) {
                    groupIdList.add(mstrGroupMap.get(groupName));
                }
            }
        }
        return groupIdList;
    }

    private Set<String> getRemoveGroupIdSet(Set<String> oldGroupSet, Set<String> newGroupSet) {
        Set<String> result = new HashSet<String>(oldGroupSet);

        result.removeAll(newGroupSet);

        return result;
    }

    private Set<String> getAddGroupIdSet(Set<String> oldGroupSet, Set<String> newGroupSet) {
        Set<String> result = new HashSet<String>(newGroupSet);

        result.removeAll(oldGroupSet);

        return result;
    }

    public void doSync(WebIServerSession session) {
        try {
            jobStart();

            setTargetUserList(); // 원천으로부터 조회된 사용자정보 목록
            logger.debug("=> 목표 사용자정보: [{}]", targetUserList);

            source = session.getFactory().getObjectSource();
            setMstrUserMap(); // 모든 MSTR 사용자에 대한 Map 생성
            logger.debug("=> 모든 MSTR 사용자정보: [{}]", mstrUserMap);

            setMstrGroupMap(); // 모든 MSTR 사용자에 대한 Map 생성
            logger.debug("=> 처리 대상 MSTR 사용자그룹정보: [{}]", mstrGroupMap);

            // dbUserList에서 사용자정보를 조회 후 사용자에 반영
            for (UserInfo targetUser : targetUserList) {
                try {
                    StringBuilder log = new StringBuilder();

                    // 사용자ID로 오브젝트ID 조회
                    String objectId = mstrUserMap.get(targetUser.getUserId());
                    log.append(String.format("=> 처리대상 사용자  정보:[%s]\n", targetUser));

                    WebUser user = null;

                    // 오브젝트ID가 비어있다면, 미존재 사용자
                    if (StringUtils.isEmpty(objectId)) {
                        log.append("=> 새로운 사용자 생성\n");
                        // 새로운 사용자 생성
                        user = MstrUserUtil.createUserWithMail(session, targetUser.getUserId(),
                                targetUser.getUserName(), null, targetUser.getEmail(), getMailDeviceId());
                        objectId = user.getID();
                        mstrUserMap.put(user.getAbbreviation(), objectId);
                    } else {
                        // 기존 사용자
                        user = MstrUserUtil.getUser(source, objectId);
                    }

                    // 사용자가 소속 사용자그룹ID 목록 생성
                    Set<String> currentMstrGroupIdList = getCurrentMstrGroupIdSet(user);
                    log.append(String.format("=> 현재 사용자 소속 MSTR 사용자그룹: [%s]\n", currentMstrGroupIdList));

                    // 사용자가 소속할 사용자그룹ID 목록 생성
                    Set<String> targetMstrGroupIdSet = getTargetMstrGroupIdSet(targetUser.getGroupNameSet());
                    log.append(String.format("=> 사용자가 소속되어야 할  MSTR 사용자그룹: [%s]\n", targetMstrGroupIdSet));

                    // 사용자로부터 삭제될 사용자그룹ID 결정
                    Set<String> removeGroupIdSet = getRemoveGroupIdSet(currentMstrGroupIdList, targetMstrGroupIdSet);
                    log.append(String.format("=> 사용자로부터 삭제될 MSTR 사용자그룹: [%s]\n", removeGroupIdSet));

                    // 사용자에게 추가될 사용자그룹ID 결정
                    Set<String> addGroupIdSet = getAddGroupIdSet(currentMstrGroupIdList, targetMstrGroupIdSet);
                    log.append(String.format("=> 사용자에게 추가될 MSTR 사용자그룹: [%s]\n", addGroupIdSet));

                    for (String removeGroupId : removeGroupIdSet) {
                        MstrUserUtil.removeFromUserGroup(source, user, removeGroupId);
                    }

                    for (String addGroupId : addGroupIdSet) {
                        MstrUserUtil.addToUserGroup(source, user, addGroupId);
                    }

                    if (removeGroupIdSet.size() > 0 || addGroupIdSet.size() > 0) {
                        logger.debug(log.toString());
                    }
                } catch (WebBeanException e) {
                    logger.error("!!! error", e);
                }
            }

            // targetUserList에 존재하지 않는 사용자인 경우 비활성화
            for (String userId : mstrUserMap.keySet()) {
                boolean alive = false;

                for (UserInfo userInfo : targetUserList) {
                    if (StringUtils.equals(userInfo.getUserId(), userId)) {
                        alive = true;
                        break;
                    }
                }

                if (alive) {
                    continue;
                }

                WebUser user = MstrUserUtil.getUser(source, mstrUserMap.get(userId));

                if (doDisable(user) && user.isEnabled()) {
                    user.setEnabled(false);
                    source.save(user);
                    logger.debug("=> 사용자 비활성화");
                }
            }
        } catch (WebObjectsException e) {
            logger.error("!!! error", e);
            throw new RuntimeException("");
        } finally {
            jobEnd();
        }
    }

    private static boolean active = false;

    public synchronized static void jobStart() {
        active = true;
        logger.debug("=> 작업을 시작합니다.");
    }

    public synchronized static void jobEnd() {
        active = false;
        logger.debug("=> 작업을 종료합니다.");
    }

    public synchronized static boolean isActive() {
        return active;
    }

    /* 배치로 실행되기 때문에 main 메서드 제공 */
    public static void call(AbstractSyncAllUser syncAllUser, WebIServerSession session) {
        if (AbstractSyncAllUser.isActive()) {
            logger.debug("!!! 이미 작업이 실행 중입니다.");
            throw new RuntimeException("!!! 이미 작업이 실행 중입니다.");
        } else {
            syncAllUser.doSync(session);
        }
    }
}
