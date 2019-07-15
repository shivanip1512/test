package com.cannontech.web.api.dr.constraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.HolidayUsage;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class ProgramConstraintValidator extends SimpleValidator<ProgramConstraint> {

    private final static String requiredKey = "yukon.web.modules.dr.setup.error.required";
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";
    @Autowired LMValidatorHelper lmValidatorHelper;

    public ProgramConstraintValidator() {
        super(ProgramConstraint.class);
    }

    @Override
    protected void doValidation(ProgramConstraint programConstraint, Errors errors) {
        // Mandatory, max length(60) and special character check for name.
        lmValidatorHelper.validateName(programConstraint.getName(), errors, "Constraint name");
        
        if (programConstraint.getSeasonSchedule() == null || programConstraint.getSeasonSchedule().getId() == null) {
            errors.rejectValue("seasonSchedule", requiredKey, new Object[] { "Season Schedule" }, "");
        }
        if (programConstraint.getHolidaySchedule() == null || programConstraint.getHolidaySchedule().getId() == null) {
            errors.rejectValue("holidaySchedule", requiredKey, new Object[] { "Holiday Schedule" }, "");
        }
        // Holiday schedule and holiday usage check.Holiday usage is mandatory when holiday schedule is
        // selected. When none select is selected id will be sent as 0
        if (!errors.hasFieldErrors("holidaySchedule")) {
            Integer holidayScheduleId = programConstraint.getHolidaySchedule().getId();
            if (holidayScheduleId != null && holidayScheduleId.compareTo(0) > 0) {
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "holidayUsage", requiredKey,
                    new Object[] { "Holiday Usage" });
                if (programConstraint.getHolidayUsage() == HolidayUsage.NONE) {
                    errors.rejectValue("holidayUsage", requiredKey, new Object[] { "Holiday Usage" }, "");
                }
            }else if(programConstraint.getHolidayUsage() != HolidayUsage.NONE){
                errors.rejectValue("holidayUsage", invalidKey, new Object[] { "Holiday Usage" }, "");
            }
        }
        lmValidatorHelper.checkIfFieldRequired("maxActivateSeconds", errors, programConstraint.getMaxActivateSeconds(), "Max Activate");
        if (!errors.hasFieldErrors("maxActivateSeconds")) {
            YukonValidationUtils.checkRange(errors, "maxActivateSeconds", programConstraint.getMaxActivateSeconds(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("maxDailyOpsSeconds", errors, programConstraint.getMaxDailyOpsSeconds(), "Max Daily Ops");
        if (!errors.hasFieldErrors("maxDailyOpsSeconds")) {
            YukonValidationUtils.checkRange(errors, "maxDailyOpsSeconds", programConstraint.getMaxDailyOpsSeconds(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("minActivateSeconds", errors, programConstraint.getMinActivateSeconds(), "Min Activate");
        if (!errors.hasFieldErrors("minActivateSeconds")) {
            YukonValidationUtils.checkRange(errors, "minActivateSeconds", programConstraint.getMinActivateSeconds(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("minRestartSeconds", errors, programConstraint.getMinRestartSeconds(), "Min Restart");
        if (!errors.hasFieldErrors("minRestartSeconds")) {
            YukonValidationUtils.checkRange(errors, "minRestartSeconds", programConstraint.getMinRestartSeconds(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("maxHoursDaily", errors, programConstraint.getMaxHoursDaily(), "Daily");
        if (!errors.hasFieldErrors("maxHoursDaily")) {
            YukonValidationUtils.checkRange(errors, "maxHoursDaily", programConstraint.getMaxHoursDaily(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("maxHoursMonthly", errors, programConstraint.getMaxHoursMonthly(), "Monthly");
        if (!errors.hasFieldErrors("maxHoursMonthly")) {
            YukonValidationUtils.checkRange(errors, "maxHoursMonthly", programConstraint.getMaxHoursMonthly(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("maxHoursAnnually", errors, programConstraint.getMaxHoursAnnually(), "Anually");
        if (!errors.hasFieldErrors("maxHoursAnnually")) {
            YukonValidationUtils.checkRange(errors, "maxHoursAnnually", programConstraint.getMaxHoursAnnually(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("maxHoursSeasonal", errors, programConstraint.getMaxHoursSeasonal(), "Seasonal");
        if (!errors.hasFieldErrors("maxHoursSeasonal")) {
            YukonValidationUtils.checkRange(errors, "maxHoursSeasonal", programConstraint.getMaxHoursSeasonal(), 0, 99999, false);
        }
    }
}
