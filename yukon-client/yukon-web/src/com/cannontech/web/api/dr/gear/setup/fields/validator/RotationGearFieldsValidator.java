package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.fields.RotationGearFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class RotationGearFieldsValidator extends ProgramGearFieldsValidator<RotationGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

    public RotationGearFieldsValidator() {
        super(RotationGearFields.class);
    }

    public RotationGearFieldsValidator(Class<RotationGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.Rotation;
    }

    @Override
    protected void doValidation(RotationGearFields rotationCycleGear, Errors errors) {
        // Check Shed Time
        yukonApiValidationUtils.checkIfFieldRequired("shedTime", errors, rotationCycleGear.getShedTime(), "Shed Time");
        if (!errors.hasFieldErrors("shedTime")) {
            TimeIntervals shedTime = TimeIntervals.fromSeconds(rotationCycleGear.getShedTime());
            if (!TimeIntervals.getRotationshedtime().contains(shedTime)) {
                errors.rejectValue("shedTime", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "Shed Time" }, "");
            }
        }

        // Check No. Of Groups
        gearApiValidatorHelper.checkNumberOfGroups(rotationCycleGear.getNumberOfGroups(), errors);

        // Check Command Resend Rate
        gearApiValidatorHelper.checkCommandResendRate(rotationCycleGear.getSendRate(), errors);

        // Check Group Selection Method
        gearApiValidatorHelper.checkGroupSelectionMethod(rotationCycleGear.getGroupSelectionMethod(), errors);

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(rotationCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(rotationCycleGear.getCapacityReduction(), errors);

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(rotationCycleGear.getWhenToChangeFields(), errors);
    }

}
