package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.SimpleThermostatRampingGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class SimpleThermostatRampingGearFieldsValidator
        extends ProgramGearFieldsValidator<SimpleThermostatRampingGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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

        // Check Random Start Time
        yukonApiValidationUtils.checkIfFieldRequired("randomStartTimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getRandomStartTimeInMinutes(), "Random Start Time");
        if (!errors.hasFieldErrors("randomStartTimeInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "randomStartTimeInMinutes",
                simpleThermostatRampingCycleGear.getRandomStartTimeInMinutes(), 0, 120, false);
        }

        // Check Heating Mode or Cooling Mode
        yukonApiValidationUtils.checkIfFieldRequired("mode", errors, simpleThermostatRampingCycleGear.getMode(), "Mode");

        // Check Pre-op (Cool or Heat) Temp
        yukonApiValidationUtils.checkIfFieldRequired("preOpTemp", errors, simpleThermostatRampingCycleGear.getPreOpTemp(),
            "Temp");
        if (!errors.hasFieldErrors("preOpTemp")) {
            yukonApiValidationUtils.checkRange(errors, "preOpTemp", simpleThermostatRampingCycleGear.getPreOpTemp(), -20,
                20, false);
        }

        // Check Pre-op (Cool or Heat) Time
        yukonApiValidationUtils.checkIfFieldRequired("preOpTimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getPreOpTimeInMinutes(), "Time");
        if (!errors.hasFieldErrors("preOpTimeInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "preOpTimeInMinutes",
                simpleThermostatRampingCycleGear.getPreOpTimeInMinutes(), 0, 300, false);
        }

        // Check Pre-op (Cool or Heat) Hold
        yukonApiValidationUtils.checkIfFieldRequired("preOpHoldInMinutes", errors,
            simpleThermostatRampingCycleGear.getPreOpHoldInMinutes(), "Hold");
        if (!errors.hasFieldErrors("preOpHoldInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "preOpHoldInMinutes",
                simpleThermostatRampingCycleGear.getPreOpHoldInMinutes(), 0, 300, false);
        }

        // Check Ramp degree F/Hour
        yukonApiValidationUtils.checkIfFieldRequired("rampPerHour", errors, simpleThermostatRampingCycleGear.getRampPerHour(),
            "Ramp °F/Hour");
        if (!errors.hasFieldErrors("rampPerHour")) {
            yukonApiValidationUtils.checkRange(errors, "rampPerHour", simpleThermostatRampingCycleGear.getRampPerHour(),
                (float) -9.9, (float) 9.9, false);
        }

        // Check Max degree delta
        yukonApiValidationUtils.checkIfFieldRequired("max", errors, simpleThermostatRampingCycleGear.getMax(),
            "Max °\u0394");
        if (!errors.hasFieldErrors("max")) {
            yukonApiValidationUtils.checkRange(errors, "max", simpleThermostatRampingCycleGear.getMax(), 0, 20, false);
        }

        // Check Ramp out Time
        yukonApiValidationUtils.checkIfFieldRequired("rampOutTimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getRampOutTimeInMinutes(), "Ramp out Time");
        if (!errors.hasFieldErrors("rampOutTimeInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "rampOutTimeInMinutes",
                simpleThermostatRampingCycleGear.getRampOutTimeInMinutes(), 0, 300, false);
        }

        // Check Max Runtime
        yukonApiValidationUtils.checkIfFieldRequired("maxRuntimeInMinutes", errors,
            simpleThermostatRampingCycleGear.getMaxRuntimeInMinutes(), "Max Runtime");
        if (!errors.hasFieldErrors("maxRuntimeInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "maxRuntimeInMinutes",
                simpleThermostatRampingCycleGear.getMaxRuntimeInMinutes(), 240, 1439, false);
        }

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(simpleThermostatRampingCycleGear.getHowToStopControl(),
            getControlMethod(), errors);

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(simpleThermostatRampingCycleGear.getWhenToChangeFields(), errors);
    }

}
