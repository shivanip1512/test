package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class ItronCycleGearFieldsValidator extends ProgramGearFieldsValidator<ItronCycleGearFields> {

    public ItronCycleGearFieldsValidator() {
        super(ItronCycleGearFields.class);
    }

    public ItronCycleGearFieldsValidator(Class<ItronCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.ItronCycle;
    }

    @Override
    protected void doValidation(ItronCycleGearFields target, Errors errors) {
        // TODO validate itron fields

    }

}
