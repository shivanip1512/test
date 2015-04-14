package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
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

        return jdbcTemplate.query(sql, RowMapper.STRING);
    }
    
    @Override
    public LiteNotificationGroup retrieveLiteNotificationGroup(int groupId) throws NotFoundException {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT GroupName, DisableFlag");
            sql.append("FROM NotificationGroup");
            sql.append("WHERE NotificationGroupId").eq(groupId);

            LiteNotificationGroup liteNotificationGroup = jdbcTemplate.queryForObject(sql, new YukonRowMapper<LiteNotificationGroup>() {
                
                @Override
                public LiteNotificationGroup mapRow(YukonResultSet rs) throws SQLException {

                    String name = rs.getString("GroupName");
                    LiteNotificationGroup notificationGroup = new LiteNotificationGroup(groupId, name);
                    notificationGroup.setDisabled(rs.getBoolean("DisableFlag"));
                    return notificationGroup;
                }
            });

            // Load up NotifDestinationMap
            sql = new SqlStatementBuilder();
            sql.append("SELECT RecipientId, Attribs");
            sql.append("FROM NotificationDestination");
            sql.append("WHERE NotificationGroupId").eq(groupId);
            
            List<NotifDestinationMap> notifDestinationMaps = jdbcTemplate.query(sql, new YukonRowMapper<NotifDestinationMap>() {
                @Override
                public NotifDestinationMap mapRow(YukonResultSet rs) throws SQLException {
                    int recipientId = rs.getInt("RecipientId");
                    String attribs = rs.getString("Attribs");
                    return new NotifDestinationMap(recipientId, attribs);
                }
            });
            liteNotificationGroup.setNotifDestinationMap(notifDestinationMaps);

            // Load up ContactNotifGroupMap
            sql = new SqlStatementBuilder();
            sql.append("SELECT ContactId, Attribs");
            sql.append("FROM ContactNotifGroupMap");
            sql.append("WHERE NotificationGroupId").eq(groupId);

            List<ContactNotifGroupMap> contactNotifGroupMaps = jdbcTemplate.query(sql, new YukonRowMapper<ContactNotifGroupMap>() {
                @Override
                public ContactNotifGroupMap mapRow(YukonResultSet rs) throws SQLException {
                    int contactId = rs.getInt("ContactId");
                    String attribs = rs.getString("Attribs");
                    return new ContactNotifGroupMap(contactId, attribs);
                }
            });
            liteNotificationGroup.setContactMap(contactNotifGroupMaps);

            // Load up CustomerNotifGroupMap
            sql = new SqlStatementBuilder();
            sql.append("SELECT CustomerId, Attribs");
            sql.append("FROM CustomerNotifGroupMap");
            sql.append("WHERE NotificationGroupId").eq(groupId);

            List<CustomerNotifGroupMap> customerNotifGroupMaps = jdbcTemplate.query(sql, new YukonRowMapper<CustomerNotifGroupMap>() {
                @Override
                public CustomerNotifGroupMap mapRow(YukonResultSet rs) throws SQLException {
                    int customerId = rs.getInt("CustomerId");
                    String attribs = rs.getString("Attribs");
                    return new CustomerNotifGroupMap(customerId, attribs);
                }
            });
            liteNotificationGroup.setCustomerMap(customerNotifGroupMaps);
            
            return liteNotificationGroup;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A NotificationGroup with id " + groupId + " cannot be found.");
        }
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
