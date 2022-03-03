package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.fields.TimeRefreshGearFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class TimeRefreshGearFieldsValidator extends ProgramGearFieldsValidator<TimeRefreshGearFields> {
    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;

    public TimeRefreshGearFieldsValidator() {
        super(TimeRefreshGearFields.class);
    }

    public TimeRefreshGearFieldsValidator(Class<TimeRefreshGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.TimeRefresh;
    }

    @Override
    protected void doValidation(TimeRefreshGearFields timeRefreshCycleGear, Errors errors) {
        // Check Refresh Shed Type
        YukonApiValidationUtils.checkIfFieldRequired("refreshShedType", errors, timeRefreshCycleGear.getRefreshShedType(),
            "Refresh Shed Type");
        if (!errors.hasFieldErrors("refreshShedType")) {
            if (timeRefreshCycleGear.getRefreshShedType() != CycleCountSendType.FixedShedTime
                    && timeRefreshCycleGear.getRefreshShedType() != CycleCountSendType.DynamicShedTime) {
                errors.rejectValue("refreshShedType", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { CycleCountSendType.FixedShedTime, CycleCountSendType.DynamicShedTime }, "");
            }
        }

        // Check Shed Time
        if (!errors.hasFieldErrors("refreshShedType")) {
            YukonApiValidationUtils.checkIfFieldRequired("shedTime", errors, timeRefreshCycleGear.getShedTime(), "Shed Time");
            if (!errors.hasFieldErrors("shedTime")) {
                TimeIntervals shedTime = TimeIntervals.fromSeconds(timeRefreshCycleGear.getShedTime());
                if (!TimeIntervals.getShedtime().contains(shedTime)) {
                    errors.rejectValue("shedTime", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { TimeIntervals.getShedtime() }, "");
                }
            }
        }

        // Check Group Selection Method
        gearApiValidatorHelper.checkGroupSelectionMethod(timeRefreshCycleGear.getGroupSelectionMethod(), errors);

        // Check No. of groups Each Time
        gearApiValidatorHelper.checkNumberOfGroups(timeRefreshCycleGear.getNumberOfGroups(), errors);

        // Check Command Resend Rate
        gearApiValidatorHelper.checkCommandResendRate(timeRefreshCycleGear.getSendRate(), errors);

        // Check Ramp In Percent and Interval
        if (timeRefreshCycleGear.getRampInPercent() != null
            || timeRefreshCycleGear.getRampInIntervalInSeconds() != null) {
            gearApiValidatorHelper.checkRampInPercentAndInterval(timeRefreshCycleGear.getRampInPercent(),
                timeRefreshCycleGear.getRampInIntervalInSeconds(), errors);
        }

        // Check How to Stop Control
        gearApiValidatorHelper.checkStopControlAndOrder(timeRefreshCycleGear.getHowToStopControl(),
            timeRefreshCycleGear.getStopOrder(), errors);

        // Check for Ramp Out and Ramp Out Interval
        if (!errors.hasFieldErrors("howToStopControl")
            && (timeRefreshCycleGear.getHowToStopControl() == HowToStopControl.RampOutRestore
                || timeRefreshCycleGear.getHowToStopControl() == HowToStopControl.RampOutTimeIn)) {
            gearApiValidatorHelper.checkRampOutPercentAndInterval(timeRefreshCycleGear.getRampOutPercent(),
                timeRefreshCycleGear.getRampOutIntervalInSeconds(), errors);
        }

        // Check Stop Command Repeat
        gearApiValidatorHelper.checkStopCommandRepeat(timeRefreshCycleGear.getStopCommandRepeat(), getControlMethod(),
            errors);

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(timeRefreshCycleGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearApiValidatorHelper.checkWhenToChange(timeRefreshCycleGear.getWhenToChangeFields(), errors);
    }
}