package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.fields.TimeRefreshGearFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class TimeRefreshGearFieldsValidator extends ProgramGearFieldsValidator<TimeRefreshGearFields> {

    @Autowired private GearValidatorHelper gearValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

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
        // Check Refresh Shed Time
        if (timeRefreshCycleGear.getRefreshShedTime() != CycleCountSendType.FixedShedTime
            && timeRefreshCycleGear.getRefreshShedTime() != CycleCountSendType.DynamicShedTime) {
            errors.rejectValue("refreshShedTime", invalidKey, new Object[] { "Refresh Shed Time" }, "");
        }

        // Check Shed Time
        if (!errors.hasFieldErrors("refreshShedTime")) {
            if (!TimeIntervals.getShedtime().contains(timeRefreshCycleGear.getShedTime())) {
                errors.rejectValue("shedTime", invalidKey, new Object[] { "Shed Time" }, "");
            }
        }

        // Check No. of groups Each Time
        gearValidatorHelper.checkNumberOfGroups(timeRefreshCycleGear.getNumberOfGroups(), errors);

        // Check Command Resend Rate
        gearValidatorHelper.checkCommandResendRate(timeRefreshCycleGear.getSendRate(), errors);

        // Check Ramp In Percent and Interval
        if (timeRefreshCycleGear.getRampInPercent() != null
            || timeRefreshCycleGear.getRampInIntervalInSeconds() != null) {
            gearValidatorHelper.checkRampInPercentAndInterval(timeRefreshCycleGear.getRampInPercent(),
                timeRefreshCycleGear.getRampInIntervalInSeconds(), errors);
        }

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(timeRefreshCycleGear.getHowToStopControl(), getControlMethod(),
            errors);

        // Check for Ramp Out and Ramp Out Interval
        if (!errors.hasFieldErrors("howToStopControl")
            && (timeRefreshCycleGear.getHowToStopControl() == HowToStopControl.RampOutRestore
                || timeRefreshCycleGear.getHowToStopControl() == HowToStopControl.RampOutTimeIn)) {
            gearValidatorHelper.checkRampOutPercentAndInterval(timeRefreshCycleGear.getRampOutPercent(),
                timeRefreshCycleGear.getRampOutIntervalInSeconds(), errors);
        }

        // Check Stop Command Repeat
        gearValidatorHelper.checkStopCommandRepeat(timeRefreshCycleGear.getStopCommandRepeat(), getControlMethod(),
            errors);

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(timeRefreshCycleGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearValidatorHelper.checkWhenToChange(timeRefreshCycleGear.getWhenToChangeFields(), errors);
    }

}
