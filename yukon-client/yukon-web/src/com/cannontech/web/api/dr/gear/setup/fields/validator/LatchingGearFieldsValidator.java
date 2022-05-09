package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.LatchingGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class LatchingGearFieldsValidator extends ProgramGearFieldsValidator<LatchingGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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
        yukonApiValidationUtils.checkIfFieldRequired("startControlState", errors, latchingCycleGear.getStartControlState(),
            "Start Control State");

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(latchingCycleGear.getCapacityReduction(), errors);
    }

}
