package com.cannontech.stars.dr.hardware.model;

public class AddByRange {
    int hardwareTypeId;
    String from;
    String to;
    int statusTypeId;
    int voltageTypeId;
    int serviceCompanyId;
    int routeId;
    
    public int getStatusTypeId() {
        return statusTypeId;
    }
    public void setStatusTypeId(int statusTypeId) {
        this.statusTypeId = statusTypeId;
    }
    public int getVoltageTypeId() {
        return voltageTypeId;
    }
    public void setVoltageTypeId(int voltageTypeId) {
        this.voltageTypeId = voltageTypeId;
    }
    public int getServiceCompanyId() {
        return serviceCompanyId;
    }
    public void setServiceCompanyId(int serviceCompanyId) {
        this.serviceCompanyId = serviceCompanyId;
    }
    public int getRouteId() {
        return routeId;
    }
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public int getHardwareTypeId() {
        return hardwareTypeId;
    }
    public void setHardwareTypeId(int hardwareTypeId) {
        this.hardwareTypeId = hardwareTypeId;
    }
}