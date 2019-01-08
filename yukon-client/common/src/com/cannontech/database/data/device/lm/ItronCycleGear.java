package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class ItronCycleGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {

    private static final long serialVersionUID = 1L;

    public ItronCycleGear() {
        setControlMethod(GearControlMethod.ItronCycle);
    }

    public boolean isFrontRampEnabled() {
        return RAMP_RANDOM.equals(getFrontRampOption());
    }

    public void setFrontRampEnabled(boolean doRamp) {
        if (doRamp) {
            setFrontRampOption(RAMP_RANDOM);
        } else {
            setFrontRampOption(RAMP_NO_RAMP);
        }
    }

    public boolean isBackRampEnabled() {
        return RAMP_RANDOM.equals(getBackRampOption());
    }

    public void setBackRampEnabled(boolean doRamp) {
        if (doRamp) {
            setBackRampOption(RAMP_RANDOM);
        } else {
            setBackRampOption(RAMP_NO_RAMP);
        }
    }

    public Integer getCriticality() {
        return Integer.parseInt(getMethodOptionType());
    }

    public void setCriticality(Integer criticality) {
        this.setMethodOptionType(criticality.toString());
    }

    public Integer getControlPercent() {
        return getMethodRate();
    }

    public Integer getCyclePeriod() {
        return getMethodPeriod() / 60;
    }

    public void setControlPercent(Integer percent) {
        setMethodRate(percent);
    }

    public void setCyclePeriod(Integer period) {
        setMethodPeriod(period * 60);
    }
}
