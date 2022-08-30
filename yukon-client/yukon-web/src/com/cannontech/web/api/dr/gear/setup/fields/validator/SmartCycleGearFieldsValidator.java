package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class SmartCycleGearFieldsValidator extends ProgramGearFieldsValidator<SmartCycleGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    public SmartCycleGearFieldsValidator() {
        super(SmartCycleGearFields.class);
    }

    public SmartCycleGearFieldsValidator(Class<SmartCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.SmartCycle;
    }

    @Override
    protected void doValidation(SmartCycleGearFields smartCycleGear, Errors errors) {
        // Check for No Ramp Value
        yukonApiValidationUtils.checkIfFieldRequired("noRamp", errors, smartCycleGear.getNoRamp(), "No Ramp");

        // Check for Control Percent
        gearApiValidatorHelper.checkControlPercent(smartCycleGear.getControlPercent(), errors);

        // Check for Cycle Period
        gearApiValidatorHelper.checkCyclePeriod(smartCycleGear.getCyclePeriodInMinutes(), getControlMethod(), errors);

        // Check for Cycle Count Send Type
        yukonApiValidationUtils.checkIfFieldRequired("cycleCountSendType", errors, smartCycleGear.getCycleCountSendType(),
            "Cycle Count Send Type");
        if (!errors.hasFieldErrors("cycleCountSendType")
            && (smartCycleGear.getCycleCountSendType() == CycleCountSendType.FixedShedTime
                        || smartCycleGear.getCycleCountSendType() == CycleCountSendType.DynamicShedTime)) {
            errors.rejectValue("cycleCountSendType", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { CycleCountSendType.CountDown, CycleCountSendType.FixedCount,
                            CycleCountSendType.LimitedCountDown }, "");
        }

        // Check for Max Cycle Count
        yukonApiValidationUtils.checkIfFieldRequired("maxCycleCount", errors, smartCycleGear.getMaxCycleCount(),
            "Max Cycle Count");
        if (!errors.hasFieldErrors("maxCycleCount")) {
            yukonApiValidationUtils.checkRange(errors, "maxCycleCount", smartCycleGear.getMaxCycleCount(), 0, 63, false);
        }

        // Check for Starting Period Count
        yukonApiValidationUtils.checkIfFieldRequired("startingPeriodCount", errors, smartCycleGear.getStartingPeriodCount(),
            "Starting Period Count");
        if (!errors.hasFieldErrors("startingPeriodCount")) {
            yukonApiValidationUtils.checkRange(errors, "startingPeriodCount", smartCycleGear.getStartingPeriodCount(), 1,
                63, false);
        }

        // Check for Command Resend Rate
        gearApiValidatorHelper.checkCommandResendRate(smartCycleGear.getSendRate(), errors);

        // Check for How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(smartCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Stop Command Repeat
        gearApiValidatorHelper.checkStopCommandRepeat(smartCycleGear.getStopCommandRepeat(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(smartCycleGear.getCapacityReduction(), errors);

        // Check for When to Change
        gearApiValidatorHelper.checkWhenToChange(smartCycleGear.getWhenToChangeFields(), errors);
    }

}