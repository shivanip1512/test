package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

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

        // Degree F or Degree C

        // Heating Mode or Cooling Mode

        // Check Heating offset or Cooling Offset
        errors.pushNestedPath("mode");
        lmValidatorHelper.checkIfFieldRequired("offset", errors, sepTemperatureOffsetCycleGear.getOffset(), "Offset");
        if (!errors.hasFieldErrors("offset")) {
            if (sepTemperatureOffsetCycleGear.getCelsiusOrFahrenheit() == TemperatureMeasureUnit.FAHRENHEIT) {
                YukonValidationUtils.checkRange(errors, "offset", sepTemperatureOffsetCycleGear.getOffset(), 0.1, 77.7,
                    false);
            } else {
                YukonValidationUtils.checkRange(errors, "offset", sepTemperatureOffsetCycleGear.getOffset(), 0.1, 25.4,
                    false);
            }
        }
        errors.popNestedPath();

        // Check Criticality

        // Check How to Stop Control

        // Check Group Capacity Reduction

        // Check When to Change
    }

}
