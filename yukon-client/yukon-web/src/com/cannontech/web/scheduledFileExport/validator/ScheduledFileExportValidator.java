package com.cannontech.web.scheduledFileExport.validator;

import java.text.SimpleDateFormat;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.validation.Errors;

import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.util.WebFileUtils;

public class ScheduledFileExportValidator extends SimpleValidator<ScheduledFileExportData>{

	public ScheduledFileExportValidator() {
		super(ScheduledFileExportData.class);
	}
	
	@Override
	protected void doValidation(ScheduledFileExportData target, Errors errors) {
		YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.modules.amr.billing.schedule.validation.invalidName");
		YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", target.getScheduleName(), 100);
		
		YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "exportFileName", "yukon.web.modules.amr.billing.schedule.validation.invalidFileName");
		YukonValidationUtils.checkExceedsMaxLength(errors, "exportFileName", target.getExportFileName(), 100);

        // First, just validate the Export File name
        String fileName = target.getExportFileName();
        boolean isValidFileName = WebFileUtils.isValidWindowsFilename(fileName);
        if(!isValidFileName) {
            errors.rejectValue("exportFileName", "yukon.web.modules.amr.billing.schedule.validation.badCharacters");
        }
        // Next, validate the combined Export File Name + Timestamp (if checked)
        if (target.isAppendDateToFileName()) {
            // Validate to make sure the pattern is actually a valid date format pattern
            String tsPattern = target.getTimestampPatternField();
            try {
                new SimpleDateFormat(tsPattern);
            } catch (IllegalArgumentException e) {
                errors.rejectValue("timestampPatternField", "yukon.web.modules.amr.billing.schedule.validation.invalidPattern");
            }
            // Now make sure it doesn't contain characters invalid for a filename, such as : or /
            fileName = fileName + tsPattern;
            isValidFileName = WebFileUtils.isValidWindowsFilename(fileName);
            if (!isValidFileName) {
                errors.rejectValue("timestampPatternField", "yukon.web.modules.amr.billing.schedule.validation.badCharacters");
            }
        }

        if (target.isIncludeExportCopy()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "exportPath", "yukon.web.modules.amr.billing.schedule.validation.invalidExportPath");
        }
        
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
