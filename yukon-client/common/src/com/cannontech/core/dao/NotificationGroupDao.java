package com.cannontech.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.model.Direction;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.web.notificationGroup.NotificationGroup;

public interface NotificationGroupDao {

    /**
     * Returns a list of email addresses to be sent to for notificationGroup.
     * Must be active (enabled) email type notifications.
     * This method should be deleted when YUK-13186 is fully corrected.
     * And callers should utilize the Notification Server messaging instead.
     * @deprecated
     */
    @Deprecated
    List<String> getContactNoficationEmails(LiteNotificationGroup notificationGroup);

    LiteNotificationGroup getLiteNotificationGroup(int groupId);

    Set<LiteNotificationGroup> getAllNotificationGroups();

    /**
     * Returns a list of notification groups
     */
    List<NotificationGroup> getAllNotificationGroups(String name, SortBy sortBy, Direction direction);

    public enum SortBy {
        NAME("GroupName"),
        STATUS("DisableFlag");

        private final String dbString;

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }

}
