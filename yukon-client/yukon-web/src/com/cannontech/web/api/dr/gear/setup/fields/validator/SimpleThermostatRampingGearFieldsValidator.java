package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SimpleThermostatRampingGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class SimpleThermostatRampingGearFieldsValidator
        extends ProgramGearFieldsValidator<SimpleThermostatRampingGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public SimpleThermostatRampingGearFieldsValidator() {
        super(SimpleThermostatRampingGearFields.class);
    }

    public SimpleThermostatRampingGearFieldsValidator(Class<SimpleThermostatRampingGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.SimpleThermostatRamping;
    }

    @Override
    protected void doValidation(SimpleThermostatRampingGearFields simpleThermostatRampingCycleGear, Errors errors) {
        // Check Heat Mode or Cool Mode
        gearValidatorHelper.checkTemperatureMode(simpleThermostatRampingCycleGear.getMode(), errors);

        // Check Random Start Time
        lmValidatorHelper.checkIfFieldRequired("randomStartTimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getRandomStartTimeInMinutes(), "Random Start Time");
        if (!errors.hasFieldErrors("randomStartTimeInMinutes")) {
            YukonValidationUtils.checkRange(errors, "randomStartTimeInMinutes",
                simpleThermostatRampingCycleGear.getRandomStartTimeInMinutes(), 0, 2, false);
        }

        // Check Pre-op (Cool or Heat) Temp
        lmValidatorHelper.checkIfFieldRequired("preOpTemp", errors, simpleThermostatRampingCycleGear.getPreOpTemp(),
            "Pre-op Temp");
        if (!errors.hasFieldErrors("preOpTemp")) {
            YukonValidationUtils.checkRange(errors, "preOpTemp", simpleThermostatRampingCycleGear.getPreOpTemp(), -20,
                20, false);
        }

        // Check Pre-op (Cool or Heat) Time
        lmValidatorHelper.checkIfFieldRequired("preOpTimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getPreOpTimeInMinutes(), "Pre-op Time");
        if (!errors.hasFieldErrors("preOpTimeInMinutes")) {
            YukonValidationUtils.checkRange(errors, "preOpTimeInMinutes",
                simpleThermostatRampingCycleGear.getPreOpTimeInMinutes(), 0, 5, false);
        }

        // Check Pre-op (Cool or Heat) Hold
        lmValidatorHelper.checkIfFieldRequired("preOpHoldInMinutes", errors,
            simpleThermostatRampingCycleGear.getPreOpHoldInMinutes(), "Pre-op Hold");
        if (!errors.hasFieldErrors("preOpHoldInMinutes")) {
            YukonValidationUtils.checkRange(errors, "preOpHoldInMinutes",
                simpleThermostatRampingCycleGear.getPreOpHoldInMinutes(), 0, 5, false);
        }

        // Check Ramp degree F/Hour
        lmValidatorHelper.checkIfFieldRequired("rampPerHour", errors, simpleThermostatRampingCycleGear.getRampPerHour(),
            "Pre-op Hold");
        if (!errors.hasFieldErrors("rampPerHour")) {
            YukonValidationUtils.checkRange(errors, "rampPerHour", simpleThermostatRampingCycleGear.getRampPerHour(),
                (float) -9.9, (float) 9.9, false);
        }

        // Check Max degree delta
        lmValidatorHelper.checkIfFieldRequired("max", errors, simpleThermostatRampingCycleGear.getMax(),
            "Max degree Delta");
        if (!errors.hasFieldErrors("max")) {
            YukonValidationUtils.checkRange(errors, "max", simpleThermostatRampingCycleGear.getMax(), 0, 20, false);
        }

        // Check Ramp out Time
        lmValidatorHelper.checkIfFieldRequired("rampOutTimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getRampOutTimeInMinutes(), "Ramp Out Time");
        if (!errors.hasFieldErrors("rampOutTimeInMinutes")) {
            YukonValidationUtils.checkRange(errors, "rampOutTimeInMinutes",
                simpleThermostatRampingCycleGear.getRampOutTimeInMinutes(), 0, 5, false);
        }

        // Check Max Runtime
        lmValidatorHelper.checkIfFieldRequired("maxRuntimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getMaxRuntimeInMinutes(), "Max Runtime");
        if (!errors.hasFieldErrors("maxRuntimeInMinutes")) {
            YukonValidationUtils.checkRange(errors, "maxRuntimeInMinutes",
                simpleThermostatRampingCycleGear.getMaxRuntimeInMinutes(), 4, 24, false);
        }

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(simpleThermostatRampingCycleGear.getHowToStopControl(),
            getControlMethod(), errors);

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(simpleThermostatRampingCycleGear.getWhenToChangeFields(), errors);
    }

}
