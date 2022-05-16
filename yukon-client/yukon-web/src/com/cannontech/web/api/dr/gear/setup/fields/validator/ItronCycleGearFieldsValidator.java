package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;


public class ItronCycleGearFieldsValidator extends ProgramGearFieldsValidator<ItronCycleGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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
        yukonApiValidationUtils.checkIfFieldRequired("dutyCycleType", errors, itronCycleGear.getDutyCycleType(), "Itron Cycle Type");

        // Check Ramp In
        gearApiValidatorHelper.checkRampIn(itronCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearApiValidatorHelper.checkRampOut(itronCycleGear.getRampOut(), errors);

        // Check Duty Cycle Percent
        yukonApiValidationUtils.checkIfFieldRequired("dutyCyclePercent", errors, itronCycleGear.getDutyCyclePercent(),
            "Duty Cycle");
        if (!errors.hasFieldErrors("dutyCyclePercent")) {
            yukonApiValidationUtils.checkRange(errors, "dutyCyclePercent", itronCycleGear.getDutyCyclePercent(), 0, 100,
                false);
        }

        // Check Duty Cycle Period
        yukonApiValidationUtils.checkIfFieldRequired("dutyCyclePeriodInMinutes", errors,
            itronCycleGear.getDutyCyclePeriodInMinutes(), "Duty Cycle Period");
        if (!errors.hasFieldErrors("dutyCyclePeriodInMinutes")) {
            if (itronCycleGear.getDutyCyclePeriodInMinutes() != 15 
                && itronCycleGear.getDutyCyclePeriodInMinutes() != 30
                && itronCycleGear.getDutyCyclePeriodInMinutes() != 60) {
                errors.rejectValue("dutyCyclePeriodInMinutes", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { "Duty Cycle Period In Minutes" }, "");
            }
        }

        // Check Criticality
        yukonApiValidationUtils.checkIfFieldRequired("criticality", errors, itronCycleGear.getCriticality(), "Criticality");
        if (!errors.hasFieldErrors("criticality")) {
            yukonApiValidationUtils.checkRange(errors, "criticality", itronCycleGear.getCriticality(), 0, 255, false);
        }

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(itronCycleGear.getCapacityReduction(), errors);

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(itronCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(itronCycleGear.getWhenToChangeFields(), errors);

    }

}
