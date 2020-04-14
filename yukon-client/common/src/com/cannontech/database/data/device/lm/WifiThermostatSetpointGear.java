package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.LMProgramDirectGear;

/**
 * Abstract gear containing options shared by wifi thermostat setpoint gears.
 */
public abstract class WifiThermostatSetpointGear extends LMProgramDirectGear {
    
    protected static final String TABLE_NAME = "LMThermostatGear";
    protected static final String[] CONSTRAINT_COLUMNS = {"GearID"};
    protected static final String[] SETTER_COLUMNS = {"Settings", "MinValue", "MaxValue", "ValueB", "ValueD", "ValueF", 
            "Random", "ValueTa", "ValueTb", "ValueTc", "ValueTd", "ValueTe", "ValueTf", "RampRate"};
    
    private int setpointOffset = 0;
    private HeatCool heatCool = HeatCool.COOL;
    
    public void setMethodOptionType(boolean isSelected) {
        if (isSelected) {
            setMethodOptionType(OPTION_MANDATORY);
        } else {
            setMethodOptionType(OPTION_OPTIONAL);
        }
    }

    public boolean isMandatorySelected(String methodOptionType) {
        return OPTION_MANDATORY.equalsIgnoreCase(methodOptionType);
    }
    
    public void setSetpointOffset(int setpointOffset) {
        this.setpointOffset = setpointOffset;
    }
    
    public int getSetpointOffset() {
        return setpointOffset;
    }
    
    public void setHeatCool(HeatCool heatCool) {
        this.heatCool = heatCool;
    }
    
    public HeatCool getHeatCool() {
        return heatCool;
    }
}
