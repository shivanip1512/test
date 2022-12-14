package com.cannontech.core.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.yukon.IDatabaseCache;

public final class NotificationGroupDaoImpl implements NotificationGroupDao {
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public List<String> getContactNoficationEmails(LiteNotificationGroup notificationGroup) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Notification FROM (");
        sql.append(    "(SELECT ContactNotifId, ContactId, Notification, Attribs");
        sql.append(        "FROM NotificationDestination ND");
        sql.append(        "JOIN ContactNotification CN ON ND.RecipientID = CN.ContactNotifID");
        sql.append(        "WHERE ND.NotificationGroupID").eq(notificationGroup.getNotificationGroupID());
        sql.append(        "AND CN.NotificationCategoryID").eq_k(ContactNotificationType.EMAIL);
        sql.append(        "AND DisableFlag").eq_k(YNBoolean.NO);
        sql.append(    ")");
        sql.append("UNION");
        sql.append(    "(SELECT CN.ContactNotifID, CN.ContactID, Notification, Attribs FROM (");
        sql.append(        "(SELECT ContNGM.ContactID, ContNGM.NotificationGroupID, Attribs");
        sql.append(            "FROM ContactNotifGroupMap ContNGM");
        sql.append(            "WHERE ContNGM.NotificationGroupID").eq(notificationGroup.getNotificationGroupID());
        sql.append(            ")");
        sql.append(        "UNION (SELECT C.PrimaryContactID, CustNGM.NotificationGroupID, Attribs");
        sql.append(            "FROM CustomerNotifGroupMap CustNGM");
        sql.append(            "JOIN Customer C ON C.customerid = CustNGM.CustomerID");
        sql.append(            "WHERE PrimaryContactID").gt(CtiUtilities.NONE_ZERO_ID);
        sql.append(            "AND CustNGM.NotificationGroupID").eq(notificationGroup.getNotificationGroupID());
        sql.append(            ")");
        sql.append(        "UNION (SELECT CAC.ContactID, CustNGM.NotificationGroupID, Attribs");
        sql.append(            "FROM CustomerNotifGroupMap CustNGM");
        sql.append(            "JOIN Customer C ON C.customerid = CustNGM.CustomerID");
        sql.append(            "JOIN CustomerAdditionalContact cac on cac.CustomerID = c.CustomerID");
        sql.append(            "WHERE CustNGM.NotificationGroupID").eq(notificationGroup.getNotificationGroupID());
        sql.append(            ")");
        sql.append(        ") AllC");
        sql.append(    "JOIN ContactNotification CN on AllC.ContactID = CN.ContactID");
        sql.append(    "WHERE DisableFlag").eq_k(YNBoolean.NO);
        sql.append(    "AND NotificationCategoryID").eq_k(ContactNotificationType.EMAIL);
        sql.append(    ")");
        sql.append(") results");
        sql.append("WHERE Attribs LIKE 'Y%'");   //the first char in Attribs represents sending emails.

        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
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
