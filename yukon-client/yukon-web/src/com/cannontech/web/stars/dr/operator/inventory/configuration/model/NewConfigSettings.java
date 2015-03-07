package com.cannontech.web.stars.dr.operator.inventory.configuration.model;

import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfig;

public class NewConfigSettings {
    
    public NewConfigSettings() {}
    
    private boolean inService;
    private boolean specificRoute;
    private int routeId;
    private HardwareConfig config;
    
    public boolean isInService() {
        return inService;
    }
    public void setInService(boolean inService) {
        this.inService = inService;
    }
    public boolean isSpecificRoute() {
        return specificRoute;
    }
    public void setSpecificRoute(boolean specificRoute) {
        this.specificRoute = specificRoute;
    }
    public int getRouteId() {
        return routeId;
    }
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    public HardwareConfig getConfig() {
        return config;
    }
    public void setConfig(HardwareConfig config) {
        this.config = config;
    }
    
}