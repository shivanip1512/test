package com.cannontech.web.scheduledFileExport.validator;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.validation.Errors;

import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.util.WebFileUtils;

public class ScheduledFileExportValidator extends SimpleValidator<ScheduledFileExportData>{

    private String source;
    
    public ScheduledFileExportValidator() {
        super(ScheduledFileExportData.class);
        source = null;
    }
    
    public ScheduledFileExportValidator(Class<?> c) {
        super(ScheduledFileExportData.class);
        source = c.getSimpleName();
    }
    
    @Override
    protected void doValidation(ScheduledFileExportData target, Errors errors) {
        
        switch (this.source) {
            case "MeterEventsReportController":
                if (errors.getFieldError("daysPrevious") == null) {
                    YukonValidationUtils.checkIsPositiveInt(errors, "daysPrevious", target.getDaysPrevious());
                }
                break;
            case "WaterLeakReportController":
                YukonValidationUtils.checkIsPositiveInt(errors, "daysOffset", target.getDaysOffset());
                YukonValidationUtils.checkIsPositiveInt(errors, "hoursPrevious", target.getHoursPrevious());
                YukonValidationUtils.checkIsPositiveDouble(errors, "threshold", target.getThreshold());
                YukonValidationUtils.checkRange(errors, "hoursPrevious", target.getHoursPrevious(), 1, Integer.MAX_VALUE, true);
                YukonValidationUtils.checkRange(errors, "daysOffset", target.getHoursPrevious(), 1, Integer.MAX_VALUE, true);
                break;
        }
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.modules.amr.billing.schedule.validation.invalidName");
        YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", target.getScheduleName(), 100);
        
        String fileName = target.getExportFileName();
        boolean exportFileNameError = YukonValidationUtils.checkIsBlank(errors, "exportFileName", fileName, false);
        exportFileNameError |= YukonValidationUtils.checkExceedsMaxLength(errors, "exportFileName", fileName, 100);

        // First, just validate the Export File name
        if(!exportFileNameError && !WebFileUtils.isValidWindowsFilename(fileName)) {
            errors.rejectValue("exportFileName", "yukon.web.modules.amr.billing.schedule.validation.badCharacters");
        }
        // Next, validate the Timestamp (if checked)
        if (target.isAppendDateToFileName()) {
            // If they are appending a timestamp, it cannot be blank.
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "timestampPatternField", "yukon.web.modules.amr.billing.schedule.validation.emptyTimestampPattern");
            YukonValidationUtils.checkExceedsMaxLength(errors, "timestampPatternField", target.getTimestampPatternField(), 100);
            // Validate to make sure the pattern is actually a valid date format pattern
            String tsPattern = target.getTimestampPatternField().trim();
            if (tsPattern.length() > 0) {
                try {
                    new SimpleDateFormat(tsPattern);
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("timestampPatternField", "yukon.web.modules.amr.billing.schedule.validation.invalidPattern");
                }
                // Now make sure it doesn't contain characters invalid for a filename, such as : or /
                if (!WebFileUtils.isValidWindowsFilename(fileName + tsPattern)) {
                    errors.rejectValue("timestampPatternField", "yukon.web.modules.amr.billing.schedule.validation.badCharacters");
                }
            }
        }

        if (target.isOverrideFileExtension()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "exportFileExtension", "yukon.web.modules.amr.billing.schedule.validation.emptyFileExtension");
            // make sure the file extension doesn't contain characters invalid for a windows filename.
            if (!WebFileUtils.isValidWindowsFilename(target.getExportFileExtension())) {
                errors.rejectValue("exportFileExtension", "yukon.web.modules.amr.billing.schedule.validation.badCharacters");
            }
        }

        if (target.isIncludeExportCopy()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "exportPath", "yukon.web.modules.amr.billing.schedule.validation.emptyExportPath");
            // make sure the path exists
            String expPath = target.getExportPath();
            if (!new File(expPath).isDirectory()) {
                errors.rejectValue("exportPath", "yukon.web.modules.amr.billing.schedule.validation.invalidExportPath");
            }
            
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
