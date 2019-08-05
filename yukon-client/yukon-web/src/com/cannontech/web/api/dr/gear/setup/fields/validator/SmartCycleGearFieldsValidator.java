package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class SmartCycleGearFieldsValidator extends ProgramGearFieldsValidator<SmartCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

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
        lmValidatorHelper.checkIfFieldRequired("noRamp", errors, smartCycleGear.getNoRamp(), "No Ramp");

        // Check for Control Percent
        gearValidatorHelper.checkControlPercent(smartCycleGear.getControlPercent(), errors);

        // Check for Cycle Period
        gearValidatorHelper.checkCyclePeriod(smartCycleGear.getCyclePeriodInMinutes(), errors);

        // Check for Cycle Count Send Type
        if (smartCycleGear.getCycleCountSendType() == CycleCountSendType.FixedShedTime
            || smartCycleGear.getCycleCountSendType() == CycleCountSendType.DynamicShedTime) {
            errors.rejectValue("cycleCountSendType", invalidKey, new Object[] { "Cycle Count Send Type" }, "");
        }

        // Check for Max Cycle Count
        lmValidatorHelper.checkIfFieldRequired("maxCycleCount", errors, smartCycleGear.getMaxCycleCount(),
            "Max Cycle Count");

        // Check for Starting Period Count
        lmValidatorHelper.checkIfFieldRequired("startingPeriodCount", errors, smartCycleGear.getStartingPeriodCount(),
            "Starting Period Count");
        if (!errors.hasFieldErrors("startingPeriodCount")) {
            YukonValidationUtils.checkRange(errors, "startingPeriodCount", smartCycleGear.getStartingPeriodCount(), 1,
                63, false);
        }

        // Check for Command Resend Rate
        gearValidatorHelper.checkCommandResendRate(smartCycleGear.getSendRate(), errors);

        // Check for How to Stop Control
        gearValidatorHelper.checkHowToStopControl(smartCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Stop Command Repeat
        gearValidatorHelper.checkStopCommandRepeat(smartCycleGear.getStopCommandRepeat(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(smartCycleGear.getCapacityReduction(), errors);

        // Check for When to Change
        gearValidatorHelper.checkWhenToChange(smartCycleGear.getWhenToChangeFields(), errors);
    }

}
