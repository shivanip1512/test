package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class SmartCycleGearFieldsValidator extends ProgramGearFieldsValidator<SmartCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    private final static String key = "yukon.web.modules.dr.setup.error.invalid";

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
        lmValidatorHelper.checkIfFieldRequired("controlPercent", errors, smartCycleGear.getControlPercent(),
            "Control Percent");
        if (!errors.hasFieldErrors("controlPercent")) {
            YukonValidationUtils.checkRange(errors, "controlPercent", smartCycleGear.getControlPercent(), 5, 100,
                false);
        }

        // Check for Cycle Period
        lmValidatorHelper.checkIfFieldRequired("cyclePeriodInMinutes", errors, smartCycleGear.getCyclePeriodInMinutes(),
            "Cycle Period");
        if (!errors.hasFieldErrors("cyclePeriodInMinutes")) {
            YukonValidationUtils.checkRange(errors, "cyclePeriodInMinutes", smartCycleGear.getCyclePeriodInMinutes(), 1,
                945, false);
        }

        // Check for Cycle Count Send Type
        lmValidatorHelper.checkIfFieldRequired("cycleCountSendType", errors, smartCycleGear.getCycleCountSendType(),
            "Cycle Count Send Type");

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
        lmValidatorHelper.checkIfFieldRequired("sendRate", errors, smartCycleGear.getSendRate(), "Command Resend Rate");
        if (!errors.hasFieldErrors("sendRate")) {
            if (!TimeIntervals.getCommandresendrate().contains(smartCycleGear.getSendRate())) {
                errors.rejectValue("sendRate", key, new Object[] { "Command Resend Rate" }, "");
            }
        }

        // Check for How to Stop Control
        lmValidatorHelper.checkIfFieldRequired("howToStopControl", errors, smartCycleGear.getHowToStopControl(),
            "How To Stop Control");

        // Check for Stop Command Repeat
        lmValidatorHelper.checkIfFieldRequired("stopCommandRepeat", errors, smartCycleGear.getStopCommandRepeat(),
            "Stop Command Repeat");
        if (!errors.hasFieldErrors("stopCommandRepeat")) {
            YukonValidationUtils.checkRange(errors, "stopCommandRepeat", smartCycleGear.getStopCommandRepeat(), 0, 5,
                false);
        }

        // Check for Group Capacity Reduction
        YukonValidationUtils.checkRange(errors, "capacityReduction", smartCycleGear.getCapacityReduction(), 0, 100,
            false);
    }

}
