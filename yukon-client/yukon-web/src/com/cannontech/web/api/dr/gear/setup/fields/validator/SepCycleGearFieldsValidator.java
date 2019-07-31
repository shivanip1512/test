package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SepCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class SepCycleGearFieldsValidator extends ProgramGearFieldsValidator<SepCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

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
        gearValidatorHelper.checkRampIn(sepCycleGearFieldsCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearValidatorHelper.checkRampOut(sepCycleGearFieldsCycleGear.getRampOut(), errors);

        // Check TrueCycle or adaptive algorithm
        lmValidatorHelper.checkIfFieldRequired("trueCycle", errors, sepCycleGearFieldsCycleGear.getTrueCycle(),
            "True Cycle");

        // Check Control Percent
        gearValidatorHelper.checkControlPercent(sepCycleGearFieldsCycleGear.getControlPercent(), errors);

        // Check Criticality
        gearValidatorHelper.checkCriticality(sepCycleGearFieldsCycleGear.getCriticality(), errors);

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(sepCycleGearFieldsCycleGear.getHowToStopControl(), getControlMethod(),
            errors);

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(sepCycleGearFieldsCycleGear.getCapacityReduction(), errors);

        // Check When To Change
        gearValidatorHelper.checkWhenToChange(sepCycleGearFieldsCycleGear.getWhenToChangeFields(), errors);
    }

}
