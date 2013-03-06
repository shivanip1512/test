package com.cannontech.web.scheduledFileExport.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.validation.Errors;

import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class ScheduledBillingFileExportValidator extends SimpleValidator<ScheduledFileExportData>{

	public ScheduledBillingFileExportValidator() {
		super(ScheduledFileExportData.class);
	}
	
	@Override
	protected void doValidation(ScheduledFileExportData target, Errors errors) {
		YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.modules.amr.billing.schedule.validation.invalidName");
		YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", target.getScheduleName(), 100);
		
		YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "exportPath", "yukon.web.modules.amr.billing.schedule.validation.invalidExportPath");
		
		YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "exportFileName", "yukon.web.modules.amr.billing.schedule.validation.invalidFileName");
		YukonValidationUtils.checkExceedsMaxLength(errors, "exportFileName", target.getScheduleName(), 100);
		
		for(String email : target.getNotificationEmailAddressesAsList()) {
			try {
				new InternetAddress(email).validate();
			} catch(AddressException e) {
				Object[] args = {email};
				errors.rejectValue("notificationEmailAddresses", "yukon.web.modules.amr.billing.schedule.validation.invalidEmailAddress", args, "");
			}
		}
	}
}
