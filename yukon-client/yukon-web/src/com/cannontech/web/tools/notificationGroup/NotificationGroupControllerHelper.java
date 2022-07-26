package com.cannontech.web.tools.notificationGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.web.notificationGroup.CICustomer;
import com.cannontech.web.notificationGroup.Contact;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.web.notificationGroup.NotificationSettings;
import com.cannontech.web.util.JsTreeNode;
import com.cannontech.yukon.IDatabaseCache;

public class NotificationGroupControllerHelper {
    
    @Autowired private IDatabaseCache cache;
    @Autowired private CustomerDao customerDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private ContactDao contactDao;
    
    private static final String baseKey = "yukon.web.modules.tools.notificationGroup.";
    private static final String ciCustomerNodeIdPrefix = "ci_cust_id_";
    private static final String ciCustomerContactNodeIdPrefix = "ci_contact_id_";
    private static final String ciCustomerContactNotificationNodeIdPrefix = "ci_contact_notif_id_";
    private static final String unassignedContactNodeIdPrefix = "unassigned_contact_id_";
    private static final String unassignedContactNotfiNodeIdPrefix = "unassigned_contact_notif_id_";
    
    public JsTreeNode buildNotificationTree(MessageSourceAccessor messageSourceAccessor, NotificationGroup notificationGroup) {
        JsTreeNode systemRoot = new JsTreeNode();
        
        /* Add 'CI Customer' node to the tree. This is the root node for all CI Customer nodes and is not selectable. */
        String ciCustomerNodeText = messageSourceAccessor.getMessage(baseKey + "ciCustomers");
        JsTreeNode ciCustomersRootNode = setRootNodeAttributes("ciCustomers", ciCustomerNodeText);
        systemRoot.addChild(ciCustomersRootNode);
        
        /* Add 'Unassigned Contacts' node to the tree. This is the root node for all Unassigned Contact nodes and is not selectable. */
        String unassignedContactsNodeText = messageSourceAccessor.getMessage(baseKey + "unassignedContacts");
        JsTreeNode unassignedContactsRootNode = setRootNodeAttributes("unassignedContacts", unassignedContactsNodeText);
        systemRoot.addChild(unassignedContactsRootNode);

        NotificationSettings defaultNotificationSettings = new NotificationSettings();
        
        /* Add CI Customers nodes to the tree */
        List<LiteCICustomer> liteCICustomers = new ArrayList<>(cache.getAllCICustomers());
        if (CollectionUtils.isNotEmpty(liteCICustomers)) {
            Collections.sort(liteCICustomers, LiteComparators.liteStringComparator);
            for (LiteCICustomer liteCICustomer : liteCICustomers) {
                String nodeText = StringUtils
                        .isBlank(liteCICustomer.getCompanyName()) ? CtiUtilities.STRING_NONE : liteCICustomer.getCompanyName();
                CICustomer matchedCICustObj = null;
                NotificationSettings ciCustomerNotificationSetting = null;
                if (CollectionUtils.isNotEmpty(notificationGroup.getcICustomers())) {
                    matchedCICustObj = notificationGroup.getcICustomers().stream().filter(ciCust -> ciCust.getId().equals(liteCICustomer.getCustomerID()))
                                                                                  .findFirst()
                                                                                  .orElse(null);
                }
                if (matchedCICustObj == null) {
                    ciCustomerNotificationSetting = defaultNotificationSettings;
                } else {
                    ciCustomerNotificationSetting = (NotificationSettings) matchedCICustObj;
                }
                JsTreeNode ciCustomerNode = setNodeAttributes(ciCustomerNotificationSetting, ciCustomerNodeIdPrefix, liteCICustomer.getCustomerID(), nodeText);
                ciCustomersRootNode.addChild(ciCustomerNode);
                
                /* Add contacts for CI Customer to the tree. */
                List<LiteContact> contacts = new ArrayList<>(customerDao.getAllContacts(liteCICustomer));
                if (CollectionUtils.isNotEmpty(contacts)) {
                    Collections.sort(contacts, LiteComparators.liteStringComparator);
                    for (LiteContact liteContact : contacts) {
                        String contactNodeText = liteContact.getContLastName() + ", " + liteContact.getContFirstName();
                        Contact matchedContact = null;
                        NotificationSettings ciContactNotificationSetting = null;
                        if (matchedCICustObj != null && CollectionUtils.isNotEmpty(matchedCICustObj.getContacts())) {
                            matchedContact = matchedCICustObj.getContacts().stream().filter(contact -> contact.getId().equals(liteContact.getContactID()))
                                                                   .findFirst()
                                                                   .orElse(null);
                        }
                        if (matchedContact == null) {
                            ciContactNotificationSetting = defaultNotificationSettings;
                        } else {
                            ciContactNotificationSetting = (NotificationSettings) matchedContact;
                        }
                        JsTreeNode contactNode = setNodeAttributes(ciContactNotificationSetting, ciCustomerContactNodeIdPrefix, liteContact.getContactID(), contactNodeText);
                        ciCustomerNode.addChild(contactNode);
                        /* Add notifications for the contact to the tree */
                        addNotificationsToContact(liteContact, contactNode, ciCustomerContactNotificationNodeIdPrefix, matchedContact);
                    }
                }
            }
        }

        /* Add Unassigned Contacts nodes to the tree */
        List<LiteContact> liteUnassignedContacts = contactDao.getUnassignedContacts();
        for (LiteContact liteUnassignedContact : liteUnassignedContacts) {
            String nodeText = liteUnassignedContact.getContLastName() + ", " + liteUnassignedContact.getContFirstName();
            Contact matchedUnassignedContact = null;
            NotificationSettings unassignedContactNotificationSettings = null;
            if (CollectionUtils.isNotEmpty(notificationGroup.getUnassignedContacts())) {
                matchedUnassignedContact = notificationGroup.getUnassignedContacts().stream().filter(unassignedContact -> Integer.valueOf(liteUnassignedContact.getContactID()).equals(unassignedContact.getId()))
                                                                                             .findFirst()
                                                                                             .orElse(null);
            }
            if (matchedUnassignedContact == null) {
                unassignedContactNotificationSettings = defaultNotificationSettings;
            } else {
                unassignedContactNotificationSettings = (NotificationSettings)matchedUnassignedContact;
            }
            JsTreeNode unassignedContactNode = setNodeAttributes(unassignedContactNotificationSettings, unassignedContactNodeIdPrefix, liteUnassignedContact.getContactID(), nodeText);
            unassignedContactsRootNode.addChild(unassignedContactNode);
            /* Add notifications for the contact to the tree */
            addNotificationsToContact(liteUnassignedContact, unassignedContactNode, unassignedContactNotfiNodeIdPrefix, matchedUnassignedContact);
        }
        return systemRoot;
    }
    
