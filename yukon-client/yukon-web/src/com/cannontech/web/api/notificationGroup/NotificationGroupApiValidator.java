package com.cannontech.web.api.notificationGroup;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteNotificationGroup;
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
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private ContactDao contactDao;

    private MessageSourceAccessor accessor;
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
        Integer id = notifGrpId != null ? Integer.valueOf(notifGrpId) : null;
        if (id != null) {
            databaseCache.getAllContactNotificationGroups().stream()
                    .filter(obj -> obj.getNotificationGroupID() == id).findFirst()
                    .orElseThrow(() -> new NotFoundException("Notification Group id not found"));
        }
        validateNotificationGroupName(errors, notificationGroup.getName(), id);

        yukonApiValidationUtils.checkIfFieldRequired("enabled", errors, notificationGroup.getEnabled(), "Enabled");

        // validating CI Customers
        boolean isParentCICustomer = true;
        if (CollectionUtils.isNotEmpty(notificationGroup.getcICustomers())) {
            for (int i = 0; i < notificationGroup.getcICustomers().size(); i++) {
                errors.pushNestedPath("cICustomers[" + i + "]");
                validateCICustomer(errors, notificationGroup.getcICustomers().get(i), i, isParentCICustomer);
                errors.popNestedPath();
            }
        }

        // validating Unassigned Contacts
        if (CollectionUtils.isNotEmpty(notificationGroup.getUnassignedContacts())) {
            isParentCICustomer = false;
            for (int contactIndex = 0; contactIndex < notificationGroup.getUnassignedContacts().size(); contactIndex++) {
                errors.pushNestedPath("unassignedContacts[" + contactIndex + "]");
                validateContacts(errors, notificationGroup.getUnassignedContacts().get(contactIndex), contactIndex, null,
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
                errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "valid CI Customer ID" }, "");
            }
        }
        if (CollectionUtils.isNotEmpty(cICust.getContacts())) {
            // email and phone call enable is false at customer level if selected true at contact level
            if (!cICust.isSelected()) {
                validateEmailPhoneCallEnabledForCustomerFromContact(errors, cICust);
            }
            for (int contactIndex = 0; contactIndex < cICust.getContacts().size(); contactIndex++) {
                // email and phone call enable is false at customer level if selected true at notification level
                if (!cICust.isSelected()
                        && CollectionUtils.isNotEmpty(cICust.getContacts().get(contactIndex).getNotifications())) {
                    validateEmailPhoneCallEnabledForCustomerFromNotifications(errors, cICust.getContacts().get(contactIndex),
                            cICust);
                }
                // validate contacts
                errors.pushNestedPath("contacts[" + contactIndex + "]");
                validateContacts(errors, cICust.getContacts().get(contactIndex), contactIndex, cICust, isParentCICustomer);
                errors.popNestedPath();
            }

        }

    }

    private void validateEmailPhoneCallEnabledForCustomerFromContact(Errors errors, CICustomer cICust) {
        boolean selectionTrueForContacts = cICust.getContacts().stream().anyMatch(obj -> obj.isSelected());
        if (selectionTrueForContacts) {
            if (cICust.isEmailEnabled()) {
                errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
            }
            if (cICust.isPhoneCallEnabled()) {
                errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false },
                        "");
            }
        }
    }

    private void validateEmailPhoneCallEnabledForCustomerFromNotifications(Errors errors, Contact contact, CICustomer cICust) {
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

    private void validateContacts(Errors errors, Contact cont, int contactIndex, CICustomer cICust,
            boolean isParentCICustomer) {
        // validate empty Contact Id
        String errorMessage = isParentCICustomer ? "Contact Id" : "Unassigned Contact Id";
        yukonApiValidationUtils.checkIfFieldRequired("id", errors, cont.getId(), errorMessage);

        // validate whether contact id exists
        if (!errors.hasFieldErrors("id")) {
            validateContactId(cont, cICust, errors, isParentCICustomer, contactIndex);
        }
        if (isParentCICustomer && cICust.isSelected()) {
            // email enabled and phone call enabled for contact is same as customer if CI Cust is selected true
            // not required for unassigned Contact since it does not have a parent
            validateEmailPhoneCallEnabledForContactFromCustomer(cICust, cont, errors);
            // Validate if customer is selected, contact cannot be selected
            // not required for unassigned Contact since it does not have a parent
            if (cont.isSelected()) {
                errors.rejectValue("selected", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { false }, "");
            }
        }
        if (CollectionUtils.isNotEmpty(cont.getNotifications())) {
            // email and phone call enable is false at parent level if selected true at child level
            if (!cont.isSelected()) {
                validateEmailPhoneCallEnabledForContactFromNotification(errors, cont.getNotifications(), cont, cICust);
            }
            for (int notifIndex = 0; notifIndex < cont.getNotifications().size(); notifIndex++) {
                errors.pushNestedPath("notifications[" + notifIndex + "]");
                // validation notifications
                validateNotification(errors, cont.getNotifications().get(notifIndex), cont, cICust, isParentCICustomer,
                        notifIndex);
                errors.popNestedPath();
            }
        }
    }

    private void validateEmailPhoneCallEnabledForContactFromCustomer(CICustomer cICust, Contact cont, Errors errors) {

        if (cont.isEmailEnabled() != cICust.isEmailEnabled()) {
            errors.rejectValue("emailEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cICust.isEmailEnabled() }, "");
        }
        if (cont.isPhoneCallEnabled() != cICust.isPhoneCallEnabled()) {
            errors.rejectValue("phoneCallEnabled", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { cICust.isPhoneCallEnabled() }, "");

        }

    }

    private void validateContactId(Contact cont, CICustomer cICust, Errors errors, boolean isParentCICustomer, int contactIndex) {
        try {
            LiteContact contact = databaseCache.getAContactByContactID(cont.getId());
            if (contact == null) {
                errors.rejectValue("id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { cont.getId() }, "");
            }
        } catch (EmptyResultDataAccessException e) {
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
        if (isParentCICustomer) {
            // need to pop to check field error for CI Customer
            errors.popNestedPath();
            // validate contact with Ci Cust only when it has a parent and CI Cust ID is valid
            boolean isCICustomerIdValid = !errors.hasFieldErrors("id");
            errors.pushNestedPath("contacts[" + contactIndex + "]");
            if (isCICustomerIdValid && !errors.hasFieldErrors("id")) {
                // checking with first primary contact, if not then with additional contact
                LiteCustomer validCICust = databaseCache.getACustomerByPrimaryContactID(cont.getId());
                if (validCICust == null) {
                    validCICust = contactDao.getOwnerCICustomer(cont.getId());
                }
                if (validCICust == null || cICust.getId() != validCICust.getCustomerID()) {
                    String invalidContactI18nText = accessor.getMessage(notificationGroupKey + "invalidContact");
                    errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { invalidContactI18nText }, "");
                }
            }
        }
    }

    private void validateEmailPhoneCallEnabledForContactFromNotification(Errors errors,
            List<NotificationSettings> notif, Contact cont, CICustomer cICust) {
        boolean selectionTrueForNotif = notif.stream().anyMatch(obj -> obj.isSelected());
        // if ci cust if selected then validation for email and phone call will be according to ci cust and not notification
        if (selectionTrueForNotif) {
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

    private void validateNotification(Errors errors, NotificationSettings notif, Contact cont,
            CICustomer cICust, boolean isParentCICustomer, int notifIndex) {

        // validate empty id
        yukonApiValidationUtils.checkIfFieldRequired("id", errors, notif.getId(), "Notification Id");
        // validate for notification id
        LiteContactNotification liteNotifObject = null;
        if (!errors.hasFieldErrors("id")) {
            liteNotifObject = validateContactNotificationId(notif, errors, cont, notifIndex);
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
        if (!errors.hasFieldErrors("id") && notif.isSelected()) {
            if (liteNotifObject.getContactNotificationType().isEmailType()
                    || liteNotifObject.getContactNotificationType().isShortEmailType()
                    || liteNotifObject.getContactNotificationType().isPhoneType()) {
                validateEmailPhoneCallEnabledOnType(liteNotifObject, errors, notif);

            } else {
                String validNotificationTypeI18nText = accessor.getMessage(notificationGroupKey + "validNotificationType");
                errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { validNotificationTypeI18nText }, "");
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
        LiteContactNotification liteNotifObject = databaseCache.getAllContactNotifsMap().get(notifId);
        if (liteNotifObject == null) {
            errors.rejectValue("id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { notifId }, "");
        }
        if (!errors.hasFieldErrors("id")) {
            // validate notification id with contact id if contact id is valid
            errors.popNestedPath();
            boolean isContactIdValid = !errors.hasFieldErrors("id");
            errors.pushNestedPath("notifications[" + notifIndex + "]");
            if (isContactIdValid && liteNotifObject.getContactID() != cont.getId()) {
                String invalidNotificationi18nKey = accessor.getMessage(notificationGroupKey + "invalidNotification");
                errors.rejectValue("id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { invalidNotificationi18nKey }, null);
            }
        }
        return liteNotifObject;
    }

    public void validateNotificationGroupName(Errors errors, String notificationGroupName, Integer notificationGroupId) {
        String nameI18nText = accessor.getMessage(commonKey + "name");
        if (!errors.hasFieldErrors("name")) {
            // validate name
            yukonApiValidationUtils.checkIfFieldRequired("name", errors, notificationGroupName, nameI18nText);
        }
        if (!errors.hasFieldErrors("name")) {
            if (notificationGroupName.equals(CtiUtilities.STRING_NONE)) {
                errors.rejectValue("name", ApiErrorDetails.SYSTEM_RESERVED.getCodeString(), new Object[] { nameI18nText }, "");
            }
            if (!errors.hasFieldErrors("name")) {
                yukonApiValidationUtils.checkExceedsMaxLength(errors, "name", notificationGroupName, 40);
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