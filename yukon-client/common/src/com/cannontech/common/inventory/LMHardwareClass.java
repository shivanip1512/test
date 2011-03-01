package com.cannontech.common.inventory;

public enum LMHardwareClass {
    SWITCH,
    THERMOSTAT,
    METER,
    GATEWAY;
    
    public boolean isThermostat() {
    	if ( this == LMHardwareClass.THERMOSTAT) {
    		return true;
    	}
    	return false;
    }
    
    public boolean isGateway() {
    	if ( this == LMHardwareClass.GATEWAY) {
    		return true;
    	}
    	return false;
    }
}
