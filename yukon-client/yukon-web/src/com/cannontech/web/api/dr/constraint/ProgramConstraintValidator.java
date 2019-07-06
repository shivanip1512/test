package com.cannontech.web.api.dr.constraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class ProgramConstraintValidator extends SimpleValidator<ProgramConstraint> {

    private final static String key = "yukon.web.modules.dr.setup.error.required";
    @Autowired LMValidatorHelper lmValidatorHelper;

    public ProgramConstraintValidator() {
        super(ProgramConstraint.class);
    }

    @Override
    protected void doValidation(ProgramConstraint programConstraint, Errors errors) {
        // Mandatory, max length(60) and special character check for name.
        lmValidatorHelper.validateName(programConstraint.getName(), errors, "Constraint name");

        // Holiday schedule and holiday usage check.Holiday usage is mandatory when holiday schedule is
        // selected. When none select is selected id will be sent as 0
        Integer holidayScheduleId = programConstraint.getHolidaySchedule().getId();
        if (holidayScheduleId != null && holidayScheduleId.compareTo(0) > 0) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "holidayUsage", key,
                new Object[] { "Holiday Usage" });
        }
        lmValidatorHelper.checkIfFieldRequired("maxActivateTime", errors, programConstraint.getMaxActivateTime(), "Max Activate");
        if (!errors.hasFieldErrors("maxActivateTime")) {
            YukonValidationUtils.checkRange(errors, "maxActivateTime", programConstraint.getMaxActivateTime(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("maxDailyOps", errors, programConstraint.getMaxDailyOps(), "Max Daily Ops");
        if (!errors.hasFieldErrors("maxDailyOps")) {
            YukonValidationUtils.checkRange(errors, "maxDailyOps", programConstraint.getMaxDailyOps(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("minActivateTime", errors, programConstraint.getMinActivateTime(), "Min Activate");
        if (!errors.hasFieldErrors("minActivateTime")) {
            YukonValidationUtils.checkRange(errors, "minActivateTime", programConstraint.getMinActivateTime(), 0, 99999, false);
        }
        lmValidatorHelper.checkIfFieldRequired("minRestartTime", errors, programConstraint.getMinRestartTime(), "Min Restart");
        if (!errors.hasFieldErrors("minRestartTime")) {
            YukonValidationUtils.checkRange(errors, "minRestartTime", programConstraint.getMinRestartTime(), 0, 99999, false);
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
