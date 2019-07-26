package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class MagnitudeCycleGearFieldsValidator extends TrueCycleGearFieldsValidator {

    public MagnitudeCycleGearFieldsValidator() {
        super();
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.MagnitudeCycle;
    }

    @Override
    protected void doValidation(SmartCycleGearFields smartCycleGear, Errors errors) {
        super.doValidation(smartCycleGear, errors);
    }
}
