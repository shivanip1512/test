package com.cannontech.ivvc.model;

import java.io.Serializable;

public class IvvcSimulatorSettings implements Serializable {

    private boolean increasedSpeedMode = false;

    public IvvcSimulatorSettings() {
    }

    public IvvcSimulatorSettings(boolean increasedSpeedMode) {
        this.increasedSpeedMode = increasedSpeedMode;
    }

    public boolean isIncreasedSpeedMode() {
        return increasedSpeedMode;
    }

    public void setIncreasedSpeedMode(boolean increasedSpeedMode) {
        this.increasedSpeedMode = increasedSpeedMode;
    }
}
