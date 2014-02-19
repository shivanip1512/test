package com.cannontech.core.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.yukon.IDatabaseCache;

public final class NotificationGroupDaoImpl implements NotificationGroupDao {
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public List<String> getContactNoficationEmails(LiteNotificationGroup notificationGroup) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CN.Notification");
        sql.append("FROM NotificationGroup NG");
        sql.append("   JOIN ContactNotifGroupMap GM ON NG.NotificationGroupID = GM.NotificationGroupID");
        sql.append("   JOIN ContactNotification CN ON GM.ContactID = CN.ContactID");
        sql.append("WHERE NG.NotificationGroupID").eq(notificationGroup.getNotificationGroupID());
        sql.append("   AND CN.NotificationCategoryID").eq_k(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        
        return jdbcTemplate.query(sql, RowMapper.STRING);
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
}
