package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

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
        // Absolutes or Deltas

        // Fahrenheit or Celsius

        // Check for Heat Mode
        lmValidatorHelper.checkIfFieldRequired("isHeatMode", errors, thermostatSetBackCycleGear.getIsHeatMode(),
            "Heating Mode");

        // Check for Cooling Mode
        lmValidatorHelper.checkIfFieldRequired("isCoolMode", errors, thermostatSetBackCycleGear.getIsCoolMode(),
            "Cooling Mode");

        // Check for Min Value
        lmValidatorHelper.checkIfFieldRequired("minValue", errors, thermostatSetBackCycleGear.getMinValue(),
            "Min Value");
        if (!errors.hasFieldErrors("minValue")) {
            YukonValidationUtils.checkRange(errors, "minValue", thermostatSetBackCycleGear.getMinValue(), -2147483648,
                2147483647, false);
        }

        // Check for Max Value
        lmValidatorHelper.checkIfFieldRequired("maxValue", errors, thermostatSetBackCycleGear.getMaxValue(),
            "Max Value");
        if (!errors.hasFieldErrors("maxValue")) {
            YukonValidationUtils.checkRange(errors, "maxValue", thermostatSetBackCycleGear.getMaxValue(), -2147483648,
                2147483647, false);
        }

        // Check for Rand
        lmValidatorHelper.checkIfFieldRequired("random", errors, thermostatSetBackCycleGear.getRandom(), "Random");
        if (!errors.hasFieldErrors("random")) {
            YukonValidationUtils.checkRange(errors, "random", thermostatSetBackCycleGear.getRandom(), -2147483648,
                2147483647, false);
        }

        // Check for Abs or Delta B
        lmValidatorHelper.checkIfFieldRequired("valueB", errors, thermostatSetBackCycleGear.getValueB(), "Value B");
        if (!errors.hasFieldErrors("valueB")) {
            YukonValidationUtils.checkRange(errors, "valueB", thermostatSetBackCycleGear.getValueB(), -2147483648,
                2147483647, false);
        }

        // Check for Abs or Delta D
        lmValidatorHelper.checkIfFieldRequired("valueD", errors, thermostatSetBackCycleGear.getValueD(), "Value D");
        if (!errors.hasFieldErrors("valueD")) {
            YukonValidationUtils.checkRange(errors, "valueD", thermostatSetBackCycleGear.getValueD(), -2147483648,
                2147483647, false);
        }

        // Check for Abs or Delta F
        lmValidatorHelper.checkIfFieldRequired("valueF", errors, thermostatSetBackCycleGear.getValueF(), "Value F");
        if (!errors.hasFieldErrors("valueF")) {
            YukonValidationUtils.checkRange(errors, "valueF", thermostatSetBackCycleGear.getValueF(), -2147483648,
                2147483647, false);
        }

        // Check for Ta
        lmValidatorHelper.checkIfFieldRequired("valueTa", errors, thermostatSetBackCycleGear.getValueTa(), "Value Ta");
        if (!errors.hasFieldErrors("valueTa")) {
            YukonValidationUtils.checkRange(errors, "valueTa", thermostatSetBackCycleGear.getValueTa(), -2147483648,
                2147483647, false);
        }

        // Check for Tb
        lmValidatorHelper.checkIfFieldRequired("valueTb", errors, thermostatSetBackCycleGear.getValueTb(), "Value Tb");
        if (!errors.hasFieldErrors("valueTb")) {
            YukonValidationUtils.checkRange(errors, "valueTb", thermostatSetBackCycleGear.getValueTb(), -2147483648,
                2147483647, false);
        }

        // Check for Tc
        lmValidatorHelper.checkIfFieldRequired("valueTc", errors, thermostatSetBackCycleGear.getValueTc(), "Value Tc");
        if (!errors.hasFieldErrors("valueTc")) {
            YukonValidationUtils.checkRange(errors, "valueTc", thermostatSetBackCycleGear.getValueTc(), -2147483648,
                2147483647, false);
        }

        // Check for Td
        lmValidatorHelper.checkIfFieldRequired("valueTd", errors, thermostatSetBackCycleGear.getValueTd(), "Value Td");
        if (!errors.hasFieldErrors("valueTd")) {
            YukonValidationUtils.checkRange(errors, "valueTd", thermostatSetBackCycleGear.getValueTd(), -2147483648,
                2147483647, false);
        }

        // Check for Te
        lmValidatorHelper.checkIfFieldRequired("valueTe", errors, thermostatSetBackCycleGear.getValueTe(), "Value Te");
        if (!errors.hasFieldErrors("valueTe")) {
            YukonValidationUtils.checkRange(errors, "valueTe", thermostatSetBackCycleGear.getValueTe(), -2147483648,
                2147483647, false);
        }

        // Check for Tf
        lmValidatorHelper.checkIfFieldRequired("valueTf", errors, thermostatSetBackCycleGear.getValueTf(), "Value Tf");
        if (!errors.hasFieldErrors("valueTf")) {
            YukonValidationUtils.checkRange(errors, "valueTf", thermostatSetBackCycleGear.getValueTf(), -2147483648,
                2147483647, false);
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
