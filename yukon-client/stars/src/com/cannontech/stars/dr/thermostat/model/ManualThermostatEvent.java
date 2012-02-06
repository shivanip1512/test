package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.temperature.Temperature;

public class ManualThermostatEvent extends ThermostatEvent {
    
    private Temperature manualCoolTemp;
    private Temperature manualHeatTemp;
    private ThermostatMode manualMode;
    private ThermostatFanState manualFan;
    private boolean manualHold;

    public Temperature getManualCoolTemp() {
        return manualCoolTemp;
    }
    public void setManualCoolTemp(Temperature manualCoolTemp) {
        this.manualCoolTemp = manualCoolTemp;
    }
    
    public Temperature getManualHeatTemp() {
        return manualHeatTemp;
    }
    public void setManualHeatTemp(Temperature manualHeatTemp) {
        this.manualHeatTemp = manualHeatTemp;
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