package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EatonCloudCycleGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class EatonCloudCycleGearFieldsValidator extends ProgramGearFieldsValidator<EatonCloudCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

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
        lmValidatorHelper.checkIfFieldRequired("dutyCycleType", errors, eatonCloudCycleGear.getDutyCycleType(),
                "Eaton Cloud Cycle Type");

        // Check Ramp In
        gearValidatorHelper.checkRampIn(eatonCloudCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearValidatorHelper.checkRampOut(eatonCloudCycleGear.getRampOut(), errors);

        // Check Duty Cycle Percent
        lmValidatorHelper.checkIfFieldRequired("dutyCyclePercent", errors, eatonCloudCycleGear.getDutyCyclePercent(),
                "Duty Cycle");
        if (!errors.hasFieldErrors("dutyCyclePercent")) {
            YukonValidationUtils.checkRange(errors, "dutyCyclePercent", eatonCloudCycleGear.getDutyCyclePercent(), 0, 100,
                    false);
        }

        // Check Duty Cycle Period
        lmValidatorHelper.checkIfFieldRequired("dutyCyclePeriodInMinutes", errors,
                eatonCloudCycleGear.getDutyCyclePeriodInMinutes(), "Duty Cycle Period");
        if (!errors.hasFieldErrors("dutyCyclePeriodInMinutes")) {
            if (eatonCloudCycleGear.getDutyCyclePeriodInMinutes() != 15
                    && eatonCloudCycleGear.getDutyCyclePeriodInMinutes() != 30
                    && eatonCloudCycleGear.getDutyCyclePeriodInMinutes() != 60) {
                errors.rejectValue("dutyCyclePeriodInMinutes", invalidKey,
                        new Object[] { "Duty Cycle Period In Minutes" }, "");
            }
        }

        // Check Criticality
        lmValidatorHelper.checkIfFieldRequired("criticality", errors, eatonCloudCycleGear.getCriticality(), "Criticality");
        if (!errors.hasFieldErrors("criticality")) {
            YukonValidationUtils.checkRange(errors, "criticality", eatonCloudCycleGear.getCriticality(), 0, 255, false);
        }

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(eatonCloudCycleGear.getCapacityReduction(), errors);

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(eatonCloudCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(eatonCloudCycleGear.getWhenToChangeFields(), errors);

    }
}
