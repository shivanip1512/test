package com.cannontech.ivvc.model;

import java.io.Serializable;

public class IvvcSimulatorSettings implements Serializable {

    private boolean increasedSpeedMode = false;
    private boolean autogenerateSubstationBuskWh;
    private double substationBuskWh = 3000;
    private double localVoltageOffsetVar = 1200;
    private double remoteVoltageOffsetVar = 1200;

    public IvvcSimulatorSettings() {
    }

    public IvvcSimulatorSettings(boolean increasedSpeedMode, double substationBuskWh,
            boolean autogenerateSubstationBuskWh, double localVoltageOffsetVar, double remoteVoltageOffsetVar) {
        this.increasedSpeedMode = increasedSpeedMode;
        this.substationBuskWh = substationBuskWh;
        this.autogenerateSubstationBuskWh = autogenerateSubstationBuskWh;
        this.localVoltageOffsetVar = localVoltageOffsetVar;
        this.remoteVoltageOffsetVar = remoteVoltageOffsetVar;
    }

    public boolean isIncreasedSpeedMode() {
        return increasedSpeedMode;
    }

    public void setIncreasedSpeedMode(boolean increasedSpeedMode) {
        this.increasedSpeedMode = increasedSpeedMode;
    }

    public double getSubstationBuskWh() {
        return substationBuskWh;
    }

    public void setSubstationBuskWh(double substationBuskWh) {
        this.substationBuskWh = substationBuskWh;
    }

    public boolean isAutogenerateSubstationBuskWh() {
        return autogenerateSubstationBuskWh;
    }

    public void setAutogenerateSubstationBuskWh(boolean autogenerateSubstationBuskWh) {
        this.autogenerateSubstationBuskWh = autogenerateSubstationBuskWh;
    }

    public double getLocalVoltageOffsetVar() {
        return localVoltageOffsetVar;
    }

    public void setLocalVoltageOffsetVar(double localVoltageOffset) {
        this.localVoltageOffsetVar = localVoltageOffset;
    }

    public double getRemoteVoltageOffsetVar() {
        return remoteVoltageOffsetVar;
    }

    public void setRemoteVoltageOffsetVar(double remoteVoltageOffset) {
        this.remoteVoltageOffsetVar = remoteVoltageOffset;
    }    
}
