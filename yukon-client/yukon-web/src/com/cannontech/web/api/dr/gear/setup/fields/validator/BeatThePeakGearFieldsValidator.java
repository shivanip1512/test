package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.BeatThePeakGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class BeatThePeakGearFieldsValidator extends ProgramGearFieldsValidator<BeatThePeakGearFields> {
    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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
        yukonApiValidationUtils.checkIfFieldRequired("indicator", errors, beatThePeakCycleGear.getIndicator(),
            "BTP LED Indicator");

        // Check Max Indicator Timeout
        yukonApiValidationUtils.checkIfFieldRequired("timeoutInMinutes", errors, beatThePeakCycleGear.getTimeoutInMinutes(),
            "Max Indicator Timeout");
        if (!errors.hasFieldErrors("timeoutInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "timeoutInMinutes", beatThePeakCycleGear.getTimeoutInMinutes(), 0,
                99999, false);
        }

        // Check Resend Rate
        yukonApiValidationUtils.checkIfFieldRequired("resendInMinutes", errors, beatThePeakCycleGear.getResendInMinutes(),
            "Resend Rate");
        if (!errors.hasFieldErrors("resendInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "resendInMinutes", beatThePeakCycleGear.getResendInMinutes(), 0,
                99999, false);
        }

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(beatThePeakCycleGear.getWhenToChangeFields(), errors);
    }
}
