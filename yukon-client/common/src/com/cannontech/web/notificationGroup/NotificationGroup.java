package com.cannontech.web.notificationGroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.database.data.notification.NotifMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class NotificationGroup implements DBPersistentConverter<com.cannontech.database.data.notification.NotificationGroup> {
    private int id;
    private String name;
    private Boolean enabled;
    private List<CICustomer> cICustomers;
    private List<Contact> unassignedContacts;

    public NotificationGroup() {
        super();
    }

    public NotificationGroup(Integer notificationGroupID, String groupName, String disableFlag) {
        id = notificationGroupID;
        name = groupName;
        enabled = disableFlag.equals("N") ? true : false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<CICustomer> getcICustomers() {
        return cICustomers;
    }

    public void setcICustomers(List<CICustomer> cICustomers) {
        this.cICustomers = cICustomers;
    }

    public List<Contact> getUnassignedContacts() {
        return unassignedContacts;
    }

    public void setUnassignedContacts(List<Contact> unassignedContacts) {
        this.unassignedContacts = unassignedContacts;
    }

    @Override
    public void buildModel(com.cannontech.database.data.notification.NotificationGroup notificationGroup) {
        setId(notificationGroup.getNotificationGroup().getNotificationGroupID());
        setName(notificationGroup.getNotificationGroup().getGroupName());
        setEnabled(notificationGroup.getNotificationGroup().getDisableFlag().equals("N") ? true : false);
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.data.notification.NotificationGroup notificationGroup) {
        notificationGroup.getNotificationGroup().setGroupName(getName());
        notificationGroup.getNotificationGroup().setDisableFlag(getEnabled() == true ? "N" : "Y");

        List<NotifDestinationMap> notifList = new ArrayList<NotifDestinationMap>();
        List<CustomerNotifGroupMap> custList = new ArrayList<CustomerNotifGroupMap>();
        List<ContactNotifGroupMap> contactList = new ArrayList<ContactNotifGroupMap>();

        buildDBPersistentForCustomer(custList, contactList, notifList);
        // same contact map will be used for unassigned contacts
        buildDBPersistentForContact(getUnassignedContacts(), contactList, notifList);

        notificationGroup.setNotifDestinationMap(
                (NotifDestinationMap[]) notifList.toArray(new NotifDestinationMap[notifList.size()]));
        notificationGroup.setContactMap(
                (ContactNotifGroupMap[]) contactList.toArray(new ContactNotifGroupMap[contactList.size()]));
        notificationGroup
                .setCustomerMap((CustomerNotifGroupMap[]) custList.toArray(new CustomerNotifGroupMap[custList.size()]));

    }

    private void buildDBPersistentForCustomer(List<CustomerNotifGroupMap> custList, List<ContactNotifGroupMap> contactList,
            List<NotifDestinationMap> notifList) {
        if (CollectionUtils.isNotEmpty(getcICustomers())) {
            for (CICustomer cICust : getcICustomers()) {
                NotifMap notifMap = new NotifMap();
                notifMap.setSendEmails(cICust.isEmailEnabled());
                notifMap.setSendOutboundCalls(cICust.isPhoneCallEnabled());
                notifMap.setSendSms(cICust.isEmailEnabled());
                if (cICust.isSelected()) {
                    custList.add(
                            new CustomerNotifGroupMap(
                                    cICust.getId(),
                                    notifMap.getAttribs()));
                }
                buildDBPersistentForContact(cICust.getContacts(), contactList, notifList);
            }
        }
    }

    private void buildDBPersistentForContact(
            List<Contact> contacts, List<ContactNotifGroupMap> contactList, List<NotifDestinationMap> notifList) {
        if (CollectionUtils.isNotEmpty(contacts)) {
            for (Contact cont : contacts) {
                NotifMap notifMap = new NotifMap();
                notifMap.setSendEmails(cont.isEmailEnabled());
                notifMap.setSendOutboundCalls(cont.isPhoneCallEnabled());
                notifMap.setSendSms(cont.isEmailEnabled());
                if (cont.isSelected()) {
                    contactList.add(
                            new ContactNotifGroupMap(
                                    cont.getId(),
                                    notifMap.getAttribs()));
                }
                builDBPersistentForNotification(cont.getNotifications(), notifList);
            }
        }
    }

    private void builDBPersistentForNotification(List<NotificationSettings> notifications, List<NotifDestinationMap> notifList) {
        if (CollectionUtils.isNotEmpty(notifications)) {
            for (NotificationSettings notif : notifications) {
                NotifMap notifMap = new NotifMap();
                notifMap.setSendEmails(notif.isEmailEnabled());
                notifMap.setSendOutboundCalls(notif.isPhoneCallEnabled());
                notifMap.setSendSms(notif.isEmailEnabled());
                if (notif.isSelected()) {
                    notifList.add(
                            new NotifDestinationMap(
                                    notif.getId(),
                                    notifMap.getAttribs()));
                }
            }
        }
    }

}
