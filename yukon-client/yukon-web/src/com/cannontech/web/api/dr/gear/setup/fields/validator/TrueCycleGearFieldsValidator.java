package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class TrueCycleGearFieldsValidator extends SmartCycleGearFieldsValidator {

    public TrueCycleGearFieldsValidator() {
        super();
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.TrueCycle;
    }

    @Override
    protected void doValidation(SmartCycleGearFields smartCycleGear, Errors errors) {
        super.doValidation(smartCycleGear, errors);
    }
}
