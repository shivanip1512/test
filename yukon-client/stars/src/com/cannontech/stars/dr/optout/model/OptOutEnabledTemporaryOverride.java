package com.cannontech.stars.dr.optout.model;


public class OptOutEnabledTemporaryOverride extends OptOutTemporaryOverride {
    private int optOutValue;
    
    public OptOutEnabled getOptOutEnabled() {
        return OptOutEnabled.valueOf(optOutValue);
    }
    
    public void setOptOutValue(int optOutValue) {
        this.optOutValue = optOutValue;
    }
    
}
