package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class HoneywellCycleGearFieldsValidator extends ProgramGearFieldsValidator<HoneywellCycleGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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

        // Check Mandatory
        yukonApiValidationUtils.checkIfFieldRequired("mandatory", errors, honeywellCycleGear.getMandatory(), "Mandatory");

        // Check Ramp In/Out
        yukonApiValidationUtils.checkIfFieldRequired("rampInOut", errors, honeywellCycleGear.getRampInOut(), "Ramp In/Out");

        // Check Control Percent
        yukonApiValidationUtils.checkIfFieldRequired("controlPercent", errors, honeywellCycleGear.getControlPercent(),
            "Control Percent");
        if (!errors.hasFieldErrors("controlPercent")) {
            yukonApiValidationUtils.checkRange(errors, "controlPercent", honeywellCycleGear.getControlPercent(), 0, 100,
                false);
        }

        // Check Cycle Period
        yukonApiValidationUtils.checkIfFieldRequired("cyclePeriodInMinutes", errors,
            honeywellCycleGear.getCyclePeriodInMinutes(), "Cycle Period");
        if (!errors.hasFieldErrors("cyclePeriodInMinutes")) {
            if (honeywellCycleGear.getCyclePeriodInMinutes() != 30) {
                errors.rejectValue("cyclePeriodInMinutes", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "Cycle Period" }, "");
            }
        }

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(honeywellCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(honeywellCycleGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearApiValidatorHelper.checkWhenToChange(honeywellCycleGear.getWhenToChangeFields(), errors);
    }

}
