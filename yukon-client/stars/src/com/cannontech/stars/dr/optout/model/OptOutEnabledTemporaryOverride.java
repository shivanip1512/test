package com.cannontech.stars.dr.optout.model;


public class OptOutEnabledTemporaryOverride extends OptOutTemporaryOverride {
    private OptOutEnabled optOutEnabled;
    
    public OptOutEnabled getOptOutEnabled() {
        return optOutEnabled;
    }
    
    public void setOptOutValue(OptOutEnabled optOutEnabled) {
        this.optOutEnabled = optOutEnabled;
    }
}
