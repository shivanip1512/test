package com.cannontech.web.tools.commander.model;

public class RecentTarget {

    private String target;
    private Integer paoId;
    private Integer routeId;
    private String serialNumber;
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public Integer getPaoId() {
        return paoId;
    }
    
    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }
    
    public Integer getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    @Override
    public String toString() {
        return String.format("RecentTarget [target=%s, paoId=%s, routeId=%s, serialNumber=%s]", target, paoId, routeId, serialNumber);
    }
    
}