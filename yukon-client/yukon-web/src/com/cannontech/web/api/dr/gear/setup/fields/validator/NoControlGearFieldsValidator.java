package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.NoControlGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class NoControlGearFieldsValidator extends ProgramGearFieldsValidator<NoControlGearFields> {

    public NoControlGearFieldsValidator() {
        super(NoControlGearFields.class);
    }

    public NoControlGearFieldsValidator(Class<NoControlGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.NoControl;
    }

    @Override
    protected void doValidation(NoControlGearFields target, Errors errors) {
        // TODO validate no cycle fields

    }

}
