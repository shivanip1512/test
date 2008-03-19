package com.cannontech.capcontrol;

public class OrphanCBC {
    Integer pointId = -1;
    String pointName = "";
    String deviceName = "";
    
    public OrphanCBC(String deviceName, Integer pointId, String pointName) {
        this.pointId = pointId;
        this.pointName = pointName;
        this.deviceName = deviceName;
    }
    
    public Integer getPointId() {
        return pointId;
    }
    
    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }
    
    public void setPointName(String pointName) {
        this.pointName = pointName;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
}
