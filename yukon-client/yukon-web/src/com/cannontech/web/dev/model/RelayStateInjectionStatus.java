package com.cannontech.web.dev.model;

public enum RelayStateInjectionStatus {
    NOT_RUN("Not run"),
    RUNNING("Running"),
    COMPLETE("Complete"),
    CANCELING("Canceling"),
    CANCELED("Canceled"),
    ;
    
    private String displayString;
    
    private RelayStateInjectionStatus(String displayString) {
        this.displayString = displayString;
    }
    
    public String getDisplayString() {
        return displayString;
    }
}
