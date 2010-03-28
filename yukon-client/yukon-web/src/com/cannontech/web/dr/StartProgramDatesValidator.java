package com.cannontech.web.dr;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;

public class StartProgramDatesValidator extends SimpleValidator<StartProgramBackingBeanBase> {
    public StartProgramDatesValidator() {
        super(StartProgramBackingBeanBase.class);
    }

    @Override
    public void doValidation(StartProgramBackingBeanBase startProgram, Errors errors) {
        if (!startProgram.isStartNow() && startProgram.getStartDate() == null) {
            ValidationUtils.rejectIfEmpty(errors, "startDate", "required");
        }
        if (startProgram.isScheduleStop()) {
            ValidationUtils.rejectIfEmpty(errors, "stopDate", "required");
        }
        Date startDate = startProgram.getStartDate();
        Date stopDate = startProgram.getActualStopDate();
        if (startDate != null && stopDate != null && !stopDate.after(startDate)) {
            errors.reject("startTimeNotBeforeStopTime");
        }
    }
}
