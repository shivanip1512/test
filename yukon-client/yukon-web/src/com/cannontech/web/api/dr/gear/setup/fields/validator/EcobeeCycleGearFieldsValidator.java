package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeeCycleGearFieldsValidator extends ProgramGearFieldsValidator<EcobeeCycleGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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
        yukonApiValidationUtils.checkIfFieldRequired("mandatory", errors, ecobeeCycleGear.getMandatory(), "Mandatory");

        // Check Ramp In/Out
        yukonApiValidationUtils.checkIfFieldRequired("rampInOut", errors, ecobeeCycleGear.getRampInOut(), "Ramp In/Out");

        // Check for Control Percent
        gearApiValidatorHelper.checkControlPercent(ecobeeCycleGear.getControlPercent(), errors);

        // Check for How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(ecobeeCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(ecobeeCycleGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearApiValidatorHelper.checkWhenToChange(ecobeeCycleGear.getWhenToChangeFields(), errors);
    }

}
