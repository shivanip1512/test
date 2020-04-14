package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.Setpoint;
import com.cannontech.common.dr.gear.setup.fields.ThermostatSetbackGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class ThermostatSetbackGearFieldsValidator extends ProgramGearFieldsValidator<ThermostatSetbackGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public ThermostatSetbackGearFieldsValidator() {
        super(ThermostatSetbackGearFields.class);
    }

    public ThermostatSetbackGearFieldsValidator(Class<ThermostatSetbackGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.ThermostatRamping;
    }

    @Override
    protected void doValidation(ThermostatSetbackGearFields thermostatSetBackCycleGear, Errors errors) {

        //Check for Absolutes or Deltas
        lmValidatorHelper.checkIfFieldRequired("setpoint", errors, thermostatSetBackCycleGear.getSetpoint(),
                "Absolute Or Delta");

        // Check for Fahrenheit or Celsius
        lmValidatorHelper.checkIfFieldRequired("tempMeasureUnit", errors, thermostatSetBackCycleGear.getTempMeasureUnit(),
                "Temperature Measure Unit");

        // Check for Heat Mode
        lmValidatorHelper.checkIfFieldRequired("isHeatMode", errors, thermostatSetBackCycleGear.getIsHeatMode(),
            "Heating Mode");

        // Check for Cooling Mode
        lmValidatorHelper.checkIfFieldRequired("isCoolMode", errors, thermostatSetBackCycleGear.getIsCoolMode(),
            "Cooling Mode");

        // Check for Min Value
        lmValidatorHelper.checkIfFieldRequired("minValue", errors, thermostatSetBackCycleGear.getMinValue(),
            "Minimum Temperature");
        if (!errors.hasFieldErrors("minValue")) {
            YukonValidationUtils.checkRange(errors, "minValue", thermostatSetBackCycleGear.getMinValue(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Max Value
        lmValidatorHelper.checkIfFieldRequired("maxValue", errors, thermostatSetBackCycleGear.getMaxValue(),
            "Maximum Temperature");
        if (!errors.hasFieldErrors("maxValue")) {
            YukonValidationUtils.checkRange(errors, "maxValue", thermostatSetBackCycleGear.getMaxValue(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Rand
        lmValidatorHelper.checkIfFieldRequired("random", errors, thermostatSetBackCycleGear.getRandom(), "Random Offset Time");
        if (!errors.hasFieldErrors("random")) {
            YukonValidationUtils.checkRange(errors, "random", thermostatSetBackCycleGear.getRandom(), Integer.MIN_VALUE,
                Integer.MAX_VALUE, false);
        }

        // Check for Abs or Delta B
        lmValidatorHelper.checkIfFieldRequired("valueB", errors, thermostatSetBackCycleGear.getValueB(),
            (thermostatSetBackCycleGear.getSetpoint() == Setpoint.DELTA) ? "Delta B" : "Abs B");
        if (!errors.hasFieldErrors("valueB")) {
            YukonValidationUtils.checkRange(errors, "valueB", thermostatSetBackCycleGear.getValueB(), Integer.MIN_VALUE,
                Integer.MAX_VALUE, false);
        }

        // Check for Abs or Delta D
        lmValidatorHelper.checkIfFieldRequired("valueD", errors, thermostatSetBackCycleGear.getValueD(),
            (thermostatSetBackCycleGear.getSetpoint() == Setpoint.DELTA) ? "Delta D" : "Abs D");
        if (!errors.hasFieldErrors("valueD")) {
            YukonValidationUtils.checkRange(errors, "valueD", thermostatSetBackCycleGear.getValueD(), Integer.MIN_VALUE,
                Integer.MAX_VALUE, false);
        }

        // Check for Abs or Delta F
        lmValidatorHelper.checkIfFieldRequired("valueF", errors, thermostatSetBackCycleGear.getValueF(),
            (thermostatSetBackCycleGear.getSetpoint() == Setpoint.DELTA) ? "Delta F" : "Abs F");
        if (!errors.hasFieldErrors("valueF")) {
            YukonValidationUtils.checkRange(errors, "valueF", thermostatSetBackCycleGear.getValueF(), Integer.MIN_VALUE,
                Integer.MAX_VALUE, false);
        }

        // Check for Ta
        lmValidatorHelper.checkIfFieldRequired("valueTa", errors, thermostatSetBackCycleGear.getValueTa(), "Ta");
        if (!errors.hasFieldErrors("valueTa")) {
            YukonValidationUtils.checkRange(errors, "valueTa", thermostatSetBackCycleGear.getValueTa(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Tb
        lmValidatorHelper.checkIfFieldRequired("valueTb", errors, thermostatSetBackCycleGear.getValueTb(), "Tb");
        if (!errors.hasFieldErrors("valueTb")) {
            YukonValidationUtils.checkRange(errors, "valueTb", thermostatSetBackCycleGear.getValueTb(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Tc
        lmValidatorHelper.checkIfFieldRequired("valueTc", errors, thermostatSetBackCycleGear.getValueTc(), "Tc");
        if (!errors.hasFieldErrors("valueTc")) {
            YukonValidationUtils.checkRange(errors, "valueTc", thermostatSetBackCycleGear.getValueTc(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Td
        lmValidatorHelper.checkIfFieldRequired("valueTd", errors, thermostatSetBackCycleGear.getValueTd(), "Td");
        if (!errors.hasFieldErrors("valueTd")) {
            YukonValidationUtils.checkRange(errors, "valueTd", thermostatSetBackCycleGear.getValueTd(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Te
        lmValidatorHelper.checkIfFieldRequired("valueTe", errors, thermostatSetBackCycleGear.getValueTe(), "Te");
        if (!errors.hasFieldErrors("valueTe")) {
            YukonValidationUtils.checkRange(errors, "valueTe", thermostatSetBackCycleGear.getValueTe(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Tf
        lmValidatorHelper.checkIfFieldRequired("valueTf", errors, thermostatSetBackCycleGear.getValueTf(), "Tf");
        if (!errors.hasFieldErrors("valueTf")) {
            YukonValidationUtils.checkRange(errors, "valueTf", thermostatSetBackCycleGear.getValueTf(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(thermostatSetBackCycleGear.getWhenToChangeFields(), errors);

        // Check for How to Stop Control
        gearValidatorHelper.checkHowToStopControl(thermostatSetBackCycleGear.getHowToStopControl(), getControlMethod(),
            errors);

        // Check for Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(thermostatSetBackCycleGear.getCapacityReduction(), errors);

    }

}
