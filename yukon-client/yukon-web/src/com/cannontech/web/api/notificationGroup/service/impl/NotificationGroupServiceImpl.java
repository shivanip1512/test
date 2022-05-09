package com.cannontech.web.api.notificationGroup.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.web.api.notificationGroup.service.NotificationGroupService;
import com.cannontech.web.notificationGroup.CICustomer;
import com.cannontech.web.notificationGroup.Contact;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.web.notificationGroup.NotificationSettings;
import com.cannontech.yukon.IDatabaseCache;

public class NotificationGroupServiceImpl implements NotificationGroupService {
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private CustomerDao customerDao;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    private static final boolean ENABLE_EMAIL = true;
    private static final boolean ENABLE_PHONECALL = false;
    private static final boolean SELECTED = false;

    @Override
    public NotificationGroup create(NotificationGroup notificationGroup) {
        com.cannontech.database.data.notification.NotificationGroup notificationGroupBase = new com.cannontech.database.data.notification.NotificationGroup();
        notificationGroup.buildDBPersistent(notificationGroupBase);
        dbPersistentDao.performDBChange(notificationGroupBase, TransactionType.INSERT);
        notificationGroup.buildModel(notificationGroupBase);
        buildModelForCICustomersAndUnassignedCont(notificationGroup, notificationGroupBase);
        return notificationGroup;
    }

    /**
     * Creating CICustomers and Unassigned Contacts objects for Notification Group here
     */
    private void buildModelForCICustomersAndUnassignedCont(NotificationGroup notificationGroup,
            com.cannontech.database.data.notification.NotificationGroup notificationGroupBase) {
        Map<Integer, CICustomer> mainMap = new HashMap<Integer, CICustomer>();
        List<Contact> unassignedContacts = new ArrayList<Contact>();

        notificationSelectedAtCustLevel(mainMap, notificationGroupBase.getCustomerMap());
        notificationSelectedAtContactlevel(mainMap, notificationGroupBase.getContactMap(), unassignedContacts);
        notificationSelectedAtNotifLevel(mainMap, notificationGroupBase.getNotifDestinationMap(), unassignedContacts);

        List<CICustomer> listOfCICustomers = mainMap.values().stream().collect(Collectors.toList());
        notificationGroup.setcICustomers(listOfCICustomers);
        notificationGroup.setUnassignedContacts(unassignedContacts);
    }

    private void notificationSelectedAtNotifLevel(Map<Integer, CICustomer> mainMap, NotifDestinationMap[] notifDestinationMap,
            List<Contact> unassignedContacts) {
        Map<Integer, Contact> tempContactMap = new HashMap<Integer, Contact>();
        for (int i = 0; i < notifDestinationMap.length; i++) {
            NotificationSettings notif = new NotificationSettings();
            int notifID = notifDestinationMap[i].getRecipientID();
            notif.setId(notifID);
            notif.setSelected(true);
            notif.setEmailEnabled(
                    convertAttribs(notifDestinationMap[i].getAttribs(), NotifType.EMAIL));
            notif.setPhoneCallEnabled(
                    convertAttribs(notifDestinationMap[i].getAttribs(), NotifType.VOICE));

            // using liteContactNotification object to get contact id
            LiteContactNotification liteNotifObject = contactNotificationDao.getNotificationForContact(notifID);
            Contact contact = new Contact(liteNotifObject.getContactID(), ENABLE_EMAIL, ENABLE_PHONECALL, SELECTED);

            // using a map here since there is a possibility of having two notifications mapped to a single contact
            if (tempContactMap.containsKey(contact.getId())) {
                Contact getContact = tempContactMap.get(contact.getId());
                List<NotificationSettings> notifs = getContact.getNotifications();
                notifs.add(notif);
            } else {
                List<NotificationSettings> notifs = new ArrayList<NotificationSettings>();
                notifs.add(notif);
                contact.setNotifications(notifs);
                tempContactMap.put(contact.getId(), contact);
            }
        }
        tempContactMap.values().stream().forEach(obj -> createCICustomerForContact(obj, mainMap, unassignedContacts));
    }

