package com.cannontech.database.data.device.lm;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class SepCycleGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {

    private static final long serialVersionUID = 8669786758447311963L;

    public SepCycleGear() {
        super();
        setControlMethod(GearControlMethod.SepCycle);
    }

    public Integer getControlPercent() {
        return getMethodRate();
    }

    public void setControlPercent(Integer seconds) {
        setMethodRate(seconds);
    }

    public boolean isFrontRampEnabled() {
        if (getFrontRampOption().compareTo(RAMP_RANDOM) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setFrontRampEnabled(boolean doRamp) {
        if (doRamp) {
            setFrontRampOption(RAMP_RANDOM);
        } else {
            setFrontRampOption(RAMP_NO_RAMP);
        }
    }
    
    public boolean isBackRampEnabled() {
        if (getBackRampOption().compareTo(RAMP_RANDOM) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setBackRampEnabled(boolean doRamp) {
        if (doRamp) {
            setBackRampOption(RAMP_RANDOM);
        } else {
            setBackRampOption(RAMP_NO_RAMP);
        }
    }
    
    public boolean isTrueCycleEnabled() {
        if (getMethodOptionType().compareTo(CONTROL_TRUE_CYCLE) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setTrueCycleEnabled(boolean doTrueCycle) {
        if (doTrueCycle) {
            setMethodOptionType(CONTROL_TRUE_CYCLE);
        } else {
            setMethodOptionType(CtiUtilities.STRING_NONE);
        }
    }

}
