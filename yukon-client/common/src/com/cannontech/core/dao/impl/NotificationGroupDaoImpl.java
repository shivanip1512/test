package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.yukon.IDatabaseCache;

public final class NotificationGroupDaoImpl implements NotificationGroupDao {
    private IDatabaseCache databaseCache;

    @Override
    public String[] getNotifEmailsByLiteGroup(LiteNotificationGroup notificationGroup) {
        List<String> emailList = new ArrayList<>(8);

        synchronized (databaseCache) {
            for (NotifDestinationMap notifDest : notificationGroup.getNotifDestinationMap()) {
                LiteContactNotification lcn = databaseCache.getAllContactNotifsMap().get(notifDest.getRecipientID());

                // we have an email address, add it to our list
                if (lcn.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) {
                    emailList.add(lcn.getNotification());
                }
            }

        }

        String[] emailArray = new String[emailList.size()];
        if (!emailList.isEmpty()) {
            emailArray = emailList.toArray(emailArray);
        }

        return emailArray;
    }

    @Override
    public LiteNotificationGroup getLiteNotificationGroup(int groupId) {
        synchronized (databaseCache) {
            List<LiteNotificationGroup> groups = databaseCache.getAllContactNotificationGroupsWithNone();
            for (LiteNotificationGroup group : groups) {
                if (groupId == group.getNotificationGroupID()) {
                    return group;
                }
            }
        }

        return null;
    }

    @Override
    public Set<LiteNotificationGroup> getAllNotificationGroups() {
        List<LiteNotificationGroup> allContactNotificationGroups = databaseCache.getAllContactNotificationGroups();
        Set<LiteNotificationGroup> hashSet = new HashSet<LiteNotificationGroup>(allContactNotificationGroups);
        return hashSet;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
