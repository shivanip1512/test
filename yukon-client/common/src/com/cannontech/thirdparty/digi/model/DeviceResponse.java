package com.cannontech.thirdparty.digi.model;

import java.util.List;

public class DeviceResponse {

    String deviceId;
    
    String errorDescription;
    Integer errorId = null;
    
    int status;
    List<String> deviceList;
    
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
  
    public boolean hasError() {
        return errorId != null;
    }
}
