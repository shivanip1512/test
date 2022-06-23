package com.cannontech.web.api.notificationGroup;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.notificationGroup.CICustomer;
import com.cannontech.web.notificationGroup.Contact;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.web.notificationGroup.NotificationSettings;
import com.cannontech.yukon.IDatabaseCache;

public class NotificationGroupApiValidator extends SimpleValidator<NotificationGroup> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private ContactDao contactDao;
    private final static String commonKey = "yukon.common.";
    private final static String notificationGroupKey = "yukon.web.api.error.notificationGroup.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public NotificationGroupApiValidator() {
        super(NotificationGroup.class);
    }

    @Override
    protected void doValidation(NotificationGroup notificationGroup, Errors errors) {

        String notifGrpId = ServletUtils.getPathVariable("id");
        Integer id = null;
        if (notifGrpId != null) {
            id = Integer.valueOf(notifGrpId);
        }
        validateNotificationGroupName(errors, notificationGroup.getName(), id);

        yukonApiValidationUtils.checkIfFieldRequired("enabled", errors, notificationGroup.getEnabled(), "Enabled");

        // validating CI Customers
        boolean isParentCICustomer = true;
        if (notificationGroup.getcICustomers() != null && !notificationGroup.getcICustomers().isEmpty()) {
            for (int i = 0; i < notificationGroup.getcICustomers().size(); i++) {
                errors.pushNestedPath("cICustomers[" + i + "]");
                validateCICustomer(errors, notificationGroup.getcICustomers().get(i), i, isParentCICustomer);
                errors.popNestedPath();
            }
        }

        // validating Unassigned Contacts
        if (notificationGroup.getUnassignedContacts() != null && !notificationGroup.getUnassignedContacts().isEmpty()) {
            isParentCICustomer = false;
            for (int contactIndex = 0; contactIndex < notificationGroup.getUnassignedContacts().size(); contactIndex++) {
                errors.pushNestedPath("unassignedContacts[" + contactIndex + "]");
                validateContacts(errors, notificationGroup.getUnassignedContacts().get(contactIndex), contactIndex, null, 0,
                        isParentCICustomer);
                errors.popNestedPath();
            }
        }
    }

    private void validateCICustomer(Errors errors, CICustomer cICust, int cICustomersIndex, boolean isParentCICustomer) {
        // validate empty CI Customer id

        yukonApiValidationUtils.checkIfFieldRequired("id", errors, cICust.getId(), "CI Customer Id");

        // validate whether CI Customer valid
        if (!errors.hasFieldErrors("id")) {
            boolean cICustomerExists = databaseCache.getAllCICustomers().stream()
                    .anyMatch(cust -> cust.getCustomerID() == cICust.getId());
            if (!cICustomerExists) {
                errors.rejectValue("id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { cICust.getId() }, "");
            }
        }

        if (cICust.getContacts() != null && !cICust.getContacts().isEmpty()) {
            // email and phone call enable is false at customer level if selected true at contact level
            validateEmailPhoneCallEnabledForCustomerFromContact(errors, cICust, cICustomersIndex);
            for (int contactIndex = 0; contactIndex < cICust.getContacts().size(); contactIndex++) {
                // email and phone call enable is false at customer level if selected true at notification level
                errors.pushNestedPath("contacts[" + contactIndex + "]");

                if (!cICust.isSelected() && cICust.getContacts().get(contactIndex).getNotifications() != null
                        && !cICust.getContacts().get(contactIndex).getNotifications().isEmpty()) {
                    validateEmailPhoneCallEnabledForCustomerFromNotifications(errors, cICust.getContacts().get(contactIndex),
                            cICust, cICustomersIndex);
                }
                // validate contacts
                validateContacts(errors, cICust.getContacts().get(contactIndex), contactIndex, cICust, cICustomersIndex,
                        isParentCICustomer);
                errors.popNestedPath();
            }

        }

    }

    private void validateEmailPhoneCallEnabledForCustomerFromContact(Errors errors, CICustomer cICust, int cICustomersIndex) {
        boolean selectionTrueForContact = cICust.getContacts().stream().anyMatch(obj -> obj.isSelected());

        if (!cICust.isSelected()) {
            if (selectionTrueForContact) {
                if (cICust.isEmailEnabled()) {
                    errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
                }
                if (cICust.isPhoneCallEnabled()) {
                    errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false },
                            "");
                }

            }
        }
    }

    private void validateEmailPhoneCallEnabledForCustomerFromNotifications(Errors errors, Contact contact, CICustomer cICust,
            int cICustomersIndex) {
        // email and phone call enable is false at parent level if selected true at child level
        boolean selectionTrueForNotification = contact.getNotifications().stream().anyMatch(obj -> obj.isSelected());
        if (selectionTrueForNotification) {
            if (cICust.isEmailEnabled() && !errors.hasFieldErrors("emailEnabled")) {
                errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
            }
            if (cICust.isPhoneCallEnabled() && !errors.hasFieldErrors("phoneCallEnabled")) {
                errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
            }
        }
    }

    private void validateContacts(Errors errors, Contact cont, int contactIndex, CICustomer cICust, int cICustomersIndex,
            boolean isParentCICustomer) {

        // validate empty Contact Id

        if (isParentCICustomer) {
            yukonApiValidationUtils.checkIfFieldRequired("id", errors, cont.getId(), "Contact Id");

        } else {
            yukonApiValidationUtils.checkIfFieldRequired("id", errors, cont.getId(), "Unassigned Contact Id");

        }

        // validate whether contact id exists
        if (!errors.hasFieldErrors("id")) {
            validateContactId(cont, cICust, errors, isParentCICustomer, contactIndex);
        }
        if (isParentCICustomer) {
            // email enabled and phone call enabled for contact is same as customer if CI Cust is selected true
            // not required for unassigned Contact since it does not have a parent
            if (cICust.isSelected()) {
                validateEmailPhoneCallEnabledForContactFromCustomer(cICust, cont, cICustomersIndex, contactIndex, errors);
            }
            // Validate if customer is selected, contact cannot be selected
            // not required for unassigned Contact since it does not have a parent
            if (cont.isSelected()) {
                if (cICust.isSelected()) {
                    errors.rejectValue("selected", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
                }
            }

        }
        if ((cont.getNotifications() != null && !cont.getNotifications().isEmpty())) {
            // email and phone call enable is false at parent level if selected true at child level
            validateEmailPhoneCallEnabledForContactFromNotification(errors, cont.getNotifications(), cont, cICust);

            for (int notifIndex = 0; notifIndex < cont.getNotifications().size(); notifIndex++) {
                errors.pushNestedPath("notifications[" + notifIndex + "]");
                // validation notifications
                validateNotification(errors, cont.getNotifications().get(notifIndex), cont, cICust, isParentCICustomer,
                        notifIndex);
                errors.popNestedPath();
            }

        }
    }

    private void validateEmailPhoneCallEnabledForContactFromCustomer(CICustomer cICust, Contact cont, int cICustomersIndex,
            int contactIndex,
            Errors errors) {

        if (cont.isEmailEnabled() != cICust.isEmailEnabled()) {
            errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cICust.isEmailEnabled() }, "");
        }
        if (cont.isPhoneCallEnabled() != cICust.isPhoneCallEnabled()) {
            errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cICust.isPhoneCallEnabled() }, "");

        }

    }

    private void validateContactId(Contact cont, CICustomer cICust,
            Errors errors, boolean isParentCICustomer, int contactIndex) {
        LiteContact contactExists = null;

        try {
            contactExists = contactDao.getContact(cont.getId());
        } catch (DataAccessException e) {
            errors.rejectValue("id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { cont.getId() }, "");

        }
        // validating for unassigned contact id
        if (!isParentCICustomer && !errors.hasFieldErrors("id")) {
            boolean unassignedContactExists = contactDao.getUnassignedContacts().stream()
                    .anyMatch(c -> c.getContactID() == cont.getId());
            if (!unassignedContactExists) {
                String invalidUnassignedContactI18nText = accessor.getMessage(notificationGroupKey + "invalidUnassignedContact");
                errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { invalidUnassignedContactI18nText }, "");
            }

        }
        // need to pop to check field error for CI Customer
        errors.popNestedPath();
        // validate contact with Ci Cust only when it has a parent and CI Cust ID is valid
        if (isParentCICustomer && !errors.hasFieldErrors("id")) {
            // pushing it back again
            errors.pushNestedPath("contacts[" + contactIndex + "]");
            if (contactExists != null) {

                LiteCustomer validCICust = databaseCache.getACustomerByPrimaryContactID(cont.getId());
                if (validCICust == null) {
                    String invalidContactI18nText = accessor.getMessage(notificationGroupKey + "invalidContact");
                    errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { invalidContactI18nText }, "");
                } else if (validCICust != null && cICust.getId() != null) {
                    String invalidContactI18nText = accessor.getMessage(notificationGroupKey + "invalidContact");
                    if (validCICust.getCustomerID() != cICust.getId()
                            || validCICust.getCustomerTypeID() != CustomerTypes.CUSTOMER_CI) {
                        errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { invalidContactI18nText }, "");

                    }
                }
            }
        } else {
            errors.pushNestedPath("contacts[" + contactIndex + "]");
        }

    }

    private void validateEmailPhoneCallEnabledForContactFromNotification(Errors errors,
            List<NotificationSettings> notif, Contact cont, CICustomer cICust) {
        // email and phone call enable is false at parent level if selected true at child level

        boolean selectionTrueForNotif = notif.stream().anyMatch(obj -> obj.isSelected());
        if (!cont.isSelected()) {
            // if ci cust if selected then validation for email and phone call will be according to ci cust and not notification
            if (selectionTrueForNotif && !cICust.isSelected()) {
                {
                    if (cont.isEmailEnabled()) {
                        errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false },
                                "");
                    }
                    if (cont.isPhoneCallEnabled()) {
                        errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { false }, "");
                    }
                }
            }
        }
    }

    private void validateNotification(Errors errors, NotificationSettings notif, Contact cont,
            CICustomer cICust, boolean isParentCICustomer, int notifIndex) {

        // validate empty id
        yukonApiValidationUtils.checkIfFieldRequired("id", errors, notif.getId(), "Notification Id");

        // validate for notification id
        LiteContactNotification liteNotifObject = null;
        if (!errors.hasFieldErrors("id")) {

            liteNotifObject = validateContactNotificationId(notif, errors,
                    cont, notifIndex);

        }

        // validate email enabled and phone call enabled when selected true for Ci Cust
        if (isParentCICustomer && cICust.isSelected()) {
            validateEmailPhoneCallEnabledForNotificationFromCustomer(cICust, cont, errors, notif);
        }
        // validate email enabled and phone call enabled one level up i.e. when selected true for Contact
        if (cont.isSelected()) {
            validateEmailPhoneCallEnabledForNotificationFromContact(errors, notif, cont);
        }

        // if selected is true for either customer or contact, then it cannot be true for notification
        validateWhenSelectedTrueForCustomerContactAndNotification(errors, notif, cont, cICust, isParentCICustomer);

        // validate email enabled phone call enabled on category if selected true
        if (notif.isSelected()) {
            if (liteNotifObject != null) {
                if (liteNotifObject.getContactNotificationType().isEmailType()
                        || liteNotifObject.getContactNotificationType().isShortEmailType()
                        || liteNotifObject.getContactNotificationType().isPhoneType()) {
                    validateEmailPhoneCallEnabledOnType(liteNotifObject, errors, notif);

                } else if (!errors.hasFieldErrors("id")) {
                    String validNotificationTypeI18nText = accessor.getMessage(notificationGroupKey + "validNotificationType");
                    errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { validNotificationTypeI18nText }, "");
                }
            }
        }
    }

    private void validateWhenSelectedTrueForCustomerContactAndNotification(Errors errors, NotificationSettings notif,
            Contact cont, CICustomer cICust, boolean isParentCICustomer) {
        if (isParentCICustomer && (cICust.isSelected() || cont.isSelected()) && notif.isSelected()) {
            errors.rejectValue("selected", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
        } else if (!isParentCICustomer) {
            if (cont.isSelected() && notif.isSelected()) {
                errors.rejectValue("selected", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
            }

        }

    }

    private void validateEmailPhoneCallEnabledForNotificationFromContact(Errors errors, NotificationSettings notif,
            Contact cont) {

        if (notif.isEmailEnabled() != cont.isEmailEnabled() && (!errors.hasFieldErrors("emailEnabled"))) {
            errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cont.isEmailEnabled() }, "");

        }
        if (notif.isPhoneCallEnabled() != cont.isPhoneCallEnabled() && (!errors.hasFieldErrors("phoneCallEnabled"))) {
            errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cont.isPhoneCallEnabled() }, "");

        }

    }

    private void validateEmailPhoneCallEnabledForNotificationFromCustomer(CICustomer cICust, Contact cont, Errors errors,
            NotificationSettings notif) {

        if (notif.isEmailEnabled() != cICust.isEmailEnabled()) {
            errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cICust.isEmailEnabled() }, "");
        }
        if ((notif.isPhoneCallEnabled() != cICust.isPhoneCallEnabled())) {
            errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cICust.isPhoneCallEnabled() }, "");
        }

    }

    private LiteContactNotification validateContactNotificationId(NotificationSettings notif, Errors errors, Contact cont,
            int notifIndex) {
        int notifId = notif.getId();
        LiteContactNotification liteNotifObject = null;
        boolean notifExists = databaseCache.getAllContactNotifsMap().keySet().stream().anyMatch(obj -> obj.intValue() == notifId);
        if (!notifExists) {
            errors.rejectValue("id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { notifId }, "");
        }
        if (!errors.hasFieldErrors("id")) {
            try {
                liteNotifObject = contactNotificationDao.getNotificationForContact(notif.getId());
            } catch (DataAccessException e) {
                errors.rejectValue("id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { notif.getId() }, null);

            }
            // validate notification id with contact id if contact id is valid
            if (liteNotifObject != null && cont.getId() != null) {
                if (liteNotifObject.getContactID() != cont.getId()) {
                    String invalidNotificationi18nKey = accessor.getMessage(notificationGroupKey + "invalidNotification");
                    errors.popNestedPath();
                    if (!errors.hasFieldErrors("id")) {
                        errors.pushNestedPath("notifications[" + notifIndex + "]");
                        errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { invalidNotificationi18nKey }, null);

                    } else {
                        errors.pushNestedPath("notifications[" + notifIndex + "]");
                    }
                }
            }
        }
        return liteNotifObject;
    }

    public void validateNotificationGroupName(Errors errors, String notificationGroupName, Integer notificationGroupId) {
        String nameI18nText = accessor.getMessage(commonKey + "name");
        if (!errors.hasFieldErrors("name")) {
            // validate name
            yukonApiValidationUtils.checkIfFieldRequired("name", errors, notificationGroupName, nameI18nText);

            if (StringUtils.isNotBlank(notificationGroupName)) {
                yukonApiValidationUtils.checkExceedsMaxLength(errors, "name", notificationGroupName, 40);
            }
        }
        if (!errors.hasFieldErrors("name")) {
            databaseCache.getAllContactNotificationGroups().stream()
                    .filter(liteGroup -> liteGroup.getNotificationGroupName().equalsIgnoreCase(notificationGroupName.trim()))
                    .findAny()
                    .ifPresent(group -> {
                        if (notificationGroupId == null || group.getNotificationGroupID() != notificationGroupId) {
                            errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                                    new Object[] { nameI18nText }, "");
                        }
                    });
        }
    }

    private void validateEmailPhoneCallEnabledOnType(LiteContactNotification liteNotifObject, Errors errors,
            NotificationSettings notif) {
        String emailTypeNotificationI18nText = accessor.getMessage(notificationGroupKey + "invalidPhoneCallNotification");
        String phoneTypeNotificationI18nText = accessor.getMessage(notificationGroupKey + "invalidEmailNotification");

        if ((liteNotifObject.getContactNotificationType().isEmailType()
                || liteNotifObject.getContactNotificationType().isShortEmailType()) && notif.isPhoneCallEnabled()) {
            errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { emailTypeNotificationI18nText }, "");
        } else if (liteNotifObject.getContactNotificationType().isPhoneType() && notif.isEmailEnabled()) {
            errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { phoneTypeNotificationI18nText }, "");

        }
    }
}