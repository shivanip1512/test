package com.cannontech.web.stars.dr.operator.inventoryOperations;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.CommandScheduleWrapper;

public class CommandScheduleValidator extends SimpleValidator<CommandScheduleWrapper> {
    
    public CommandScheduleValidator() {
        super(CommandScheduleWrapper.class);
    }

    @Override
    protected void doValidation(CommandScheduleWrapper target, Errors errors) {
        
        /* Validate Required */
        if (StringUtils.isBlank(target.getHours()) && StringUtils.isBlank(target.getMinutes())) {
            errors.rejectValue("hours", "yukon.web.modules.operator.commandSchedule.error.required.hours");
            errors.rejectValue("minutes", "yukon.web.modules.operator.commandSchedule.error.required.minutes");
        }
        
        if (StringUtils.isBlank(target.getSeconds())) {
            errors.rejectValue("seconds", "yukon.web.modules.operator.commandSchedule.error.required.seconds");
        }
        
        /* Validate Hours */
        int hourValue = 0;
        if(StringUtils.isNotBlank(target.getHours())) {
            try {
                int hours = Integer.parseInt(target.getHours());
                hourValue = hours;
                if (hours < 0) {
                    errors.rejectValue("hours", "yukon.web.modules.operator.commandSchedule.error.lessThanZero.hours");
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("hours", "yukon.web.modules.operator.commandSchedule.error.NaN.hours");
            }
        }
        
        /* Validate Minutes */
        int minuteValue = 0;
        if(StringUtils.isNotBlank(target.getMinutes())) {
            try {
                int minutes = Integer.parseInt(target.getMinutes());
                minuteValue = minutes;
                if (minutes < 0) {
                    errors.rejectValue("minutes", "yukon.web.modules.operator.commandSchedule.error.lessThanZero.minutes");
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("minutes", "yukon.web.modules.operator.commandSchedule.error.NaN.minutes");
            }
        }
        
        /* Validate Seconds */
        try {
            int seconds = Integer.parseInt(target.getSeconds());
            if (seconds < 0) {
                errors.rejectValue("seconds", "yukon.web.modules.operator.commandSchedule.error.lessThanZero.seconds");
            }
        } catch (NumberFormatException e) {
            errors.rejectValue("seconds", "yukon.web.modules.operator.commandSchedule.error.NaN.seconds");
        }
        
        if(errors.hasErrors()) {
            return;
        }
        
        /* Validate Duration */
        if (hourValue + minuteValue <= 0) {
            errors.rejectValue("hours", "yukon.web.modules.operator.commandSchedule.error.durationLessThanZero.hours");
        }
        
    }
    
}