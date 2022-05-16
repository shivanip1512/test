package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.common.dr.gear.setup.fields.SepTemperatureOffsetGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class SepTemperatureOffsetGearFieldsValidator
        extends ProgramGearFieldsValidator<SepTemperatureOffsetGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

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
        gearApiValidatorHelper.checkRampIn(sepTemperatureOffsetCycleGear.getRampIn(), errors);

        // Check Ramp Out
        gearApiValidatorHelper.checkRampOut(sepTemperatureOffsetCycleGear.getRampOut(), errors);

        // Check degree F/ degree C
        yukonApiValidationUtils.checkIfFieldRequired("tempMeasureUnit", errors,
            sepTemperatureOffsetCycleGear.getTempMeasureUnit(), "Temperature Measure Unit");

        // Check Heating Mode or Cooling Mode
        yukonApiValidationUtils.checkIfFieldRequired("mode", errors, sepTemperatureOffsetCycleGear.getMode(), "Mode");

        // Check Heating offset or Cooling Offset
        yukonApiValidationUtils.checkIfFieldRequired("offset", errors, sepTemperatureOffsetCycleGear.getOffset(),
            (sepTemperatureOffsetCycleGear.getMode() == Mode.HEAT) ? "Heating Offset" : "Cooling Offset");
        if (!errors.hasFieldErrors("offset")) {
            if (sepTemperatureOffsetCycleGear.getTempMeasureUnit() == TemperatureMeasureUnit.FAHRENHEIT) {
                yukonApiValidationUtils.checkRange(errors, "offset", sepTemperatureOffsetCycleGear.getOffset(), 0.1, 77.7,
                    false);
            } else {
                yukonApiValidationUtils.checkRange(errors, "offset", sepTemperatureOffsetCycleGear.getOffset(), 0.1, 25.4,
                    false);
            }
        }

        // Check Criticality
        gearApiValidatorHelper.checkCriticality(sepTemperatureOffsetCycleGear.getCriticality(), errors);

        // Check How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(sepTemperatureOffsetCycleGear.getHowToStopControl(),
            getControlMethod(), errors);

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(sepTemperatureOffsetCycleGear.getCapacityReduction(), errors);

        // Check When to Change
        gearApiValidatorHelper.checkWhenToChange(sepTemperatureOffsetCycleGear.getWhenToChangeFields(), errors);
    }

}
