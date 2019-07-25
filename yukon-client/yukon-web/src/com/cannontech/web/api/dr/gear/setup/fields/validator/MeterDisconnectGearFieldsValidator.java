package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.MeterDisconnectGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class MeterDisconnectGearFieldsValidator extends ProgramGearFieldsValidator<MeterDisconnectGearFields> {
    
    public MeterDisconnectGearFieldsValidator() {
        super(MeterDisconnectGearFields.class);
    }
    
    public MeterDisconnectGearFieldsValidator(Class<MeterDisconnectGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.MeterDisconnect;
    }

    @Override
    protected void doValidation(MeterDisconnectGearFields target, Errors errors) {
        // TODO Auto-generated method stub
    }

}
