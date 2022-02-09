package com.cannontech.web.api.dr.constraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.HolidayUsage;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.api.dr.setup.LMApiValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintValidator extends SimpleValidator<ProgramConstraint> {

    @Autowired LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private IDatabaseCache dbCache;

    public ProgramConstraintValidator() {
        super(ProgramConstraint.class);
    }

    @Override
    protected void doValidation(ProgramConstraint programConstraint, Errors errors) {
        // Mandatory, max length(60) and special character check for name.
        lmApiValidatorHelper.validateName(programConstraint.getName(), errors, "Name");
        if (!errors.hasFieldErrors("name")) {
            dbCache.getAllLMProgramConstraints()
                    .stream()
                    .filter(constraint -> constraint.getConstraintName().equalsIgnoreCase(programConstraint.getName()))
                    .findAny()
                    .ifPresent(liteConstraint -> {
                        if (programConstraint.getId() == null || liteConstraint.getLiteID() != programConstraint.getId()) {
                            errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                                    new Object[] { "Name" }, "");
                        }
                    });
        }
        
        if (programConstraint.getSeasonSchedule() == null || programConstraint.getSeasonSchedule().getId() == null) {
            errors.rejectValue("seasonSchedule", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Season Schedule" }, "");
        }
        if (programConstraint.getHolidaySchedule() == null || programConstraint.getHolidaySchedule().getId() == null) {
            errors.rejectValue("holidaySchedule", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Holiday Schedule" }, "");
        }
        // Holiday schedule and holiday usage check.Holiday usage is mandatory when holiday schedule is
        // selected. When none select is selected id will be sent as 0
        if (!errors.hasFieldErrors("holidaySchedule")) {
            Integer holidayScheduleId = programConstraint.getHolidaySchedule().getId();
            if (holidayScheduleId != null && holidayScheduleId.compareTo(0) > 0) {
                YukonApiValidationUtils.checkIsBlank(errors, "holidayUsage", programConstraint.getHolidayUsage().toString(), false, "Holiday Usage");
                if (programConstraint.getHolidayUsage() == HolidayUsage.NONE) {
                    errors.rejectValue("holidayUsage", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Holiday Usage" }, "");
                }
            } else if (programConstraint.getHolidayUsage() != HolidayUsage.NONE) {
                errors.rejectValue("holidayUsage", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Holiday Usage" }, "");
            }
        }
        lmApiValidatorHelper.checkIfFieldRequired("maxActivateSeconds", errors, programConstraint.getMaxActivateSeconds(), "Max Activate");
        if (!errors.hasFieldErrors("maxActivateSeconds")) {
            YukonApiValidationUtils.checkRange(errors, "maxActivateSeconds", programConstraint.getMaxActivateSeconds(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("maxDailyOps", errors, programConstraint.getMaxDailyOps(), "Max Daily Ops");
        if (!errors.hasFieldErrors("maxDailyOps")) {
            YukonApiValidationUtils.checkRange(errors, "maxDailyOps", programConstraint.getMaxDailyOps(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("minActivateSeconds", errors, programConstraint.getMinActivateSeconds(), "Min Activate");
        if (!errors.hasFieldErrors("minActivateSeconds")) {
            YukonApiValidationUtils.checkRange(errors, "minActivateSeconds", programConstraint.getMinActivateSeconds(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("minRestartSeconds", errors, programConstraint.getMinRestartSeconds(), "Min Restart");
        if (!errors.hasFieldErrors("minRestartSeconds")) {
            YukonApiValidationUtils.checkRange(errors, "minRestartSeconds", programConstraint.getMinRestartSeconds(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("maxHoursDaily", errors, programConstraint.getMaxHoursDaily(), "Daily");
        if (!errors.hasFieldErrors("maxHoursDaily")) {
            YukonApiValidationUtils.checkRange(errors, "maxHoursDaily", programConstraint.getMaxHoursDaily(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("maxHoursMonthly", errors, programConstraint.getMaxHoursMonthly(), "Monthly");
        if (!errors.hasFieldErrors("maxHoursMonthly")) {
            YukonApiValidationUtils.checkRange(errors, "maxHoursMonthly", programConstraint.getMaxHoursMonthly(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("maxHoursAnnually", errors, programConstraint.getMaxHoursAnnually(), "Annually");
        if (!errors.hasFieldErrors("maxHoursAnnually")) {
            YukonApiValidationUtils.checkRange(errors, "maxHoursAnnually", programConstraint.getMaxHoursAnnually(), 0, 99999, false);
        }
        lmApiValidatorHelper.checkIfFieldRequired("maxHoursSeasonal", errors, programConstraint.getMaxHoursSeasonal(), "Seasonal");
        if (!errors.hasFieldErrors("maxHoursSeasonal")) {
            YukonApiValidationUtils.checkRange(errors, "maxHoursSeasonal", programConstraint.getMaxHoursSeasonal(), 0, 99999, false);
        }
    }
}
