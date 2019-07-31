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
        // Check Absolutes or Deltas
        lmValidatorHelper.checkIfFieldRequired("absoluteOrDelta", errors,
            thermostatSetBackCycleGear.getAbsoluteOrDelta(), "Absolute Or Delta");

        // Check for Fahrenheit or Celsius
        lmValidatorHelper.checkIfFieldRequired("measureUnit", errors, thermostatSetBackCycleGear.getMeasureUnit(),
            "Temperature Measure Unit");

        // Check for Heat Mode
        gearValidatorHelper.checkHeatingMode(thermostatSetBackCycleGear.isHeatMode(), errors);

        // Check for Cooling Mode
        gearValidatorHelper.checkHeatingMode(thermostatSetBackCycleGear.isCoolMode(), errors);

        // Check for Min Value
        if (thermostatSetBackCycleGear.getMinValue() != null) {
            YukonValidationUtils.checkRange(errors, "minValue", thermostatSetBackCycleGear.getMinValue(), -2147483648,
                2147483647, false);
        }

        // Check for Max Value
        if (thermostatSetBackCycleGear.getMaxValue() != null) {
            YukonValidationUtils.checkRange(errors, "maxValue", thermostatSetBackCycleGear.getMaxValue(), -2147483648,
                2147483647, false);
        }

        // Check for Rand
        if (thermostatSetBackCycleGear.getRandom() != null) {
            YukonValidationUtils.checkRange(errors, "random", thermostatSetBackCycleGear.getRandom(), -2147483648,
                2147483647, false);
        }

        // Check for Abs or Delta B
        if (thermostatSetBackCycleGear.getValueB() != null) {
            YukonValidationUtils.checkRange(errors, "valueB", thermostatSetBackCycleGear.getValueB(), -2147483648,
                2147483647, false);
        }

        // Check for Abs or Delta D
        if (thermostatSetBackCycleGear.getValueD() != null) {
            YukonValidationUtils.checkRange(errors, "valueD", thermostatSetBackCycleGear.getValueD(), -2147483648,
                2147483647, false);
        }

        // Check for Abs or Delta F
        if (thermostatSetBackCycleGear.getValueF() != null) {
            YukonValidationUtils.checkRange(errors, "valueF", thermostatSetBackCycleGear.getValueF(), -2147483648,
                2147483647, false);
        }

        // Check for Ta
        if (thermostatSetBackCycleGear.getValueTa() != null) {
            YukonValidationUtils.checkRange(errors, "valueTa", thermostatSetBackCycleGear.getValueTa(), -2147483648,
                2147483647, false);
        }

        // Check for Tb
        if (thermostatSetBackCycleGear.getValueTb() != null) {
            YukonValidationUtils.checkRange(errors, "valueTb", thermostatSetBackCycleGear.getValueTb(), -2147483648,
                2147483647, false);
        }

        // Check for Tc
        if (thermostatSetBackCycleGear.getValueTc() != null) {
            YukonValidationUtils.checkRange(errors, "valueTc", thermostatSetBackCycleGear.getValueTc(), -2147483648,
                2147483647, false);
        }

        // Check for Td
        if (thermostatSetBackCycleGear.getValueTd() != null) {
            YukonValidationUtils.checkRange(errors, "valueTd", thermostatSetBackCycleGear.getValueTd(), -2147483648,
                2147483647, false);
        }

        // Check for Te
        if (thermostatSetBackCycleGear.getValueTe() != null) {
            YukonValidationUtils.checkRange(errors, "valueTe", thermostatSetBackCycleGear.getValueTe(), -2147483648,
                2147483647, false);
        }

        // Check for Tf
        if (thermostatSetBackCycleGear.getValueTf() != null) {
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
