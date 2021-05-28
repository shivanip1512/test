package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeePlusGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class EcobeePlusGearFieldsValidator extends ProgramGearFieldsValidator<EcobeePlusGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public EcobeePlusGearFieldsValidator() {
        super(EcobeePlusGearFields.class);
    }

    public EcobeePlusGearFieldsValidator(Class<EcobeePlusGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.EcobeePlus;
    }

    @Override
    protected void doValidation(EcobeePlusGearFields ecobeePlusGear, Errors errors) {

        // Check Heating Event
        lmValidatorHelper.checkIfFieldRequired("heatingEvent", errors, ecobeePlusGear.getHeatingEvent(), "Heating Event");

        // Check Ramp In/Out
        lmValidatorHelper.checkIfFieldRequired("rampInOut", errors, ecobeePlusGear.getRampInOut(), "Ramp In/Out");

        // Check for How to Stop Control
        gearValidatorHelper.checkHowToStopControl(ecobeePlusGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(ecobeePlusGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearValidatorHelper.checkWhenToChange(ecobeePlusGear.getWhenToChangeFields(), errors);
    }

}
