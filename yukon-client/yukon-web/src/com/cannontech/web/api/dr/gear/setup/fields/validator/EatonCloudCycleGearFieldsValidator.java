package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.fields.EatonCloudCycleGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class EatonCloudCycleGearFieldsValidator extends ProgramGearFieldsValidator<EatonCloudCycleGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

    public EatonCloudCycleGearFieldsValidator() {
        super(EatonCloudCycleGearFields.class);
    }

    public EatonCloudCycleGearFieldsValidator(Class<EatonCloudCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.EatonCloudCycle;
    }

    @Override
    protected void doValidation(EatonCloudCycleGearFields eatonCloudCycleGear, Errors errors) {

        // Check Eaton Cloud duty Cycle
        yukonApiValidationUtils.checkIfFieldRequired("dutyCycleType", errors, eatonCloudCycleGear.getDutyCycleType(),
                "Eaton Cloud Cycle Type");

        // Check Ramp In
        gearApiValidatorHelper.checkRampIn(eatonCloudCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearApiValidatorHelper.checkRampOut(eatonCloudCycleGear.getRampOut(), errors);

        // Check Duty Cycle Percent
        yukonApiValidationUtils.checkIfFieldRequired("dutyCyclePercent", errors, eatonCloudCycleGear.getDutyCyclePercent(),
                "Duty Cycle");
        if (!errors.hasFieldErrors("dutyCyclePercent")) {
            yukonApiValidationUtils.checkRange(errors, "dutyCyclePercent", eatonCloudCycleGear.getDutyCyclePercent(), 0, 100,
                    false);
        }

        // Check Duty Cycle Period
        yukonApiValidationUtils.checkIfFieldRequired("dutyCyclePeriodInMinutes", errors,
                eatonCloudCycleGear.getDutyCyclePeriodInMinutes(), "Duty Cycle Period");
        if (!errors.hasFieldErrors("dutyCyclePeriodInMinutes")) {
            if (eatonCloudCycleGear.getDutyCyclePeriodInMinutes() != 15
                    && eatonCloudCycleGear.getDutyCyclePeriodInMinutes() != 30
                    && eatonCloudCycleGear.getDutyCyclePeriodInMinutes() != 60) {
                errors.rejectValue("dutyCyclePeriodInMinutes", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "Duty Cycle Period In Minutes" }, "");
            }
        }

        // Check Criticality
        yukonApiValidationUtils.checkIfFieldRequired("criticality", errors, eatonCloudCycleGear.getCriticality(), "Criticality");
        if (!errors.hasFieldErrors("criticality")) {
            yukonApiValidationUtils.checkRange(errors, "criticality", eatonCloudCycleGear.getCriticality(), 0, 255, false);
        }

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(eatonCloudCycleGear.getCapacityReduction(), errors);

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(eatonCloudCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(eatonCloudCycleGear.getWhenToChangeFields(), errors);

    }
}
