package com.cannontech.common.inventory;

public enum HardwareClass {
    SWITCH,
    THERMOSTAT,
    METER,
    GATEWAY;
    
    public boolean isSwitch() {
        return (this == HardwareClass.SWITCH);
    }
    
    public boolean isMeter() {
        return (this == HardwareClass.METER);
    }
    
    public boolean isThermostat() {
    	return (this == HardwareClass.THERMOSTAT);
    }
    
    public boolean isGateway() {
    	return (this == HardwareClass.GATEWAY);
    }
}
