package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.util.CtiUtilities;

public class ManualThermostatEvent extends ThermostatEvent {
    
    private Integer manualTemp;
    private ThermostatMode manualMode;
    private ThermostatFanState manualFan;
    private boolean manualHold;

    public Integer getManualTemp() {
        return manualTemp;
    }
    
    public Integer getManualTempInC() {
        return CtiUtilities.convertTemperature(manualTemp, CtiUtilities.FAHRENHEIT_CHARACTER, CtiUtilities.CELSIUS_CHARACTER);
    }
    
    public void setManualTemp(Integer manualTemp) {
        this.manualTemp = manualTemp;
    }

    public ThermostatMode getManualMode() {
        return manualMode;
    }

    public void setManualMode(ThermostatMode manualMode) {
        this.manualMode = manualMode;
    }

    public ThermostatFanState getManualFan() {
        return manualFan;
    }

    public void setManualFan(ThermostatFanState manualFan) {
        this.manualFan = manualFan;
    }

    public boolean isManualHold() {
        return manualHold;
    }

    public void setManualHold(boolean manualHold) {
        this.manualHold = manualHold;
    }
    
    public ThermostatEventType getEventType() {
        return ThermostatEventType.MANUAL;
    }
}
