package com.cannontech.stars.xml.serialize;

public class MCT {
    private int mctType;
    private boolean hasMctType;
    private int routeID;
    private boolean hasRouteID;
    private String deviceName;
    private int physicalAddress;
    private boolean hasPhysicalAddress;
    private String meterNumber;

    public MCT() {

    }

    public void deleteMctType() {
        this.hasMctType = false;
    }

    public void deletePhysicalAddress() {
        this.hasPhysicalAddress = false;
    }
    
    public void deleteRouteID() {
        this.hasRouteID = false;
    }

    public String getDeviceName() {
        return this.deviceName;
    } 

    public int getMctType() {
        return this.mctType;
    }

    public String getMeterNumber() {
        return this.meterNumber;
    } 

    public int getPhysicalAddress() {
        return this.physicalAddress;
    }

    public int getRouteID() {
        return this.routeID;
    }

    public boolean hasMctType() {
        return this.hasMctType;
    }

    public boolean hasPhysicalAddress() {
        return this.hasPhysicalAddress;
    }

    public boolean hasRouteID() {
        return this.hasRouteID;
    }

    public void setDeviceName(java.lang.String deviceName) {
        this.deviceName = deviceName;
    }

    public void setMctType(int mctType) {
        this.mctType = mctType;
        this.hasMctType = true;
    }

    public void setMeterNumber(java.lang.String meterNumber) {
        this.meterNumber = meterNumber;
    } 

    public void setPhysicalAddress(int physicalAddress) {
        this.physicalAddress = physicalAddress;
        this.hasPhysicalAddress = true;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
        this.hasRouteID = true;
    }

}
