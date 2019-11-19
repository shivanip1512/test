package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeeSetpointGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class EcobeeSetpointGearFieldsValidator extends ProgramGearFieldsValidator<EcobeeSetpointGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    
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
        lmValidatorHelper.checkIfFieldRequired("mandatory", errors, ecobeeSetpointGear.getMandatory(), "Mandatory");
        
        //Check Setpoint
        gearValidatorHelper.checkSetpointOffset(ecobeeSetpointGear.getSetpointOffset(), errors);
        
        // Check for How to Stop Control
        gearValidatorHelper.checkHowToStopControl(ecobeeSetpointGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(ecobeeSetpointGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearValidatorHelper.checkWhenToChange(ecobeeSetpointGear.getWhenToChangeFields(), errors);
    }
}
