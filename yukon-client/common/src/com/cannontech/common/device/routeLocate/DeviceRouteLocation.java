package com.cannontech.common.device.routeLocate;

import org.apache.commons.lang.math.RandomUtils;

import com.cannontech.common.device.YukonDevice;

public class DeviceRouteLocation {

    Integer id = null; 
    YukonDevice device = null;
    boolean located = false;
    boolean routeUpdated = false;
    Integer routeId = null;
    String initialRouteName = "";
    Integer initialRouteId = null;
    String routeName = "";
    String deviceName = "";
    
    public DeviceRouteLocation(YukonDevice device) {
        this.device = device;
        this.id = RandomUtils.nextInt();
    }

    public YukonDevice getDevice() {
        return device;
    }

    public void setDevice(YukonDevice device) {
        this.device = device;
    }

    public boolean isLocated() {
        return located;
    }

    public void setLocated(boolean located) {
        this.located = located;
    }

    public boolean isRouteUpdated() {
        return routeUpdated;
    }
    
    public void setRouteUpdated(boolean routeUpdated) {
        this.routeUpdated = routeUpdated;
    }
    
    public Integer getId() {
        return id;
    }

    public Integer getInitialRouteId() {
        return initialRouteId;
    }

    public void setInitialRouteId(Integer initialRouteId) {
        this.initialRouteId = initialRouteId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getInitialRouteName() {
        return initialRouteName;
    }

    public void setInitialRouteName(String initialRouteName) {
        this.initialRouteName = initialRouteName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
}
