package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeePlusGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeePlusGearFieldsValidator extends ProgramGearFieldsValidator<EcobeePlusGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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
        yukonApiValidationUtils.checkIfFieldRequired("heatingEvent", errors, ecobeePlusGear.getHeatingEvent(), "Heating Event");

        // Check Ramp In/Out
        yukonApiValidationUtils.checkIfFieldRequired("rampInOut", errors, ecobeePlusGear.getRampInOut(), "Ramp In/Out");

        // Check for How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(ecobeePlusGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(ecobeePlusGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearApiValidatorHelper.checkWhenToChange(ecobeePlusGear.getWhenToChangeFields(), errors);
    }

}
