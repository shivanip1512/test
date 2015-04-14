package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.spring.YukonSpringHook;

public class ContactNotificationGroupLoader implements Runnable {
    private List<LiteNotificationGroup> allContactNotificationGroups = null;

    /**
     * ContactNotificationLoader constructor comment.
     */
    public ContactNotificationGroupLoader(List<LiteNotificationGroup> contactGroupArray) {
        super();
        this.allContactNotificationGroups = contactGroupArray;
    }

    @Override
    public void run() {

        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT NotificationGroupId, GroupName, DisableFlag");
        sql.append("FROM NotificationGroup");
        sql.append("WHERE NotificationGroupId").gt(PointAlarming.NONE_NOTIFICATIONID);
        sql.append("ORDER BY GroupName");

        List<LiteNotificationGroup> liteNotificationGroups = jdbcTemplate.query(sql, new YukonRowMapper<LiteNotificationGroup>() {

            @Override
            public LiteNotificationGroup mapRow(YukonResultSet rs) throws SQLException {
                int groupId = rs.getInt("NotificationGroupId");
                String name = rs.getString("GroupName");
                LiteNotificationGroup notificationGroup = new LiteNotificationGroup(groupId, name);
                notificationGroup.setDisabled(rs.getBoolean("DisableFlag"));
                return notificationGroup;
            }
        });

        for (LiteNotificationGroup liteNotificationGroup : liteNotificationGroups) {
            int groupId = liteNotificationGroup.getNotificationGroupID();

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
            allContactNotificationGroups.add(liteNotificationGroup);
        }
    }
}
