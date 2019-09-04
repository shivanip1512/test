package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.TargetCycleGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class TargetCycleGearFieldsValidator extends TrueCycleGearFieldsValidator {
    
    @Autowired private LMValidatorHelper lmValidatorHelper;

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
        lmValidatorHelper.checkIfFieldRequired("kWReduction", errors, targetCycleGearFields.getkWReduction(),
                "KW Reduction");
        YukonValidationUtils.checkRange(errors, "kWReduction", targetCycleGearFields.getkWReduction(), 0.0, 99999.999,
            true);
    }
}
