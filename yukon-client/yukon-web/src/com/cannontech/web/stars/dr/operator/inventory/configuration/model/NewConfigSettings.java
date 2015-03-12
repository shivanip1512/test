package com.cannontech.web.stars.dr.operator.inventory.configuration.model;

import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfig;

public class NewConfigSettings {
    
    public NewConfigSettings() {}
    
    private boolean inService;
    private boolean specificRoute;
    private boolean batch;
    private Integer routeId;
    private Integer groupId;
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
    
    public boolean isBatch() {
        return batch;
    }
    
    public void setBatch(boolean batch) {
        this.batch = batch;
    }
    
    public Integer getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    
    public Integer getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    
    public HardwareConfig getConfig() {
        return config;
    }
    
    public void setConfig(HardwareConfig config) {
        this.config = config;
    }
    
}