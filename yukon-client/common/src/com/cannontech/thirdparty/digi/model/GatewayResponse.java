package com.cannontech.thirdparty.digi.model;

import java.util.List;

public class GatewayResponse {

    private String deviceId;
    
    private String errorDescription;
    private Integer errorId = null;
    
    private int eventId;
    private int status;
    private List<String> deviceList;
    
    public boolean hasError() {
        return errorId != null;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getErrorDescription() {
        return errorDescription;
    }
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    public int getErrorId() {
        return errorId;
    }
    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public List<String> getDeviceList() {
        return deviceList;
    }
    public void setDeviceList(List<String> deviceList) {
        this.deviceList = deviceList;
    }
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
