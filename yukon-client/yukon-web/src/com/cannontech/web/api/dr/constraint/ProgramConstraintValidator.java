package com.cannontech.web.api.dr.constraint;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.HolidayUsage;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintValidator extends SimpleValidator<ProgramConstraint> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private LMServiceHelper lmServiceHelper;

    public ProgramConstraintValidator() {
        super(ProgramConstraint.class);
    }

    @Override
    protected void doValidation(ProgramConstraint programConstraint, Errors errors) {
        // Mandatory, max length(60) and special character check for name.
        yukonApiValidationUtils.validateName(programConstraint.getName(), errors, "Name");
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

        if (programConstraint.getSeasonSchedule() == null) {
            errors.rejectValue("seasonSchedule", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                    new Object[] { "Season Schedule" }, "");
        } else if (programConstraint.getSeasonSchedule().getId() == null) {
            errors.rejectValue("seasonSchedule.id", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                    new Object[] { "Season Schedule Id" }, "");
        } else {
            Integer seasonScheduleId = programConstraint.getSeasonSchedule().getId();
            if (seasonScheduleId != 0) {
                Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(seasonScheduleId);
                if (seasonSchedule.isEmpty()) {
                    errors.rejectValue("seasonSchedule.id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "Season Schedule Id" }, "");
                }
            }
        }

        if (programConstraint.getHolidaySchedule() == null) {
            errors.rejectValue("holidaySchedule", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                    new Object[] { "Holiday Schedule" }, "");
        } else if (programConstraint.getHolidaySchedule().getId() == null) {
            errors.rejectValue("holidaySchedule.id", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                    new Object[] { "Holiday Schedule Id" }, "");
        } else {
            Integer holidayScheduleId = programConstraint.getHolidaySchedule().getId();
            if (holidayScheduleId != 0) {
                Optional<LMDto> holidaySchedule = lmServiceHelper.getHolidaySchedule(holidayScheduleId);
                if (holidaySchedule.isEmpty()) {
                    errors.rejectValue("holidaySchedule.id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "Holiday Schedule Id" }, "");
                }
            }
        }

        // Holiday schedule and holiday usage check.Holiday usage is mandatory when holiday schedule is
        // selected. When none select is selected id will be sent as 0
        if (!errors.hasFieldErrors("holidaySchedule") && !errors.hasFieldErrors("holidaySchedule.id")) {
            Integer holidayScheduleId = programConstraint.getHolidaySchedule().getId();
            if (holidayScheduleId != null && holidayScheduleId.compareTo(0) > 0) {
                yukonApiValidationUtils.checkIsBlank(errors, "holidayUsage", programConstraint.getHolidayUsage().toString(), false, "Holiday Usage");
                if (programConstraint.getHolidayUsage() == HolidayUsage.NONE) {
                    errors.rejectValue("holidayUsage", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Holiday Usage" }, "");
                }
            }
        }
        yukonApiValidationUtils.checkIfFieldRequired("maxActivateSeconds", errors, programConstraint.getMaxActivateSeconds(), "Max Activate");
        if (!errors.hasFieldErrors("maxActivateSeconds")) {
            yukonApiValidationUtils.checkRange(errors, "maxActivateSeconds", programConstraint.getMaxActivateSeconds(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("maxDailyOps", errors, programConstraint.getMaxDailyOps(), "Max Daily Ops");
        if (!errors.hasFieldErrors("maxDailyOps")) {
            yukonApiValidationUtils.checkRange(errors, "maxDailyOps", programConstraint.getMaxDailyOps(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("minActivateSeconds", errors, programConstraint.getMinActivateSeconds(), "Min Activate");
        if (!errors.hasFieldErrors("minActivateSeconds")) {
            yukonApiValidationUtils.checkRange(errors, "minActivateSeconds", programConstraint.getMinActivateSeconds(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("minRestartSeconds", errors, programConstraint.getMinRestartSeconds(), "Min Restart");
        if (!errors.hasFieldErrors("minRestartSeconds")) {
            yukonApiValidationUtils.checkRange(errors, "minRestartSeconds", programConstraint.getMinRestartSeconds(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("maxHoursDaily", errors, programConstraint.getMaxHoursDaily(), "Daily");
        if (!errors.hasFieldErrors("maxHoursDaily")) {
            yukonApiValidationUtils.checkRange(errors, "maxHoursDaily", programConstraint.getMaxHoursDaily(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("maxHoursMonthly", errors, programConstraint.getMaxHoursMonthly(), "Monthly");
        if (!errors.hasFieldErrors("maxHoursMonthly")) {
            yukonApiValidationUtils.checkRange(errors, "maxHoursMonthly", programConstraint.getMaxHoursMonthly(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("maxHoursAnnually", errors, programConstraint.getMaxHoursAnnually(), "Annually");
        if (!errors.hasFieldErrors("maxHoursAnnually")) {
            yukonApiValidationUtils.checkRange(errors, "maxHoursAnnually", programConstraint.getMaxHoursAnnually(), 0, 99999, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("maxHoursSeasonal", errors, programConstraint.getMaxHoursSeasonal(), "Seasonal");
        if (!errors.hasFieldErrors("maxHoursSeasonal")) {
            yukonApiValidationUtils.checkRange(errors, "maxHoursSeasonal", programConstraint.getMaxHoursSeasonal(), 0, 99999, false);
        }
    }
}
