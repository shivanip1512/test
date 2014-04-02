package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

public class ContactNotificationDtoValidator extends SimpleValidator<ContactNotificationDto> {

    public static final int MAX_NOTIFICATION_LENGTH = 130;

    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;

    public ContactNotificationDtoValidator() {
        super(ContactNotificationDto.class);
    }

    @Override
    public void doValidation(ContactNotificationDto contactNotificationDto, Errors errors) {

        String notificationValue = contactNotificationDto.getNotificationValue();
        ContactNotificationType contactNotificationType = contactNotificationDto.getContactNotificationType();

        if (contactNotificationType == null) {
            if (StringUtils.isNotBlank(notificationValue)) {
                errors.rejectValue("contactNotificationType", "yukon.web.modules.operator.contact.error.noNotificationMethod");
            }
            return;
        }

        if (StringUtils.isBlank(notificationValue)) {
            errors.rejectValue("notificationValue", "yukon.web.modules.operator.contact.error.notificationValueBlank");
            return;
        }

        if (notificationValue.length() > MAX_NOTIFICATION_LENGTH) {
            errors.rejectValue("notificationValue", "yukon.web.error.exceedsMaximumLength", new Object[]{MAX_NOTIFICATION_LENGTH}, "Exceeds maximum length of " + MAX_NOTIFICATION_LENGTH);
        }

        if (contactNotificationType.isPhoneType() || contactNotificationType.isFaxType()) {
            if (phoneNumberFormattingService.isHasInvalidCharacters(notificationValue)) {
                errors.rejectValue("notificationValue", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
            }

        } else if (contactNotificationType.isEmailType() || contactNotificationType.isShortEmailType()) {
            if (!emailValidator.isValid(notificationValue)) {
                errors.rejectValue("notificationValue", "yukon.web.modules.operator.accountGeneral.email.invalid");
            }
        }
    }
}
