package com.cannontech.common.device.routeLocate;

import java.util.Random;

import com.cannontech.common.device.model.SimpleDevice;

public class DeviceRouteLocation {
    private final static Random random = new Random();

    private Integer id = null; 
    private SimpleDevice device = null;
    private boolean located = false;
    private boolean routeUpdated = false;
    private Integer routeId = null;
    private String initialRouteName = "";
    private Integer initialRouteId = null;
    private String routeName = "";
    private String deviceName = "";
    
    public DeviceRouteLocation(SimpleDevice device) {
        this.device = device;
        this.id = random.nextInt();
    }

    public SimpleDevice getDevice() {
        return device;
    }

    public void setDevice(SimpleDevice device) {
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
