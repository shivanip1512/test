package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.database.db.notification.NotificationGroup;

public class LiteNotificationGroup extends LiteBase {
    private String notificationGroupName = null;
    private boolean disabled = false;

    private NotifDestinationMap[] notifDestinationMap = new NotifDestinationMap[0];
    private ContactNotifGroupMap[] contactMap = new ContactNotifGroupMap[0];
    private CustomerNotifGroupMap[] customerMap = new CustomerNotifGroupMap[0];

    /**
     * LiteNotificationGroup
     */
    public LiteNotificationGroup(int nID) {
        this(nID, "");
    }

    /**
     * LiteNotificationGroup
     */
    public LiteNotificationGroup(int nID, String nName) {
        super();
        setNotificationGroupID(nID);
        notificationGroupName = new String(nName);
        setLiteType(LiteTypes.NOTIFICATION_GROUP);
    }

    public int getNotificationGroupID() {
        return getLiteID();
    }

    public String getNotificationGroupName() {
        return notificationGroupName;
    }

    public void retrieve(String databaseAlias) {
        String notifSQL = 
                "SELECT GroupName, DisableFlag " +
                "FROM " + NotificationGroup.TABLE_NAME + " " + 
                "WHERE NotificationGroupID = " + 
                Integer.toString(getNotificationGroupID());

        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;
    
        try {
            conn = PoolManager.getInstance().getConnection( databaseAlias );
            stmt = conn.createStatement();
            rset = stmt.executeQuery(notifSQL);

            while (rset.next()) {
                setNotificationGroupName(rset.getString(1).trim());
                setDisabled(rset.getString(2).trim().charAt(0) == CtiUtilities.trueChar.charValue());
            }

            setNotifDestinationMap(
                com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupDestinations(
                    new Integer(getNotificationGroupID()), conn));

            setContactMap(
                com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupContacts(
                    new Integer(getNotificationGroupID()), conn));

            setCustomerMap(
                com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupCustomers(
                    new Integer(getNotificationGroupID()), conn));
                
        } catch (java.sql.SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }
    }

    public void setNotificationGroupID(int newValue) {
        setLiteID(newValue);
    }

    public void setNotificationGroupName(String newValue) {
        this.notificationGroupName = new String(newValue);
    }

    @Override
    public String toString() {
        return notificationGroupName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean b) {
        disabled = b;
    }

    public ContactNotifGroupMap[] getContactMap() {
        return contactMap;
    }

    public CustomerNotifGroupMap[] getCustomerMap() {
        return customerMap;
    }

    public void setContactMap(ContactNotifGroupMap[] maps) {
        contactMap = maps;
    }

    public void setCustomerMap(CustomerNotifGroupMap[] maps) {
        customerMap = maps;
    }

    public NotifDestinationMap[] getNotifDestinationMap() {
        return notifDestinationMap;
    }

    public void setNotifDestinationMap(NotifDestinationMap[] maps) {
        notifDestinationMap = maps;
    }

}
