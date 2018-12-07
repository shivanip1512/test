package com.cannontech.web.dev;

import com.cannontech.dr.nest.model.v3.SchedulabilityError;

public class NestSimulatorConfiguration {
    
    private SchedulabilityError startError;
    private String stopError;
    private String cancelError;

    public SchedulabilityError getStartError() {
        return startError;
    }

    public void setStartError(SchedulabilityError startError) {
        this.startError = startError;
    }

    public String getStopError() {
        return stopError;
    }

    public void setStopError(String stopError) {
        this.stopError = stopError;
    }

    public String getCancelError() {
        return cancelError;
    }

    public void setCancelError(String cancelError) {
        this.cancelError = cancelError;
    }

}
