package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.web.stars.dr.operator.model.ContactDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

public class ContactDtoValidator extends SimpleValidator<ContactDto> {

	private PhoneNumberFormattingService phoneNumberFormattingService;
	private ContactNotificationDtoValidator contactNotificationDtoValidator;
	
	public ContactDtoValidator() {
		super(ContactDto.class);
	}

	@Override
	public void doValidation(ContactDto contactDto, Errors errors) {

		// first name on create
		if (contactDto.getContactId() <= 0 && StringUtils.isBlank(contactDto.getFirstName())) {
			errors.rejectValue("firstName", "yukon.web.modules.operator.contactEdit.firstNameRequired");
		}
		
		// home phone number
		String homePhone = contactDto.getHomePhone();
		if (homePhone != null && phoneNumberFormattingService.isHasInvalidCharacters(homePhone)) {
			errors.rejectValue("homePhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		YukonValidationUtils.checkExceedsMaxLength(errors, "homePhone", homePhone, ContactNotificationDtoValidator.MAX_NOTIFICATION_LENGTH);
		
		// work phone number
		String workPhone = contactDto.getWorkPhone();
		if (workPhone != null && phoneNumberFormattingService.isHasInvalidCharacters(workPhone)) {
			errors.rejectValue("workPhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		YukonValidationUtils.checkExceedsMaxLength(errors, "workPhone", workPhone, ContactNotificationDtoValidator.MAX_NOTIFICATION_LENGTH);
		
		// email
		String email = contactDto.getEmail();
		YukonValidationUtils.checkExceedsMaxLength(errors, "email", email, ContactNotificationDtoValidator.MAX_NOTIFICATION_LENGTH);
		
		
		// other notifications
		int i = 0;
		for (ContactNotificationDto contactNotificationDto : contactDto.getOtherNotifications()) {
			
			errors.pushNestedPath("otherNotifications[" + i++ + "]");
			contactNotificationDtoValidator.validate(contactNotificationDto, errors);
			errors.popNestedPath();
		}
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
	
	@Autowired
	public void setContactNotificationDtoValidator(ContactNotificationDtoValidator contactNotificationDtoValidator) {
		this.contactNotificationDtoValidator = contactNotificationDtoValidator;
	}

}
