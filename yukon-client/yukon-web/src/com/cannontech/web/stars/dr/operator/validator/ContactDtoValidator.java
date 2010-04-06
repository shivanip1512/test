package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.web.stars.dr.operator.model.ContactDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

public class ContactDtoValidator extends SimpleValidator<ContactDto> {

	private PhoneNumberFormattingService phoneNumberFormattingService;
	private static final int MAX_NOTIFICATION_LENGTH = 130;
	
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
		YukonValidationUtils.checkExceedsMaxLength(errors, "homePhone", homePhone, MAX_NOTIFICATION_LENGTH);
		
		// work phone number
		String workPhone = contactDto.getWorkPhone();
		if (workPhone != null && phoneNumberFormattingService.isHasInvalidCharacters(workPhone)) {
			errors.rejectValue("workPhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		YukonValidationUtils.checkExceedsMaxLength(errors, "workPhone", workPhone, MAX_NOTIFICATION_LENGTH);
		
		// email
		String email = contactDto.getEmail();
		YukonValidationUtils.checkExceedsMaxLength(errors, "email", email, MAX_NOTIFICATION_LENGTH);
		
		
		// other notifications
		List<ContactNotificationDto> otherNotifications = contactDto.getOtherNotifications();
		for (int i = 0; i < otherNotifications.size(); i++) {
			
			String valueField = "otherNotifications[" + i + "].notificationValue";
			String typeField = "otherNotifications[" + i + "].contactNotificationType";
			
			ContactNotificationDto contactNotificationDto = otherNotifications.get(i);
			String notificationValue = contactNotificationDto.getNotificationValue();
			ContactNotificationType contactNotificationType = contactNotificationDto.getContactNotificationType();
			
			if (contactNotificationType == null && StringUtils.isNotBlank(notificationValue)) {
				errors.rejectValue(typeField, "yukon.web.modules.operator.contactEdit.error.noNotificationMethod");
			}
			
			if (notificationValue != null && contactNotificationType != null && (contactNotificationType.isPhoneType() || contactNotificationType.isFaxType())) {
				if (phoneNumberFormattingService.isHasInvalidCharacters(notificationValue)) {
					errors.rejectValue(valueField, "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
				}
			}
			
			YukonValidationUtils.checkExceedsMaxLength(errors, valueField, notificationValue, MAX_NOTIFICATION_LENGTH);
		}
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}

}
