package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeeSetpointGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeeSetpointGearFieldsValidator extends ProgramGearFieldsValidator<EcobeeSetpointGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;
    
    public EcobeeSetpointGearFieldsValidator() {
        super(EcobeeSetpointGearFields.class);
    }
    
    public EcobeeSetpointGearFieldsValidator(Class<EcobeeSetpointGearFields> objectType) {
        super(objectType);
    }
    
    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.EcobeeSetpoint;
    }
    
    @Override
    protected void doValidation(EcobeeSetpointGearFields ecobeeSetpointGear, Errors errors) {
        //Check Mandatory
        yukonApiValidationUtils.checkIfFieldRequired("mandatory", errors, ecobeeSetpointGear.getMandatory(), "Mandatory");
        
        //Check Setpoint
        gearApiValidatorHelper.checkSetpointOffset(ecobeeSetpointGear.getSetpointOffset(), errors);
        
        // Check for How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(ecobeeSetpointGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(ecobeeSetpointGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearApiValidatorHelper.checkWhenToChange(ecobeeSetpointGear.getWhenToChangeFields(), errors);
    }
}
