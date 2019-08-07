package com.cannontech.common.dr.gear.setup.fields;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.database.data.device.lm.SepTemperatureOffsetGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SepTemperatureOffsetGearFields implements ProgramGearFields {

    private Boolean rampIn;
    private Boolean rampOut;
    public TemperatureMeasureUnit celsiusOrFahrenheit;
    public Mode mode;
    private Double offset;
    private Integer criticality;

    private HowToStopControl howToStopControl;
    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

    public Boolean getRampIn() {
        return rampIn;
    }

    public void setRampIn(Boolean rampIn) {
        this.rampIn = rampIn;
    }

    public Boolean getRampOut() {
        return rampOut;
    }

    public void setRampOut(Boolean rampOut) {
        this.rampOut = rampOut;
    }

    public Double getOffset() {
        return offset;
    }

    public void setOffset(Double offset) {
        if (offset != null) {
            this.offset = new BigDecimal(offset).setScale(1, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.offset = offset;
        }
    }

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }

    public HowToStopControl getHowToStopControl() {
        return howToStopControl;
    }

    public void setHowToStopControl(HowToStopControl howToStopControl) {
        this.howToStopControl = howToStopControl;
    }

    public Integer getCapacityReduction() {
        return capacityReduction;
    }

    public void setCapacityReduction(Integer capacityReduction) {
        this.capacityReduction = capacityReduction;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    public TemperatureMeasureUnit getCelsiusOrFahrenheit() {
        return celsiusOrFahrenheit;
    }

    public void setCelsiusOrFahrenheit(TemperatureMeasureUnit celsiusOrFahrenheit) {
        this.celsiusOrFahrenheit = celsiusOrFahrenheit;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        SepTemperatureOffsetGear sepTemperatureOffsetGear = (SepTemperatureOffsetGear) programDirectGear;

        setRampIn(sepTemperatureOffsetGear.isFrontRampEnabled());
        setRampOut(sepTemperatureOffsetGear.isBackRampEnabled());

        double heatingOffset = sepTemperatureOffsetGear.getHeatingOffset();
        double coolingOffset = sepTemperatureOffsetGear.getCoolingOffset();

        if (heatingOffset != 0.0 && coolingOffset == 0.0) {
            setOffset(sepTemperatureOffsetGear.getHeatingOffset());
            setMode(Mode.HEAT);
        } else if (heatingOffset == 0.0 && coolingOffset != 0.0) {
            setOffset(sepTemperatureOffsetGear.getCoolingOffset());
            setMode(Mode.COOL);
        } else if (heatingOffset == 0.0 && coolingOffset == 0.0) {
            setOffset(0.0);
            setMode(Mode.HEAT);
        } else {
            throw new RuntimeException("Illegal database values: Heating and cooling offsets cannot both be nonzero");
        }

        if (sepTemperatureOffsetGear.getSettings().charAt(1) == 'C') {
            setCelsiusOrFahrenheit(TemperatureMeasureUnit.CELSIUS);
        } else {
            setCelsiusOrFahrenheit(TemperatureMeasureUnit.FAHRENHEIT);
        }

        setCriticality(sepTemperatureOffsetGear.getCriticality());
        setHowToStopControl(HowToStopControl.valueOf(sepTemperatureOffsetGear.getMethodStopType()));
        setCapacityReduction(sepTemperatureOffsetGear.getPercentReduction());

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        SepTemperatureOffsetGear temperatureOffsetGear = (SepTemperatureOffsetGear) programDirectGear;

        temperatureOffsetGear.setMethodStopType(getHowToStopControl().name());
        temperatureOffsetGear.getSettings().setCharAt(1,
            getCelsiusOrFahrenheit() == TemperatureMeasureUnit.FAHRENHEIT ? 'F' : 'C');

        temperatureOffsetGear.setFrontRampEnabled(getRampIn());
        temperatureOffsetGear.setBackRampEnabled(getRampOut());

        if (getMode() == Mode.HEAT) {
            temperatureOffsetGear.setHeatingOffset(getOffset());
            temperatureOffsetGear.setCoolingOffset(0.0);
        } else if (getMode() == Mode.COOL) {
            temperatureOffsetGear.setHeatingOffset(0.0);
            temperatureOffsetGear.setCoolingOffset(getOffset());
        }

        temperatureOffsetGear.setCriticality(getCriticality());
        temperatureOffsetGear.setMethodStopType(getHowToStopControl().name());
        temperatureOffsetGear.setPercentReduction(getCapacityReduction());

        whenToChangeFields.buildDBPersistent(programDirectGear);
    }

}