    private void notificationSelectedAtContactlevel(
            Map<Integer, CICustomer> mainMap, ContactNotifGroupMap[] contactNotifGroupMap, List<Contact> unassignedContacts) {
        for (int i = 0; i < contactNotifGroupMap.length; i++) {
            Contact contact = new Contact();
            int contID = contactNotifGroupMap[i].getContactID();
            contact.setId(contID);
            contact.setSelected(true);
            contact.setEmailEnabled(convertAttribs(contactNotifGroupMap[i].getAttribs(), NotifType.EMAIL));
            contact.setPhoneCallEnabled(convertAttribs(contactNotifGroupMap[i].getAttribs(), NotifType.VOICE));

            getNotificationsForContact(contact);

            createCICustomerForContact(contact, mainMap, unassignedContacts);
        }
    }

    private void notificationSelectedAtCustLevel(Map<Integer, CICustomer> mainMap,
            CustomerNotifGroupMap[] customerNotifGroupMap) {

        // Getting all LiteCICustomer - needed for fetching contacts
        Map<Integer, List<LiteCICustomer>> allCICustomers = cache.getAllCICustomers().stream()
                .collect(Collectors.groupingBy(LiteCICustomer::getCustomerID));

        for (int i = 0; i < customerNotifGroupMap.length; i++) {
            CICustomer cICustomer = new CICustomer();
            cICustomer.setId(customerNotifGroupMap[i].getCustomerID());
            cICustomer.setSelected(true);
            cICustomer.setEmailEnabled(convertAttribs(customerNotifGroupMap[i].getAttribs(), NotifType.EMAIL));
            cICustomer
                    .setPhoneCallEnabled(convertAttribs(customerNotifGroupMap[i].getAttribs(), NotifType.VOICE));

            // Getting Contacts associated with this customer
            List<LiteContact> liteContacts = customerDao
                    .getAllContacts(allCICustomers.get(customerNotifGroupMap[i].getCustomerID()).get(0));
            List<Contact> contacts = liteContacts.stream()
                    .map(obj -> new Contact(obj.getContactID(), ENABLE_EMAIL, ENABLE_PHONECALL, SELECTED))
                    .collect(Collectors.toList());

            // Getting Notifications for each Contact
            contacts.stream().forEach(contact -> getNotificationsForContact(contact));

            cICustomer.setContacts(contacts);
            mainMap.put(cICustomer.getId(), cICustomer);
        }
    }

    private boolean convertAttribs(String attribs, NotifType type) {
        return attribs.charAt(type.getAttribPosition()) == 'Y' ? true : false;
    }

    /**
     * This method creates CICustomer when notification is selected at Contact or Notification(Email/Phone call) level.
     */
    private void createCICustomerForContact(Contact contact, Map<Integer, CICustomer> mainMap, List<Contact> unassignedContacts) {
        // Getting Customer for this contact
        LiteCICustomer cICust = contactDao.getCICustomer(contact.getId());
        if (cICust != null) {
            CICustomer cICustomer = new CICustomer(cICust.getCustomerID(), ENABLE_EMAIL, ENABLE_PHONECALL, SELECTED);
            if (mainMap.containsKey(cICustomer.getId())) {
                List<Contact> contacts = mainMap.get(cICustomer.getId()).getContacts();
                contacts.add(contact);
            } else {
                List<Contact> temporary = new ArrayList<Contact>();
                temporary.add(contact);
                cICustomer.setContacts(temporary);
                mainMap.put(cICustomer.getId(), cICustomer);
            }
        }
        // If no Customer found for this contact, it is an unassigned contact
        else
            unassignedContacts.add(contact);

    }

    private void getNotificationsForContact(Contact contact) {
        // Getting Notif for this contact
        List<LiteContactNotification> notificationsForContact = contactNotificationDao
                .getNotificationsForContact(contact.getId());
        List<NotificationSettings> notifications = notificationsForContact.stream()
                .map(obj -> new NotificationSettings(obj.getContactNotifID(), ENABLE_PHONECALL, ENABLE_EMAIL,
                        SELECTED))
                .collect(Collectors.toList());
        contact.setNotifications(notifications);
    }
}
