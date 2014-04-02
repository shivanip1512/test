package com.cannontech.web.stars.dr.consumer.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.util.Validator;

public class LiteContactValidator extends SimpleValidator<LiteContact> {

	public static final int MAX_NOTIFICATION_LENGTH = 130;
	
	@Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
	
	public LiteContactValidator() {
		super(LiteContact.class);
	}

	@Override
	public void doValidation(LiteContact contact, Errors errors) {

		// first name
		if (contact.getContactID() <= 0 && StringUtils.isBlank(contact.getContFirstName())) {
			errors.rejectValue("contFirstName", "yukon.web.modules.consumer.contacts.error.firstNameRequired");
		}
		
		YukonValidationUtils.checkExceedsMaxLength(errors, "contFirstName", contact.getContFirstName(), 120);
		
		// last name
		YukonValidationUtils.checkExceedsMaxLength(errors, "contLastName", contact.getContLastName(), 120);
		
		// other notifications
		int i = 0;
		for(LiteContactNotification notification : contact.getLiteContactNotifications()){
			errors.pushNestedPath("liteContactNotifications[" + i++ + "]");

			String notificationValue = notification.getNotification();
			
			//check type
			if (notification.getContactNotificationType() == null && StringUtils.isNotBlank(notificationValue)) {
				errors.rejectValue("notificationCategoryID", "yukon.web.modules.operator.contact.error.noNotificationMethod");
			}else{
				//check min length
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "notification", "yukon.web.modules.consumer.contacts.error.blankNotification");
			}
			
			//check max length
			YukonValidationUtils.checkExceedsMaxLength(errors, "notification", notificationValue, MAX_NOTIFICATION_LENGTH);
			
			switch(notification.getContactNotificationType().getContactNotificationMethodType()){
			case FAX:
			case PHONE:
				if (phoneNumberFormattingService.isHasInvalidCharacters(notificationValue)) {
					errors.rejectValue("notification", "yukon.web.modules.consumer.contacts.error.invalidPhoneNumber");
				}
				break;
			case EMAIL:
				if (!Validator.isEmailAddress(notificationValue)) {
		            errors.rejectValue("notification", "yukon.web.modules.consumer.contacts.error.emailInvalid");
		        }
				break;
			default: break;
			}
			
			errors.popNestedPath();
		}
	}
}
