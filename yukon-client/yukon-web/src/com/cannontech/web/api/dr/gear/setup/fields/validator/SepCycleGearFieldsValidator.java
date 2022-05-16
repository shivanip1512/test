package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SepCycleGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class SepCycleGearFieldsValidator extends ProgramGearFieldsValidator<SepCycleGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

    public SepCycleGearFieldsValidator() {
        super(SepCycleGearFields.class);
    }

    public SepCycleGearFieldsValidator(Class<SepCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.SepCycle;
    }

    @Override
    protected void doValidation(SepCycleGearFields sepCycleGearFieldsCycleGear, Errors errors) {
        // Check Ramp In
        gearApiValidatorHelper.checkRampIn(sepCycleGearFieldsCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearApiValidatorHelper.checkRampOut(sepCycleGearFieldsCycleGear.getRampOut(), errors);

        // Check TrueCycle or adaptive algorithm
        yukonApiValidationUtils.checkIfFieldRequired("trueCycle", errors, sepCycleGearFieldsCycleGear.getTrueCycle(),
            "True Cycle or Adaptive Algorithm");

        // Check Control Percent
        gearApiValidatorHelper.checkControlPercent(sepCycleGearFieldsCycleGear.getControlPercent(), errors);

        // Check Criticality
        gearApiValidatorHelper.checkCriticality(sepCycleGearFieldsCycleGear.getCriticality(), errors);

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(sepCycleGearFieldsCycleGear.getHowToStopControl(), getControlMethod(),
            errors);

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(sepCycleGearFieldsCycleGear.getCapacityReduction(), errors);

        // Check When To Change
        gearApiValidatorHelper.checkWhenToChange(sepCycleGearFieldsCycleGear.getWhenToChangeFields(), errors);
    }

}
