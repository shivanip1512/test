package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class ItronCycleGearFieldsValidator extends ProgramGearFieldsValidator<ItronCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

    public ItronCycleGearFieldsValidator() {
        super(ItronCycleGearFields.class);
    }

    public ItronCycleGearFieldsValidator(Class<ItronCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.ItronCycle;
    }

    @Override
    protected void doValidation(ItronCycleGearFields itronCycleGear, Errors errors) {

        // Check Itron duty Cycle
        lmValidatorHelper.checkIfFieldRequired("cycleType", errors, itronCycleGear.getCycleType(), "Itron Cycle Type");

        // Check Ramp In
        gearValidatorHelper.checkRampIn(itronCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearValidatorHelper.checkRampOut(itronCycleGear.getRampOut(), errors);

        // Check Duty Cycle Percent
        lmValidatorHelper.checkIfFieldRequired("dutyCyclePercent", errors, itronCycleGear.getDutyCyclePercent(),
            "Duty Cycle");
        if (!errors.hasFieldErrors("dutyCyclePercent")) {
            YukonValidationUtils.checkRange(errors, "dutyCyclePercent", itronCycleGear.getDutyCyclePercent(), 0, 100,
                false);
        }

        // Check Duty Cycle Period
        lmValidatorHelper.checkIfFieldRequired("dutyCyclePeriodInMinutes", errors,
            itronCycleGear.getDutyCyclePeriodInMinutes(), "Duty Cycle Period");
        if (!errors.hasFieldErrors("dutyCyclePeriodInMinutes")) {
            if (itronCycleGear.getDutyCyclePeriodInMinutes() != 15 
                && itronCycleGear.getDutyCyclePeriodInMinutes() != 30
                && itronCycleGear.getDutyCyclePeriodInMinutes() != 60) {
                errors.rejectValue("dutyCyclePeriodInMinutes", invalidKey,
                    new Object[] { "Duty Cycle Period In Minutes" }, "");
            }
        }

        // Check Criticality
        lmValidatorHelper.checkIfFieldRequired("criticality", errors, itronCycleGear.getCriticality(), "Criticality");
        if (!errors.hasFieldErrors("criticality")) {
            YukonValidationUtils.checkRange(errors, "criticality", itronCycleGear.getCriticality(), 0, 255, false);
        }

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(itronCycleGear.getCapacityReduction(), errors);

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(itronCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(itronCycleGear.getWhenToChangeFields(), errors);

    }

}
