package com.mococo.microstrategy.sdk.batch;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncUserUtil {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSyncAllUser.class);

    public static class UserInfo {
        private final String userId;
        private final String userName;
        private final String email;
        private final Set<String> groupNameSet;

        public UserInfo(String userId, String userName, String email, Set<String> groupNameSet) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
            this.groupNameSet = groupNameSet;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getEmail() {
            return email;
        }

        public Set<String> getGroupNameSet() {
            return groupNameSet;
        }

        @Override
        public String toString() {
            return "{userId:" + userId + ", userName:" + userName + ", email:" + email + ", groupNameSet:"
                    + groupNameSet + "}";
        }
    }
}
