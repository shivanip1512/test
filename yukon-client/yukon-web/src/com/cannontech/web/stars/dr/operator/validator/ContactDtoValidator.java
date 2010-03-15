package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.web.common.validation.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.model.ContactDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

public class ContactDtoValidator implements Validator {

	private PhoneNumberFormattingService phoneNumberFormattingService;
	private static final int MAX_NOTIFICATION_LENGTH = 130;
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return ContactDto.class.isAssignableFrom(clazz); 
	}

	@Override
	public void validate(Object target, Errors errors) {

		ContactDto contactDto = (ContactDto)target;
		
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
			
			String field = "otherNotifications[" + i + "].notificationValue";
			
			ContactNotificationDto contactNotificationDto = otherNotifications.get(i);
			String notificationValue = contactNotificationDto.getNotificationValue();
			ContactNotificationType contactNotificationType = contactNotificationDto.getContactNotificationType();
			
			if (notificationValue != null && contactNotificationType != null && contactNotificationType.isPhoneType()) {
				if (phoneNumberFormattingService.isHasInvalidCharacters(notificationValue)) {
					errors.rejectValue(field, "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
				}
			}
			
			YukonValidationUtils.checkExceedsMaxLength(errors, field, notificationValue, MAX_NOTIFICATION_LENGTH);
		}
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}

}
