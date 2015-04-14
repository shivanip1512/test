package com.cannontech.database.data.lite;

import java.util.List;

import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.google.common.collect.Lists;

public class LiteNotificationGroup extends LiteBase {
    private String notificationGroupName = null;
    private boolean disabled = false;

    private List<NotifDestinationMap> notifDestinationMap = Lists.newArrayList();
    private List<ContactNotifGroupMap> contactMap = Lists.newArrayList();
    private List<CustomerNotifGroupMap> customerMap = Lists.newArrayList();

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

    public List<ContactNotifGroupMap> getContactMap() {
        return contactMap;
    }

    public List<CustomerNotifGroupMap> getCustomerMap() {
        return customerMap;
    }

    public void setContactMap(List<ContactNotifGroupMap> maps) {
        contactMap = maps;
    }

    public void setCustomerMap(List<CustomerNotifGroupMap> maps) {
        customerMap = maps;
    }

    public List<NotifDestinationMap> getNotifDestinationMap() {
        return notifDestinationMap;
    }

    public void setNotifDestinationMap(List<NotifDestinationMap> maps) {
        notifDestinationMap = maps;
    }
}
