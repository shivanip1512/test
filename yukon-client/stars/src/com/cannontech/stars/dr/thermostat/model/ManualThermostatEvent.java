package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.Temperature;

public class ManualThermostatEvent extends ThermostatEvent {
    
    private Temperature manualTemp;
    private ThermostatMode manualMode;
    private ThermostatFanState manualFan;
    private boolean manualHold;

    public Temperature getManualTemp() {
        return manualTemp;
    }
    
    public CelsiusTemperature getManualTempInC() {
        return manualTemp.toCelsius();
    }
    
    public void setManualTemp(Temperature manualTemp) {
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
