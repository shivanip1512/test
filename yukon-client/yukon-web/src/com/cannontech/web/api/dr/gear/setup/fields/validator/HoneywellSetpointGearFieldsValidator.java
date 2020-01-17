package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.HoneywellSetpointGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class HoneywellSetpointGearFieldsValidator extends ProgramGearFieldsValidator<HoneywellSetpointGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    
    public HoneywellSetpointGearFieldsValidator() {
        super(HoneywellSetpointGearFields.class);
    }
    
    public HoneywellSetpointGearFieldsValidator(Class<HoneywellSetpointGearFields> objectType) {
        super(objectType);
    }
    
    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.HoneywellSetpoint;
    }
    
    @Override
    protected void doValidation(HoneywellSetpointGearFields honeywellSetpointGear, Errors errors) {
        //Check Mandatory
        lmValidatorHelper.checkIfFieldRequired("mandatory", errors, honeywellSetpointGear.getMandatory(), "Mandatory");
        
        //Check Setpoint
        gearValidatorHelper.checkSetpointOffset(honeywellSetpointGear.getSetpointOffset(), errors);
        
        //Check Precool Setpoint
        gearValidatorHelper.checkPrecoolOffset(honeywellSetpointGear.getPrecoolOffset(), errors);
        
        // Check for How to Stop Control
        gearValidatorHelper.checkHowToStopControl(honeywellSetpointGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(honeywellSetpointGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearValidatorHelper.checkWhenToChange(honeywellSetpointGear.getWhenToChangeFields(), errors);
    }
    
}
