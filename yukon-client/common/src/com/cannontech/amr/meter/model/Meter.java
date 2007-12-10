package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.YukonDevice;


public class Meter extends YukonDevice { // implements Cloneable{
    private String name;
    private String typeStr;
    
    private boolean disabled;
    
    private String route;
    private int routeId;
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
    
    public int getRouteId() {
        return routeId;
    }
    
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    
/*    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("name", getName());
        tsc.append("deviceId", getDeviceId());
        tsc.append("type", getTypeStr());
        return tsc.toString();
    }
*/
}
