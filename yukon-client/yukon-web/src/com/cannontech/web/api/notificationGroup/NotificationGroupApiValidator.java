package com.cannontech.web.api.notificationGroup;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final static String notificationGroupKey = "yukon.web.modules.tools.notificationGroup.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public NotificationGroupApiValidator() {
        super(NotificationGroup.class);
    }

    @Override
    protected void doValidation(NotificationGroup notificationGroup, Errors errors) {
        if (StringUtils.isNotEmpty(notificationGroup.getName())) {
            validateNotificationGroupName(errors, notificationGroup.getName(), notificationGroup.getId());
        }
        yukonApiValidationUtils.checkIfFieldRequired("enabled", errors, notificationGroup.getEnabled(),
                "Enabled");

        // validating CI Customers
        boolean isParentCICustomer = true;
        if (notificationGroup.getcICustomers() != null && !notificationGroup.getcICustomers().isEmpty()) {
            for (int i = 0; i < notificationGroup.getcICustomers().size(); i++) {
                validateCICustomer(errors, notificationGroup.getcICustomers().get(i), i, isParentCICustomer);
            }
        }

        // validating Unassigned Contacts
        if (notificationGroup.getUnassignedContacts() != null && !notificationGroup.getUnassignedContacts().isEmpty()) {
            isParentCICustomer = false;
            for (int contactIndex = 0; contactIndex < notificationGroup.getUnassignedContacts().size(); contactIndex++) {
                validateContacts(errors, notificationGroup.getUnassignedContacts().get(contactIndex), contactIndex, null, 0,
                        isParentCICustomer);
            }
        }
    }

    private void validateWhenSelectedTrueForCustomerAndContact(Errors errors, Contact cont, CICustomer cICust,
            int contactsIndex,
            int cICustomersIndex) {
        if (cont.isSelected()) {
            if (cICust.isSelected()) {
                // validating selection at contact level. Must be false.
                errors.rejectValue("cICustomers[" + cICustomersIndex + "].contacts[" + contactsIndex + "].selected",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "false" }, "");
            }

        }
    }

    private void validateCICustomer(Errors errors, CICustomer cICust, int cICustomersIndex, boolean isParentCICustomer) {
        // validate empty CI Customer id
        yukonApiValidationUtils.checkIfFieldRequired("cICustomers[" + cICustomersIndex + "].id", errors, cICust.getId(),
                "CI Customer Id");

        // validate whether CI Customer valid
        if (!errors.hasFieldErrors("cICustomers[" + cICustomersIndex + "].id")) {
            boolean cICustomerExists = databaseCache.getAllCICustomers().stream()
                    .anyMatch(cust -> cust.getCustomerID() == cICust.getId());
            if (!cICustomerExists) {
                errors.rejectValue("cICustomers[" + cICustomersIndex + "].id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { cICust.getId() }, "");
            }
        }

        if (cICust.getContacts() != null && !cICust.getContacts().isEmpty()) {
            // email and phone call enable is false at customer level if selected true at contact level
            validateEmailPhoneCallEnabledForCustomerFromContact(errors, cICust, cICustomersIndex);
            for (int contactIndex = 0; contactIndex < cICust.getContacts().size(); contactIndex++) {
                // email and phone call enable is false at customer level if selected true at notification level
                validateEmailPhoneCallEnabledForCustomerFromNotifications(errors, cICust.getContacts().get(contactIndex), cICust,
                        cICustomersIndex);
                // validate contacts
                validateContacts(errors, cICust.getContacts().get(contactIndex), contactIndex, cICust, cICustomersIndex,
                        isParentCICustomer);

            }

        }

    }

    private void validateEmailPhoneCallEnabledForCustomerFromContact(Errors errors, CICustomer cICust, int cICustomersIndex) {
        boolean selectionTrueForContact = cICust.getContacts().stream().anyMatch(obj -> obj.isSelected());

        if (!cICust.isSelected()) {
            if (selectionTrueForContact) {
                {
                    if (cICust.isEmailEnabled()) {
                        errors.rejectValue(
                                "cICustomers[" + cICustomersIndex + "].emailEnabled",
                                ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { "false" }, "");
                    }
                    if (cICust.isPhoneCallEnabled()
                            && !errors.hasFieldErrors("cICustomers[" + cICustomersIndex + "].emailEnabled")) {
                        errors.rejectValue(
                                "cICustomers[" + cICustomersIndex + "].phoneCallEnabled",
                                ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { "false" }, "");
                    }
                }

            }
        }
    }

    private void validateEmailPhoneCallEnabledForCustomerFromNotifications(Errors errors, Contact contact, CICustomer cICust,
            int cICustomersIndex) {
        // email and phone call enable is false at parent level if selected true at child level
        if (!cICust.isSelected()) {
            if (contact.getNotifications() != null && !contact.getNotifications().isEmpty()) {
                boolean selectionTrueForNotification = contact.getNotifications().stream().anyMatch(obj -> obj.isSelected());
                if (selectionTrueForNotification) {
                    if (cICust.isEmailEnabled() && !errors.hasFieldErrors("cICustomers[" + cICustomersIndex + "].emailEnabled")) {
                        errors.rejectValue(
                                "cICustomers[" + cICustomersIndex + "].emailEnabled",
                                ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { "false" }, "");
                    }
                    if (cICust.isPhoneCallEnabled()
                            && !errors.hasFieldErrors("cICustomers[" + cICustomersIndex + "].phoneCallEnabled")) {
                        errors.rejectValue(
                                "cICustomers[" + cICustomersIndex + "].phoneCallEnabled",
                                ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { "false" }, "");
                    }
                }

            }
        }
    }

    private void validateContacts(Errors errors, Contact cont, int contactIndex, CICustomer cICust, int cICustomersIndex,
            boolean isParentCICustomer) {

        // validate empty Contact Id

        if (isParentCICustomer) {
            yukonApiValidationUtils.checkIfFieldRequired(
                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].id", errors, cont.getId(),
                    "Contact Id");

        } else {
            yukonApiValidationUtils.checkIfFieldRequired(
                    "unassignedContacts[" + contactIndex + "].id", errors, cont.getId(),
                    "Unassigned Contact Id");

        }
        if (isParentCICustomer) {
            // validate whether contact id exists
            if (!errors.hasFieldErrors("cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].id")) {
                validateContactId(cont, contactIndex, cICust, cICustomersIndex, errors, isParentCICustomer);
            }

            // email enabled and phone call enabled for contact is same as customer if CI Cust is selected true
            // not required for unassigned Contact since it does not have a parent
            validateEmailPhoneCallEnabledForContactFromCustomer(cICust, cont, cICustomersIndex, contactIndex, errors);

            // Validate if customer is selected, contact connot be selected
            // not required for unassigned Contact since it does not have a parent
            validateWhenSelectedTrueForCustomerAndContact(errors, cont, cICust, contactIndex, cICustomersIndex);

        } else {
            if (!errors.hasFieldErrors("unassignedContacts[" + contactIndex + "].id")) {
                validateContactId(cont, contactIndex, null, 0, errors, isParentCICustomer);
            }
        }
        if ((cont.getNotifications() != null && !cont.getNotifications().isEmpty())) {
            for (int notifIndex = 0; notifIndex < cont.getNotifications().size(); notifIndex++) {
                // email and phone call enable is false at parent level if selected true at child level
                validateEmailPhoneCallEnabledForContactFromNotification(errors, cont.getNotifications().get(notifIndex),
                        notifIndex,
                        cont,
                        contactIndex, cICust, cICustomersIndex, isParentCICustomer);

                // validation notifications
                validateNotification(errors, cont.getNotifications().get(notifIndex), notifIndex, cont, contactIndex, cICust,
                        cICustomersIndex,
                        isParentCICustomer);
            }

        }
    }

    private void validateEmailPhoneCallEnabledForContactFromCustomer(CICustomer cICust, Contact cont, int cICustomersIndex,
            int contactIndex,
            Errors errors) {
        if (cICust.isSelected()) {
            if (cont.isEmailEnabled() != cICust.isEmailEnabled()) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].emailEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { cICust.isEmailEnabled() }, "");
            }
            if (cont.isPhoneCallEnabled() != cICust.isPhoneCallEnabled()) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].phoneCallEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { cICust.isPhoneCallEnabled() }, "");
            }
        }

    }

    private void validateContactId(Contact cont, int contactIndex, CICustomer cICust, int cICustomersIndex,
            Errors errors, boolean isParentCICustomer) {
        LiteContact contactExists = null;
        try {
            contactExists = contactDao.getContact(cont.getId());
        } catch (Exception e) {
            if (isParentCICustomer) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].id",
                        ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { cont.getId() }, "");
            } else {
                errors.rejectValue(
                        "unassignedContacts[" + contactIndex + "].id",
                        ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { cont.getId() }, "");
            }
        }
        // validate contact with Ci Cust only when it has a parent
        if (isParentCICustomer) {
            if (contactExists != null) {
                LiteCustomer validCICust = databaseCache.getACustomerByPrimaryContactID(cont.getId());
                if (validCICust == null) {
                    errors.rejectValue("cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].id",
                            ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                            new Object[] { cont.getId() }, null);
                } else if (validCICust != null && cICust.getId() != null) {
                    if (validCICust.getCustomerID() != cICust.getId()
                            || validCICust.getCustomerTypeID() != CustomerTypes.CUSTOMER_CI) {
                        errors.rejectValue("cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].id",
                                ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                                new Object[] { cont.getId() }, null);

                    }
                }
            }
        }

    }

    private void validateEmailPhoneCallEnabledForContactFromNotification(Errors errors,
            NotificationSettings notif, int notifIndex, Contact cont, int contactIndex,
            CICustomer cICust, int cICustomersIndex, boolean isParentCICustomer) {
        // email and phone call enable is false at parent level if selected true at child level
        if (!cont.isSelected()) {
            if (notif.isSelected()) {
                {
                    if (isParentCICustomer) {
                        if (cont.isEmailEnabled() && !errors.hasFieldErrors(
                                "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].emailEnabled")) {
                            errors.rejectValue(
                                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].emailEnabled",
                                    ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { "false" }, "");
                        }
                        if (cont.isPhoneCallEnabled()
                                && !errors.hasFieldErrors(
                                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex
                                                + "].phoneCallEnabled")) {
                            errors.rejectValue(
                                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].phoneCallEnabled",
                                    ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { "false" }, "");
                        }
                    } else {
                        if (cont.isEmailEnabled()) {
                            errors.rejectValue(
                                    "unassignedContacts[" + contactIndex + "].emailEnabled",
                                    ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { "false" }, "");
                        }
                        if (cont.isPhoneCallEnabled()) {
                            errors.rejectValue(
                                    "unassignedContacts[" + contactIndex + "].phoneCallEnabled",
                                    ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { "false" }, "");
                        }
                    }
                }
            }
        }
    }

    private void validateNotification(Errors errors, NotificationSettings notif, int notifIndex, Contact cont,
            int contactIndex,
            CICustomer cICust, int cICustomersIndex, boolean isParentCICustomer) {

        // validate empty id
        if (isParentCICustomer) {
            yukonApiValidationUtils.checkIfFieldRequired(
                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications[" + notifIndex
                            + "].id",
                    errors, notif.getId(),
                    "Notification Id");
        } else {
            yukonApiValidationUtils.checkIfFieldRequired(
                    "unassignedContacts[" + contactIndex + "].notifications[" + notifIndex
                            + "].id",
                    errors, notif.getId(),
                    "Notification Id");
        }

        // validate for notification id
        LiteContactNotification liteNotifObject = null;
        if (!errors.hasFieldErrors(
                "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications[" + notifIndex
                        + "].id")
                || (!isParentCICustomer && !errors.hasFieldErrors(
                        "unassignedContacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].id"))) {

            liteNotifObject = validateContactNotificationId(notif, notifIndex, errors,
                    cont, contactIndex, cICustomersIndex, isParentCICustomer);

        }

        if (isParentCICustomer) {
            // validate email enabled and phone call enabled when selected true for Ci Cust
            validateEmailPhoneCallEnabledForNotificationFromCustomer(cICust, cICustomersIndex, cont, contactIndex, notifIndex,
                    errors,
                    notif);
        }
        // validate email enabled and phone call enabled one level up i.e. when selected true for Contact
        validateEmailPhoneCallEnabledForNotificationFromContact(errors, notif, notifIndex, cont, contactIndex,
                cICustomersIndex, isParentCICustomer);

        // if selected is true for either customer or contact, then it cannot be true for notification
        validateWhenSelectedTrueForCustomerContactAndNotification(errors, notif, notifIndex, cont, contactIndex, cICust,
                cICustomersIndex, isParentCICustomer);

        // validate email enabled phone call enabled on category if selected true
        if (notif.isSelected()) {
            if (liteNotifObject != null) {
                if (liteNotifObject.getNotificationCategoryID() != 0) {
                    validateEmailPhoneCallEnabledOnType(liteNotifObject, errors, notif,
                            notifIndex, contactIndex,
                            cICustomersIndex, isParentCICustomer);

                }
            }
        }
    }

    private void validateWhenSelectedTrueForCustomerContactAndNotification(Errors errors, NotificationSettings notif,
            int notifIndex, Contact cont, int contactIndex, CICustomer cICust, int cICustomersIndex, boolean isParentCICustomer) {
        if (isParentCICustomer && (cICust.isSelected() || cont.isSelected()) && notif.isSelected()) {
            errors.rejectValue(
                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                            + notifIndex
                            + "].selected",
                    ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { "false" }, "");
        } else if (!isParentCICustomer) {
            if (cont.isSelected() && notif.isSelected()) {
                errors.rejectValue(
                        "unassignedContacts[" + contactIndex + "].notifications["
                                + notifIndex
                                + "].selected",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "false" }, "");
            }

        }

    }

    private void validateEmailPhoneCallEnabledForNotificationFromContact(Errors errors, NotificationSettings notif,
            int notifIndex, Contact cont,
            int contactIndex, int cICustomersIndex, boolean isParentCICustomer) {
        if (cont.isSelected()) {
            if (notif.isEmailEnabled() != cont.isEmailEnabled() && (!errors
                    .hasFieldErrors("cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                            + notifIndex + "].emailEnabled")
                    || (!isParentCICustomer && !errors
                            .hasFieldErrors("unassignedContacts[" + contactIndex + "].notifications["
                                    + notifIndex + "].emailEnabled")))) {
                if (isParentCICustomer) {
                    errors.rejectValue(
                            "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                                    + notifIndex + "].emailEnabled",
                            ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { cont.isEmailEnabled() }, "");
                } else {
                    errors.rejectValue(
                            "unassignedContacts[" + contactIndex + "].notifications["
                                    + notifIndex + "].emailEnabled",
                            ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { cont.isEmailEnabled() }, "");
                }
            }
            if (notif.isPhoneCallEnabled() != cont.isPhoneCallEnabled() && (!errors.hasFieldErrors(
                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                            + notifIndex + "].phoneCallEnabled")
                    || (!isParentCICustomer && !errors.hasFieldErrors(
                            "unassignedContacts[" + contactIndex + "].notifications["
                                    + notifIndex + "].phoneCallEnabled")))) {
                if (isParentCICustomer) {
                    errors.rejectValue(
                            "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                                    + notifIndex + "].phoneCallEnabled",
                            ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { cont.isPhoneCallEnabled() }, "");
                } else {
                    errors.rejectValue(
                            "unassignedContacts[" + contactIndex + "].notifications["
                                    + notifIndex + "].phoneCallEnabled",
                            ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { cont.isPhoneCallEnabled() }, "");
                }

            }
        }

    }

    private void validateEmailPhoneCallEnabledForNotificationFromCustomer(CICustomer cICust, int cICustomersIndex, Contact cont,
            int contactIndex,
            int notifIndex, Errors errors, NotificationSettings notif) {
        if (cICust.isSelected()) {
            if (notif.isEmailEnabled() != cICust.isEmailEnabled()) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                                + notifIndex + "].emailEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { cICust.isEmailEnabled() }, "");
            }
            if (!errors.hasFieldErrors(
                    "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                            + notifIndex + "].phoneCallEnabled")
                    && (notif.isPhoneCallEnabled() != cICust.isPhoneCallEnabled())) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                                + notifIndex + "].phoneCallEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { cICust.isPhoneCallEnabled() }, "");
            }
        }

    }

    private LiteContactNotification validateContactNotificationId(NotificationSettings notif,
            int notifIndex, Errors errors, Contact cont, int contactIndex, int cICustomersIndex, boolean isParentCICustomer) {
        int notifId = notif.getId();
        LiteContactNotification liteNotifObject = null;
        boolean notifExists = databaseCache.getAllContactNotifsMap().keySet().stream()
                .anyMatch(obj -> obj.intValue() == notifId);
        if (!notifExists) {
            if (isParentCICustomer) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].id",
                        ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { notifId }, "");
            } else {
                errors.rejectValue(
                        "unassignedContacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].id",
                        ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { notifId }, "");
            }
        }
        // validate parent for notification
        if ((!isParentCICustomer && !errors
                .hasFieldErrors("unassignedContacts[" + contactIndex + "].notifications[" + notifIndex
                        + "].id"))
                || !errors
                        .hasFieldErrors(
                                "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications[" + notifIndex
                                        + "].id")) {
            try {
                liteNotifObject = contactNotificationDao
                        .getNotificationForContact(notif.getId());
            } catch (Exception e) {
                if (isParentCICustomer) {
                    errors.rejectValue(
                            "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                                    + notifIndex
                                    + "].id",
                            ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                            new Object[] { notif }, null);
                } else {
                    errors.rejectValue(
                            "unassignedContacts[" + contactIndex + "].notifications["
                                    + notifIndex
                                    + "].id",
                            ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                            new Object[] { notif.getId() }, null);
                }
            }
            // validate notification id with contact id
            if (liteNotifObject != null && cont.getId() != null) {
                if (liteNotifObject.getContactID() != cont.getId()) {
                    if (isParentCICustomer) {
                        errors.rejectValue(
                                "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications["
                                        + notifIndex
                                        + "].id",
                                ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                                new Object[] { notif.getId() }, null);
                    } else {
                        errors.rejectValue(
                                "unassignedContacts[" + contactIndex + "].notifications["
                                        + notifIndex
                                        + "].id",
                                ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                                new Object[] { notif.getId() }, null);
                    }
                }
            }
        }
        return liteNotifObject;
    }

    public void validateNotificationGroupName(Errors errors, String notificationGroupName, Integer notificationGroupId) {
        if (!errors.hasFieldErrors("name")) {
            // validate name
            String nameI18nText = accessor.getMessage(commonKey + "name");
            yukonApiValidationUtils.checkIfFieldRequired(nameI18nText, errors, notificationGroupName, nameI18nText);

            if (StringUtils.isNotBlank(notificationGroupName)) {
                yukonApiValidationUtils.checkExceedsMaxLength(errors, nameI18nText, notificationGroupName, 40);
            }
        }

        databaseCache.getAllContactNotificationGroups()
                .stream()
                .filter(liteGroup -> liteGroup.getNotificationGroupName().equalsIgnoreCase(notificationGroupName.trim()))
                .findAny()
                .ifPresent(group -> {
                    if (notificationGroupId == null || group.getNotificationGroupID() != notificationGroupId) {
                        errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                                new Object[] { notificationGroupName }, "");
                    }
                });
    }

    private void validateEmailPhoneCallEnabledOnType(LiteContactNotification liteNotifObject, Errors errors,
            NotificationSettings notif, int notifIndex, int contactIndex, int cICustomersIndex, boolean isParentCICustomer) {
        String emailTypeNotificationI18nText = accessor.getMessage(notificationGroupKey + "invalidPhoneCallNotifcation");
        String phoneTypeNotificationI18nText = accessor.getMessage(notificationGroupKey + "invalidEmailNotifcation");

        if ((liteNotifObject.getContactNotificationType().isEmailType() ||
                liteNotifObject.getContactNotificationType().isShortEmailType())
                && notif.isPhoneCallEnabled()) {
            if (isParentCICustomer) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].phoneCallEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { emailTypeNotificationI18nText },
                        "");
            } else {
                errors.rejectValue(
                        "unassignedContacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].phoneCallEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { emailTypeNotificationI18nText },
                        "");
            }
        } else if (liteNotifObject.getContactNotificationType().isPhoneType() && notif.isEmailEnabled()) {
            if (isParentCICustomer) {
                errors.rejectValue(
                        "cICustomers[" + cICustomersIndex + "].contacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].emailEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { phoneTypeNotificationI18nText },
                        "");
            } else {
                errors.rejectValue(
                        "unassignedContacts[" + contactIndex + "].notifications[" + notifIndex
                                + "].phoneCallEnabled",
                        ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { emailTypeNotificationI18nText },
                        "");

            }
        }
    }
}