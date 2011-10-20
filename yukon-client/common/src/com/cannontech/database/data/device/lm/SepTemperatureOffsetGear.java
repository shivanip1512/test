package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class SepTemperatureOffsetGear extends com.cannontech.database.db.device.lm.LMThermostatGear {
    private static final long serialVersionUID = 0L;

    public static final Double MAX_FAHRENHEIT = 77.7;
    public static final Double MAX_CELSIUS = 25.4;
    public static final Double MIN_FAHRENHEIT = .1;
    public static final Double MIN_CELSIUS = .1;
    
    public SepTemperatureOffsetGear() {
        super();
        setControlMethod(GearControlMethod.SepTemperatureOffset);
    }

    public boolean isFrontRampEnabled() {
        return getFrontRampOption().equals(RAMP_RANDOM);
    }

    public void setFrontRampEnabled(boolean doRamp) {
        if (doRamp) {
            setFrontRampOption(RAMP_RANDOM);
        } else {
            setFrontRampOption(RAMP_NO_RAMP);
        }
    }
    
    public boolean isBackRampEnabled() {
        return getBackRampOption().equals(RAMP_RANDOM);
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
    
    //Offset values saved as ints representing tenths of a degree.
    public void setHeatingOffset(double offset) {
        setValueTa((int)(offset * 10));
    }
    
    public void setCoolingOffset(double offset) {
        setValueTb((int)(offset * 10));
    }
    
    public Double getHeatingOffset() {
        return (getValueTa() / 10.0);
    }
    
    public Double getCoolingOffset() {
        return (getValueTb() / 10.0);
    }
}
