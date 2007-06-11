package com.cannontech.amr.meter.model;


public class Meter {
    private int deviceId;
    private String name;
    private int type;
    private String typeStr;
    
    private boolean disabled;
    
    private String route;
    private String address;
    
    private String meterNumber;

    /**
     * @return the address or "" if none is assigned
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the meter number or "" if none is assigned
     */
    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public String getTypeStr() {
        return typeStr;
    }
    
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    
    /**
     * @return the route name or "" if no route is assigned
     */
    public String getRoute() {
        return route;
    }
    
    public void setRoute(String route) {
        this.route = route;
    }

}
