package com.cannontech.ivvc.model;

import java.io.Serializable;

public class IvvcSimulatorStatus implements Serializable {

    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
