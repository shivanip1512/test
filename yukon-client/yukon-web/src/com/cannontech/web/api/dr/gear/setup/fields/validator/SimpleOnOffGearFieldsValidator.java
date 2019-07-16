package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SimpleOnOffGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class SimpleOnOffGearFieldsValidator extends ProgramGearFieldsValidator<SimpleOnOffGearFields> {
    
    public SimpleOnOffGearFieldsValidator() {
        super(SimpleOnOffGearFields.class);
    }
    
    public SimpleOnOffGearFieldsValidator(Class<SimpleOnOffGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.SimpleOnOff;
    }

    @Override
    protected void doValidation(SimpleOnOffGearFields target, Errors errors) {
        // TODO Auto-generated method stub
    }

}
