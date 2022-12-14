package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.NestStandardCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class NestStandardCycleGearFieldsValidator extends ProgramGearFieldsValidator<NestStandardCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;

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
    protected void doValidation(NestStandardCycleGearFields nestStandardCycleGear, Errors errors) {
        // Check for Prep Load Shape
        lmValidatorHelper.checkIfFieldRequired("preparationLoadShaping", errors, nestStandardCycleGear.getPreparationLoadShaping(), "Prep Load Shape");

        // Check for Peak Load Shape
        lmValidatorHelper.checkIfFieldRequired("peakLoadShaping", errors, nestStandardCycleGear.getPeakLoadShaping(), "Peak Load Shape");

        // Check for Post Load Shape
        lmValidatorHelper.checkIfFieldRequired("postPeakLoadShaping", errors, nestStandardCycleGear.getPostPeakLoadShaping(), "Post Load Shape");
    }

}
