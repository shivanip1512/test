package com.cannontech.web.stars.dr.operator.inventory;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.inventory.model.CommandScheduleWrapper;

public class CommandScheduleValidator extends SimpleValidator<CommandScheduleWrapper> {
    
    public CommandScheduleValidator() {
        super(CommandScheduleWrapper.class);
    }

    @Override
    protected void doValidation(CommandScheduleWrapper target, Errors errors) {
        
        /* Validate Hours */
        if (target.getRunPeriodHours() < 0) {
            errors.rejectValue("runPeriodHours", "yukon.web.modules.operator.commandSchedule.error.lessThanZero.hours");
        }
        
        /* Validate Minutes */
        if (target.getRunPeriodMinutes() < 0) {
            errors.rejectValue("runPeriodMinutes", "yukon.web.modules.operator.commandSchedule.error.lessThanZero.minutes");
        }
        
        /* Validate Seconds */
        if (target.getDelayPeriodSeconds() < 0) {
            errors.rejectValue("delayPeriodSeconds", "yukon.web.modules.operator.commandSchedule.error.lessThanZero.seconds");
        }
        
        /* Validate Duration */
        if (target.getRunPeriodMinutes() + target.getRunPeriodHours() <= 0) {
            errors.rejectValue("runPeriodHours", "yukon.web.modules.operator.commandSchedule.error.durationLessThanZero.hours");
        }
        
    }
    
}