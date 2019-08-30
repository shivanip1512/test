package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.common.dr.gear.setup.fields.SepTemperatureOffsetGearFields;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class SepTemperatureOffsetGearFieldsValidator
        extends ProgramGearFieldsValidator<SepTemperatureOffsetGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public SepTemperatureOffsetGearFieldsValidator() {
        super(SepTemperatureOffsetGearFields.class);
    }

    public SepTemperatureOffsetGearFieldsValidator(Class<SepTemperatureOffsetGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.SepTemperatureOffset;
    }

    @Override
    protected void doValidation(SepTemperatureOffsetGearFields sepTemperatureOffsetCycleGear, Errors errors) {
        // Check Ramp In
        gearValidatorHelper.checkRampIn(sepTemperatureOffsetCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearValidatorHelper.checkRampOut(sepTemperatureOffsetCycleGear.getRampOut(), errors);

        // Check degree F/ degree C
        lmValidatorHelper.checkIfFieldRequired("celsiusOrFahrenheit", errors,
            sepTemperatureOffsetCycleGear.getCelsiusOrFahrenheit(), "Temperature Measure Unit");

        // Check Heating Mode or Cooling Mode
        lmValidatorHelper.checkIfFieldRequired("mode", errors, sepTemperatureOffsetCycleGear.getMode(), "Mode");

        // Check Heating offset or Cooling Offset
        lmValidatorHelper.checkIfFieldRequired("offset", errors, sepTemperatureOffsetCycleGear.getOffset(),
            (sepTemperatureOffsetCycleGear.getMode() == Mode.HEAT) ? "Heating Offset" : "Cooling Offset");
        if (!errors.hasFieldErrors("offset")) {
            if (sepTemperatureOffsetCycleGear.getCelsiusOrFahrenheit() == TemperatureMeasureUnit.FAHRENHEIT) {
                YukonValidationUtils.checkRange(errors, "offset", sepTemperatureOffsetCycleGear.getOffset(), 0.1, 77.7,
                    false);
            } else {
                YukonValidationUtils.checkRange(errors, "offset", sepTemperatureOffsetCycleGear.getOffset(), 0.1, 25.4,
                    false);
            }
        }

        // Check Criticality
        gearValidatorHelper.checkCriticality(sepTemperatureOffsetCycleGear.getCriticality(), errors);

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(sepTemperatureOffsetCycleGear.getHowToStopControl(),
            getControlMethod(), errors);

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(sepTemperatureOffsetCycleGear.getCapacityReduction(), errors);

        // Check When to Change
        gearValidatorHelper.checkWhenToChange(sepTemperatureOffsetCycleGear.getWhenToChangeFields(), errors);
    }

}
