package com.cannontech.database.data.device.lm;

import java.math.BigDecimal;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class SepTemperatureOffsetGear extends com.cannontech.database.db.device.lm.LMThermostatGear {
    private static final long serialVersionUID = 5579206107124081082L;

    public static final Double  MAX_FAHRENHEIT = 77.7;
    public static final Double  MAX_CELSIUS = 25.4;
    
    public SepTemperatureOffsetGear() {
        super();
        setControlMethod(GearControlMethod.SepTemperatureOffset);
    }

    public boolean isFrontRampEnabled() {
        return getFrontRampOption().compareTo(RAMP_RANDOM) == 0;
    }

    public void setFrontRampEnabled(boolean doRamp) {
        if (doRamp) {
            setFrontRampOption(RAMP_RANDOM);
        } else {
            setFrontRampOption(RAMP_NO_RAMP);
        }
    }
    
    public boolean isBackRampEnabled() {
        return getBackRampOption().compareTo(RAMP_RANDOM) == 0;
    }

    public void setBackRampEnabled(boolean doRamp) {
        if (doRamp) {
            setBackRampOption(RAMP_RANDOM);
        } else {
            setBackRampOption(RAMP_NO_RAMP);
        }
    }
    
    public Integer getCriticality() {
        return getMethodPeriod();
    }
    
    public void setCriticality(Integer criticality) {
        setMethodPeriod(criticality);
    }
    
    //Store heating & cooling offset values as integers that represent tenths of a degree.
    //Motivated by need to store a single decimal point floating value in an int database field.
    public void setHeatingOffset(Double offset) {
        setValueTa((int) (offset * 10));
    }
    
    public void setCoolingOffset(Double offset) {
        setValueTb((int) (offset * 10));
    }
    
    public Double getHeatingOffset() {
        return (getValueTa() / 10.0);
    }
    
    public Double getCoolingOffset() {
        return ( getValueTb() / 10.0);
    }
}
