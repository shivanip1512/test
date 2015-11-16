package com.cannontech.web.stars.dr.operator.validator;

import static com.cannontech.web.stars.dr.operator.validator.ContactNotificationDtoValidator.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;


/**
 * @date 2013May30R Added email validations, but only runs them if the email is not empty (null, "")
 * 
 * NOTE: If using this embedded within another DTO, surround calls to .doValidation(..) with
 *         errors.pushNestedPath("contact");    // if ContactDto is called "contact" within the container.
 */
public class ContactDtoValidator extends SimpleValidator<ContactDto> {
    private final static int FIRST_NAME_MAX_LENGTH = 120;
    private final static int LAST_NAME_MAX_LENGTH = 120;
    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
    @Autowired private ContactNotificationDtoValidator contactNotificationDtoValidator;
    
    public ContactDtoValidator() {
        super(ContactDto.class);
    }

    @Override
    public void doValidation(ContactDto contactDto, Errors errors) {

        // first name
        if (contactDto.getContactId() <= 0 && StringUtils.isBlank(contactDto.getFirstName())) {
            errors.rejectValue("firstName", "yukon.web.modules.operator.contactEdit.firstNameRequired");
        } else {
            YukonValidationUtils.checkExceedsMaxLength(errors, "firstName", contactDto.getFirstName(), FIRST_NAME_MAX_LENGTH);
        }

        // last name
        YukonValidationUtils.checkExceedsMaxLength(errors, "lastName", contactDto.getLastName(), LAST_NAME_MAX_LENGTH);

        // home phone number
        String homePhone = contactDto.getHomePhone();
        if (homePhone != null && phoneNumberFormattingService.isHasInvalidCharacters(homePhone)) {
            errors.rejectValue("homePhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "homePhone", homePhone, MAX_NOTIFICATION_LENGTH);

        // work phone number
        String workPhone = contactDto.getWorkPhone();
        if (workPhone != null && phoneNumberFormattingService.isHasInvalidCharacters(workPhone)) {
            errors.rejectValue("workPhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "workPhone", workPhone, MAX_NOTIFICATION_LENGTH);

        // email
        String email = contactDto.getEmail();
        if (!StringUtils.isEmpty(email)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "email", email, MAX_NOTIFICATION_LENGTH);
            if (!emailValidator.isValid(email)) {
                errors.rejectValue("email", "yukon.web.modules.operator.accountGeneral.email.invalid");
            }
        }

        // other notifications
        int i = 0;
        for (ContactNotificationDto contactNotificationDto : contactDto.getOtherNotifications()) {
            errors.pushNestedPath("otherNotifications[" + i++ + "]");
            contactNotificationDtoValidator.validate(contactNotificationDto, errors);
            errors.popNestedPath();
        }
    }
}
