package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class EcobeeCycleGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {

    private static final long serialVersionUID = 1L;
    
    public EcobeeCycleGear() {
        setControlMethod(GearControlMethod.EcobeeCycle);
    }

    public Integer getDutyCyclePercent() {
        return getMethodRate();
    }

    public void setDutyCyclePercent(Integer seconds) {
        setMethodRate(seconds);
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
        return getMethodPeriod();
    }
    
    public void setCriticality(Integer criticality) {
        setMethodPeriod(criticality);
    }
    
    public String getMethodOptionTypes() {
        return getMethodOptionType();
    }

    public void setMethodOptionTypes(boolean isSelected) {
        if (isSelected) {
            setMethodOptionType(OPTION_MANDATORY);
        } else {
            setMethodOptionType(OPTION_OPTIONAL);
        }
    }

    public boolean isMandatorySelected(String methodOptionType) {
        return OPTION_MANDATORY.equalsIgnoreCase(methodOptionType);
    }

}