    private void addNotificationsToContact(LiteContact liteContact, JsTreeNode contactNode, String idPrefix, Contact matchedContact) {
        List<LiteContactNotification> notificationsForContact = contactNotificationDao.getNotificationsForContact(liteContact);
        NotificationSettings defaultNotificationSettings = new NotificationSettings();
        if (CollectionUtils.isNotEmpty(notificationsForContact)) {
            for (LiteContactNotification liteContactNotification : notificationsForContact) {
                if (liteContactNotification.getNotificationCategoryID() != 0 &&
                        (liteContactNotification.getContactNotificationType().isPhoneType() ||
                                liteContactNotification.getContactNotificationType().isEmailType() ||
                                liteContactNotification.getContactNotificationType().isShortEmailType())) {
                    NotificationSettings matchedNotificationSettings = null;
                    JsTreeNode notificationNode = null;
                    if (matchedContact != null && CollectionUtils.isNotEmpty(matchedContact.getNotifications())) {
                        matchedNotificationSettings = matchedContact.getNotifications().stream()
                                .filter(notificationSetting -> notificationSetting.getId()
                                        .equals(liteContactNotification.getContactNotifID()))
                                .findFirst()
                                .orElse(null);
                    }
                    if (matchedNotificationSettings == null) {
                        notificationNode = setNodeAttributes(defaultNotificationSettings, idPrefix,
                                liteContactNotification.getContactNotifID(), liteContactNotification.getNotification());
                    } else {
                        notificationNode = setNodeAttributes(matchedNotificationSettings, idPrefix,
                                liteContactNotification.getContactNotifID(), liteContactNotification.getNotification());
                    }
                    notificationNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_EMAIL_TYPE.getValue(),
                            liteContactNotification.getContactNotificationType().isEmailType());
                    notificationNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_PHONE_TYPE.getValue(),
                            liteContactNotification.getContactNotificationType().isPhoneType());
                    contactNode.addChild(notificationNode);
                }
            }
        }
    }
    
    private JsTreeNode setRootNodeAttributes(String id, String nodeText) {
        JsTreeNode jsTreeNode = new JsTreeNode();
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.ID.getValue(), id);
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.TEXT.getValue(), nodeText);
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.CHECKBOX.getValue(), false);
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.EXTRA_CLASSES.getValue(), "fwb");
        return jsTreeNode;
    }

    private JsTreeNode setNodeAttributes(NotificationSettings notificationSettings, String prefix, Integer id,
            String text) {
        JsTreeNode jsTreeNode = new JsTreeNode();
         
        //The root nodes for CI Customers and Unassigned Contacts do not have id set. The below condition is for these nodes.
        if (id != null) {
            jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.ID.getValue(), prefix + id);
            jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.UUID.getValue(), id);
        } else {
            jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.ID.getValue(), prefix);
        }
        
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_SELECTED.getValue(),
                notificationSettings.isSelected());
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.TEXT.getValue(), text);
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_EMAIL_ENABLED.getValue(),
                notificationSettings.isEmailEnabled());
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_EMAIL_TYPE.getValue(), true);
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_PHONE_CALL_ENABLED.getValue(),
                notificationSettings.isPhoneCallEnabled());
        jsTreeNode.setAttribute(NotificationGroupSettingsTreeAttributes.IS_PHONE_TYPE.getValue(), true);
        return jsTreeNode;
    }

    private enum NotificationGroupSettingsTreeAttributes {
        ID("id"),
        CHECKBOX("checkbox"),
        EXTRA_CLASSES("extraClasses"),
        IS_EMAIL_ENABLED("isEmailEnabled"),
        IS_EMAIL_TYPE("isEmailType"),
        IS_PHONE_CALL_ENABLED("isPhoneCallEnabled"),
        IS_PHONE_TYPE("isPhoneType"),
        IS_SELECTED("selected"),
        TEXT("text"),
        UUID("uuid");

        private String attribute;

        private NotificationGroupSettingsTreeAttributes(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return attribute;
        }
    }

}
