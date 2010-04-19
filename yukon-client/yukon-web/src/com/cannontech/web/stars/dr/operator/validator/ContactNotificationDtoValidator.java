package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

public class ContactNotificationDtoValidator extends SimpleValidator<ContactNotificationDto> {
	
	public static final int MAX_NOTIFICATION_LENGTH = 130;

	private PhoneNumberFormattingService phoneNumberFormattingService;
	
	public ContactNotificationDtoValidator() {
		super(ContactNotificationDto.class);
	}
	
	@Override
	public void doValidation(ContactNotificationDto contactNotificationDto, Errors errors) {
		
		String notificationValue = contactNotificationDto.getNotificationValue();
		ContactNotificationType contactNotificationType = contactNotificationDto.getContactNotificationType();
		
		if (contactNotificationType == null && StringUtils.isNotBlank(notificationValue)) {
			errors.rejectValue("contactNotificationType", "yukon.web.modules.operator.contact.error.noNotificationMethod");
		}
		
		if (notificationValue != null && contactNotificationType != null && (contactNotificationType.isPhoneType() || contactNotificationType.isFaxType())) {
			if (phoneNumberFormattingService.isHasInvalidCharacters(notificationValue)) {
				errors.rejectValue("notificationValue", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
			}
		}
		
		YukonValidationUtils.checkExceedsMaxLength(errors, "notificationValue", notificationValue, MAX_NOTIFICATION_LENGTH);
	}
	
	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
}
