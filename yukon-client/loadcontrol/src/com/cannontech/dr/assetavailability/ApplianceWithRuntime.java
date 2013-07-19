package com.cannontech.dr.assetavailability;

import com.cannontech.stars.dr.appliance.model.Appliance;

public class ApplianceWithRuntime {
    private final Appliance appliance;
    private final ApplianceRuntime runtime;
    
    public ApplianceWithRuntime(Appliance appliance, ApplianceRuntime runtime) {
        this.appliance = appliance; //TODO copy
        this.runtime = runtime;
    }
    
    public Appliance getAppliance() {
        return appliance; //TODO copy
    }
    
    public ApplianceRuntime getRuntime() {
        return runtime;
    }
}
