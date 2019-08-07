package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.NestCriticalCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class NestCriticalCycleGearFieldsValidator extends ProgramGearFieldsValidator<NestCriticalCycleGearFields> {

    public NestCriticalCycleGearFieldsValidator() {
        super(NestCriticalCycleGearFields.class);
    }

    public NestCriticalCycleGearFieldsValidator(Class<NestCriticalCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.NestCriticalCycle;
    }

    @Override
    protected void doValidation(NestCriticalCycleGearFields nestCriticalCycleGear, Errors errors) {

    }

}
