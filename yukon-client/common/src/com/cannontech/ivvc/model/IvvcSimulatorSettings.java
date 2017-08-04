package com.cannontech.ivvc.model;

import java.io.Serializable;

public class IvvcSimulatorSettings implements Serializable {

    private boolean increasedSpeedMode = false;
    private boolean autogenerateSubstationBuskWh;
    private double substationBuskWh = 3000;

    public IvvcSimulatorSettings() {
    }

    public IvvcSimulatorSettings(boolean increasedSpeedMode, double substationBuskWh,
            boolean autogenerateSubstationBuskWh) {
        this.increasedSpeedMode = increasedSpeedMode;
        this.substationBuskWh = substationBuskWh;
        this.autogenerateSubstationBuskWh = autogenerateSubstationBuskWh;
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
}
