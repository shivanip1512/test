package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeeCycleGearFieldsValidator extends ProgramGearFieldsValidator<EcobeeCycleGearFields> {

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
    protected void doValidation(EcobeeCycleGearFields target, Errors errors) {
        // TODO validate ecobee fields

    }

}
