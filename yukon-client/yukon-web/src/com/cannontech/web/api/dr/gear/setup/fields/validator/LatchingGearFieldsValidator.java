package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.LatchingGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class LatchingGearFieldsValidator extends ProgramGearFieldsValidator<LatchingGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public LatchingGearFieldsValidator() {
        super(LatchingGearFields.class);
    }

    public LatchingGearFieldsValidator(Class<LatchingGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.Latching;
    }

    @Override
    protected void doValidation(LatchingGearFields latchingCycleGear, Errors errors) {
        // Check Control Start State
        lmValidatorHelper.checkIfFieldRequired("startControlState", errors, latchingCycleGear.getStartControlState(),
            "Start Control State");
        if (!errors.hasFieldErrors("startControlState")) {
            YukonValidationUtils.checkRange(errors, "startControlState", latchingCycleGear.getStartControlState(), 0, 1,
                false);
        }

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(latchingCycleGear.getCapacityReduction(), errors);
    }

}
