package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class HoneywellCycleGearFieldsValidator extends ProgramGearFieldsValidator<HoneywellCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

    public HoneywellCycleGearFieldsValidator() {
        super(HoneywellCycleGearFields.class);
    }

    public HoneywellCycleGearFieldsValidator(Class<HoneywellCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.HoneywellCycle;
    }

    @Override
    protected void doValidation(HoneywellCycleGearFields honeywellCycleGear, Errors errors) {
        // Check Ramp In/Out
        lmValidatorHelper.checkIfFieldRequired("rampInOut", errors, honeywellCycleGear.getRampInOut(), "Ramp In/Out");

        // Check Control Percent
        lmValidatorHelper.checkIfFieldRequired("controlPercent", errors, honeywellCycleGear.getControlPercent(),
            "Control Percent");
        if (!errors.hasFieldErrors("controlPercent")) {
            YukonValidationUtils.checkRange(errors, "controlPercent", honeywellCycleGear.getControlPercent(), 0, 100,
                false);
        }

        // Check Cycle Period
        lmValidatorHelper.checkIfFieldRequired("cyclePeriodInMinutes", errors,
            honeywellCycleGear.getCyclePeriodInMinutes(), "Cycle Period");
        if (!errors.hasFieldErrors("cyclePeriodInMinutes")) {
            if (honeywellCycleGear.getCyclePeriodInMinutes() != 30) {
                errors.rejectValue("cyclePeriodInMinutes", invalidKey, new Object[] { "Cycle Period" }, "");
            }
        }

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(honeywellCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(honeywellCycleGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearValidatorHelper.checkWhenToChange(honeywellCycleGear.getWhenToChangeFields(), errors);
    }

}
