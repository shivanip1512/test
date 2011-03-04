package com.cannontech.common.inventory;

public enum LMHardwareClass {
    SWITCH,
    THERMOSTAT,
    METER,
    GATEWAY;
    
    public boolean isSwitch() {
        return (this == LMHardwareClass.SWITCH);
    }
    
    public boolean isMeter() {
        return (this == LMHardwareClass.METER);
    }
    
    public boolean isThermostat() {
    	return (this == LMHardwareClass.THERMOSTAT);
    }
    
    public boolean isGateway() {
    	return (this == LMHardwareClass.GATEWAY);
    }
}
