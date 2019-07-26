package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.TargetCycleGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class TargetCycleGearFieldsValidator extends TrueCycleGearFieldsValidator {

    public TargetCycleGearFieldsValidator() {
        super();
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.TargetCycle;
    }

    @Override
    protected void doValidation(SmartCycleGearFields smartCycleGear, Errors errors) {

        super.doValidation(smartCycleGear, errors);
        TargetCycleGearFields targetCycleGearFields = (TargetCycleGearFields) smartCycleGear;
        // Check for kWReduction
        YukonValidationUtils.checkRange(errors, "kWReduction", targetCycleGearFields.getkWReduction(), 0.0, 99999.999,
            false);
    }
}
