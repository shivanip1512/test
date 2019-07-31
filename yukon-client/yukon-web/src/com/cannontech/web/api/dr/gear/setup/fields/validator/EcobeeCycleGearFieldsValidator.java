package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class EcobeeCycleGearFieldsValidator extends ProgramGearFieldsValidator<EcobeeCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public EcobeeCycleGearFieldsValidator() {
        super(EcobeeCycleGearFields.class);
    }

    public EcobeeCycleGearFieldsValidator(Class<EcobeeCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.EcobeeCycle;
    }

    @Override
    protected void doValidation(EcobeeCycleGearFields ecobeeCycleGear, Errors errors) {
        // Check Mandatory
        lmValidatorHelper.checkIfFieldRequired("mandatory", errors, ecobeeCycleGear.getMandatory(), "Mandatory");

        // Check Ramp In
        gearValidatorHelper.checkRampIn(ecobeeCycleGear.getRampIn(), errors);

        // Check for Ramp Out
        gearValidatorHelper.checkRampOut(ecobeeCycleGear.getRampOut(), errors);

        // Check for Control Percent
        gearValidatorHelper.checkControlPercent(ecobeeCycleGear.getControlPercent(), errors);

        // Check for How to Stop Control
        gearValidatorHelper.checkHowToStopControl(ecobeeCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(ecobeeCycleGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearValidatorHelper.checkWhenToChange(ecobeeCycleGear.getWhenToChangeFields(), errors);
    }

}
