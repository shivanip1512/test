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
    
    public boolean isTrueCycleEnabled() {
        return getMethodOptionType().compareTo(OPTION_TRUE_CYCLE) == 0;
    }

    public void setTrueCycleEnabled(boolean doTrueCycle) {
        if (doTrueCycle) {
            setMethodOptionType(OPTION_TRUE_CYCLE);
        } else {
            setMethodOptionType(CtiUtilities.STRING_NONE);
        }
    }
    
    public Integer getCriticality() {
        return getMethodPeriod();
    }
    
    public void setCriticality(Integer criticality) {
        setMethodPeriod(criticality);
    }

    @Override
    public boolean useCustomDbRetrieve() {
        return false;
    }
}
