package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.NestStandardCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class NestStandardCycleGearFieldsValidator extends ProgramGearFieldsValidator<NestStandardCycleGearFields> {

    public NestStandardCycleGearFieldsValidator() {
        super(NestStandardCycleGearFields.class);
    }

    public NestStandardCycleGearFieldsValidator(Class<NestStandardCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.NestStandardCycle;
    }

    @Override
    protected void doValidation(NestStandardCycleGearFields target, Errors errors) {
        // TODO validate nest fields

    }

}
