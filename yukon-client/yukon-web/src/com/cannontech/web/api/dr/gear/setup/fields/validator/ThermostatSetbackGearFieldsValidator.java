package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.Setpoint;
import com.cannontech.common.dr.gear.setup.fields.ThermostatSetbackGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class ThermostatSetbackGearFieldsValidator extends ProgramGearFieldsValidator<ThermostatSetbackGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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

        // Check for Absolutes or Deltas
        yukonApiValidationUtils.checkIfFieldRequired("setpoint", errors, thermostatSetBackCycleGear.getSetpoint(),
                "Absolute Or Delta");

        // Check for Fahrenheit or Celsius
        yukonApiValidationUtils.checkIfFieldRequired("tempMeasureUnit", errors, thermostatSetBackCycleGear.getTempMeasureUnit(),
                "Temperature Measure Unit");

        // Check for Heat Mode
        yukonApiValidationUtils.checkIfFieldRequired("isHeatMode", errors, thermostatSetBackCycleGear.getIsHeatMode(),
                "Heating Mode");

        // Check for Cooling Mode
        yukonApiValidationUtils.checkIfFieldRequired("isCoolMode", errors, thermostatSetBackCycleGear.getIsCoolMode(),
                "Cooling Mode");

        // Check for Min Value
        yukonApiValidationUtils.checkIfFieldRequired("minValue", errors, thermostatSetBackCycleGear.getMinValue(),
                "Minimum Temperature");
        if (!errors.hasFieldErrors("minValue")) {
            yukonApiValidationUtils.checkRange(errors, "minValue", thermostatSetBackCycleGear.getMinValue(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Max Value
        yukonApiValidationUtils.checkIfFieldRequired("maxValue", errors, thermostatSetBackCycleGear.getMaxValue(),
                "Maximum Temperature");
        if (!errors.hasFieldErrors("maxValue")) {
            yukonApiValidationUtils.checkRange(errors, "maxValue", thermostatSetBackCycleGear.getMaxValue(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Rand
        yukonApiValidationUtils.checkIfFieldRequired("random", errors, thermostatSetBackCycleGear.getRandom(), "Random Offset Time");
        if (!errors.hasFieldErrors("random")) {
            yukonApiValidationUtils.checkRange(errors, "random", thermostatSetBackCycleGear.getRandom(), Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
        }

        // Check for Abs or Delta B
        yukonApiValidationUtils.checkIfFieldRequired("valueB", errors, thermostatSetBackCycleGear.getValueB(),
                (thermostatSetBackCycleGear.getSetpoint() == Setpoint.DELTA) ? "Delta B" : "Abs B");
        if (!errors.hasFieldErrors("valueB")) {
            yukonApiValidationUtils.checkRange(errors, "valueB", thermostatSetBackCycleGear.getValueB(), Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
        }

        // Check for Abs or Delta D
        yukonApiValidationUtils.checkIfFieldRequired("valueD", errors, thermostatSetBackCycleGear.getValueD(),
                (thermostatSetBackCycleGear.getSetpoint() == Setpoint.DELTA) ? "Delta D" : "Abs D");
        if (!errors.hasFieldErrors("valueD")) {
            yukonApiValidationUtils.checkRange(errors, "valueD", thermostatSetBackCycleGear.getValueD(), Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
        }

        // Check for Abs or Delta F
        yukonApiValidationUtils.checkIfFieldRequired("valueF", errors, thermostatSetBackCycleGear.getValueF(),
                (thermostatSetBackCycleGear.getSetpoint() == Setpoint.DELTA) ? "Delta F" : "Abs F");
        if (!errors.hasFieldErrors("valueF")) {
            yukonApiValidationUtils.checkRange(errors, "valueF", thermostatSetBackCycleGear.getValueF(), Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
        }

        // Check for Ta
        yukonApiValidationUtils.checkIfFieldRequired("valueTa", errors, thermostatSetBackCycleGear.getValueTa(), "Ta");
        if (!errors.hasFieldErrors("valueTa")) {
            yukonApiValidationUtils.checkRange(errors, "valueTa", thermostatSetBackCycleGear.getValueTa(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Tb
        yukonApiValidationUtils.checkIfFieldRequired("valueTb", errors, thermostatSetBackCycleGear.getValueTb(), "Tb");
        if (!errors.hasFieldErrors("valueTb")) {
            yukonApiValidationUtils.checkRange(errors, "valueTb", thermostatSetBackCycleGear.getValueTb(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Tc
        yukonApiValidationUtils.checkIfFieldRequired("valueTc", errors, thermostatSetBackCycleGear.getValueTc(), "Tc");
        if (!errors.hasFieldErrors("valueTc")) {
            yukonApiValidationUtils.checkRange(errors, "valueTc", thermostatSetBackCycleGear.getValueTc(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Td
        yukonApiValidationUtils.checkIfFieldRequired("valueTd", errors, thermostatSetBackCycleGear.getValueTd(), "Td");
        if (!errors.hasFieldErrors("valueTd")) {
            yukonApiValidationUtils.checkRange(errors, "valueTd", thermostatSetBackCycleGear.getValueTd(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Te
        yukonApiValidationUtils.checkIfFieldRequired("valueTe", errors, thermostatSetBackCycleGear.getValueTe(), "Te");
        if (!errors.hasFieldErrors("valueTe")) {
            yukonApiValidationUtils.checkRange(errors, "valueTe", thermostatSetBackCycleGear.getValueTe(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check for Tf
        yukonApiValidationUtils.checkIfFieldRequired("valueTf", errors, thermostatSetBackCycleGear.getValueTf(), "Tf");
        if (!errors.hasFieldErrors("valueTf")) {
            yukonApiValidationUtils.checkRange(errors, "valueTf", thermostatSetBackCycleGear.getValueTf(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        }

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(thermostatSetBackCycleGear.getWhenToChangeFields(), errors);

        // Check for How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(thermostatSetBackCycleGear.getHowToStopControl(), getControlMethod(),
                errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(thermostatSetBackCycleGear.getCapacityReduction(), errors);

    }

}
