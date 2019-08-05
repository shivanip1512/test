package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.RotationGearFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class RotationGearFieldsValidator extends ProgramGearFieldsValidator<RotationGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

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
        lmValidatorHelper.checkIfFieldRequired("shedTime", errors, rotationCycleGear.getShedTime(), "Shed Time");
        if (!errors.hasFieldErrors("shedTime")) {
            if (!TimeIntervals.getRotationshedtime().contains(rotationCycleGear.getShedTime())) {
                errors.rejectValue("shedTime", invalidKey, new Object[] { "Shed Time" }, "");
            }
        }

        // Check No. Of Groups
        gearValidatorHelper.checkNumberOfGroups(rotationCycleGear.getNumberOfGroups(), errors);

        // Check Command Resend Rate
        gearValidatorHelper.checkCommandResendRate(rotationCycleGear.getSendRate(), errors);

        // Group Selection Method

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(rotationCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(rotationCycleGear.getCapacityReduction(), errors);

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(rotationCycleGear.getWhenToChangeFields(), errors);
    }

}
