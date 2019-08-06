package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.BeatThePeakGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class BeatThePeakGearFieldsValidator extends ProgramGearFieldsValidator<BeatThePeakGearFields> {
    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public BeatThePeakGearFieldsValidator() {
        super(BeatThePeakGearFields.class);
    }

    public BeatThePeakGearFieldsValidator(Class<BeatThePeakGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.BeatThePeak;
    }

    @Override
    protected void doValidation(BeatThePeakGearFields beatThePeakCycleGear, Errors errors) {
        // Check BTP LED indicator
        lmValidatorHelper.checkIfFieldRequired("indicator", errors, beatThePeakCycleGear.getIndicator(),
            "BTP LED Indicator");

        // Check Max Indicator Timeout
        lmValidatorHelper.checkIfFieldRequired("timeoutInMinutes", errors, beatThePeakCycleGear.getTimeoutInMinutes(),
            "Max Indicator Timeout");
        if (!errors.hasFieldErrors("timeoutInMinutes")) {
            YukonValidationUtils.checkRange(errors, "timeoutInMinutes", beatThePeakCycleGear.getTimeoutInMinutes(), 0,
                99999, false);
        }

        // Check Resend Rate
        lmValidatorHelper.checkIfFieldRequired("resendInMinutes", errors, beatThePeakCycleGear.getResendInMinutes(),
            "Resend Rate");
        if (!errors.hasFieldErrors("resendInMinutes")) {
            YukonValidationUtils.checkRange(errors, "resendInMinutes", beatThePeakCycleGear.getResendInMinutes(), 0,
                99999, false);
        }

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(beatThePeakCycleGear.getWhenToChangeFields(), errors);
    }
}
