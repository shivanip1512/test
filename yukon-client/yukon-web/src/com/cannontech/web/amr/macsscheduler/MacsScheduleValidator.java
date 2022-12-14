package com.cannontech.web.amr.macsscheduler;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy;
import com.cannontech.amr.macsscheduler.model.MacsStopPolicy;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.util.WebFileUtils;

@Service
public class MacsScheduleValidator extends SimpleValidator<MacsSchedule> {
    
    private final static String scheduleKey = "yukon.web.modules.tools.schedule.";

    public MacsScheduleValidator() {
        super(MacsSchedule.class);
    }

    @Override
    public void doValidation(MacsSchedule schedule, Errors errors) {

        validateName(schedule, errors);
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryName", "yukon.web.error.isBlank");
        YukonValidationUtils.checkExceedsMaxLength(errors, "categoryName", schedule.getCategoryName(), 50);
        if (schedule.getStartPolicy().getPolicy() == MacsStartPolicy.StartPolicy.DAYOFMONTH) {
            YukonValidationUtils.checkRange(errors, "startPolicy.dayOfMonth", schedule.getStartPolicy().getDayOfMonth(), 1, 31, true);
        }
        if (schedule.getStopPolicy().getPolicy() == MacsStopPolicy.StopPolicy.DURATION) {
            YukonValidationUtils.checkIsPositiveInt(errors, "stopPolicy.duration", schedule.getStopPolicy().getDuration());
        }
        if (schedule.isScript()) {
            validateFileName(schedule, errors);
            if (!schedule.getTemplate().isNoTemplateSelected()) {
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scriptOptions.groupName", "yukon.web.error.isBlank");
                YukonValidationUtils.checkIsPositiveInt(errors, "scriptOptions.porterTimeout", schedule.getScriptOptions().getPorterTimeout());
                if (schedule.getScriptOptions().isDemandResetSelected()) {
                    YukonValidationUtils.checkRange(errors, "scriptOptions.demandResetRetryCount", schedule.getScriptOptions().getDemandResetRetryCount(), 0, 5, true);
                }
                if (!schedule.getTemplate().isRetry()) {
                    YukonValidationUtils.checkRange(errors, "scriptOptions.retryCount", schedule.getScriptOptions().getRetryCount(), 0, 10, true);
                    YukonValidationUtils.checkRange(errors, "scriptOptions.queueOffCount", schedule.getScriptOptions().getQueueOffCount(), 0, 10, true);
                    if (!errors.hasFieldErrors("scriptOptions.retryCount") && !errors.hasFieldErrors("scriptOptions.queueOffCount")) {
                        if (schedule.getScriptOptions().getQueueOffCount() > schedule.getScriptOptions().getRetryCount()) {
                            errors.rejectValue("scriptOptions.queueOffCount", scheduleKey + "scriptOptions.validation.queueOffCountLessThanRetryCount");
                        }
                    }
                    YukonValidationUtils.checkRange(errors, "scriptOptions.maxRetryHours", schedule.getScriptOptions().getMaxRetryHours(), -1, 10, true);
                }
                if (schedule.getScriptOptions().isBillingSelected()) {
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scriptOptions.billingGroupName", "yukon.web.error.isBlank");
                    YukonValidationUtils.checkIsPositiveInt(errors, "scriptOptions.billingDemandDays", schedule.getScriptOptions().getBillingDemandDays());
                    YukonValidationUtils.checkIsPositiveInt(errors, "scriptOptions.billingEnergyDays", schedule.getScriptOptions().getBillingEnergyDays());
                }
                if (schedule.getScriptOptions().isNotificationSelected()) {
                    if (schedule.getScriptOptions().getNotificationGroupId() == 0) {
                        errors.rejectValue("scriptOptions.notificationGroupId", "yukon.web.error.isBlank");
                    }
                }
            }
        } else {
            YukonValidationUtils.checkExceedsMaxLength(errors, "simpleOptions.startCommand", schedule.getSimpleOptions().getStartCommand(), 120);
            YukonValidationUtils.checkExceedsMaxLength(errors, "simpleOptions.stopCommand", schedule.getSimpleOptions().getStopCommand(), 120);
            YukonValidationUtils.checkIsPositiveInt(errors, "simpleOptions.repeatInterval", schedule.getSimpleOptions().getRepeatInterval());
        }
    }

    private void validateName(MacsSchedule schedule, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.error.isBlank");

        if (!errors.hasFieldErrors("scheduleName")) {
            if (!PaoUtils.isValidPaoName(schedule.getScheduleName())) {
                errors.rejectValue("scheduleName", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", schedule.getScheduleName(), 60);
    }
    
    private void validateFileName(MacsSchedule schedule, Errors errors) {
        String fileName = schedule.getScriptOptions().getFileName();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scriptOptions.fileName", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("scriptOptions.fileName")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "scriptOptions.fileName", fileName, 180);
            boolean validFileName = WebFileUtils.isValidWindowsFilename(fileName);
            if (!validFileName) {
                errors.rejectValue("scriptOptions.fileName", scheduleKey + "scriptOptions.validation.fileName.badCharacters");
            }
            if (!fileName.endsWith(".ctl")) {
                errors.rejectValue("scriptOptions.fileName", scheduleKey + "scriptOptions.validation.fileName.endsInCtl");
            }
        }

    }
}
