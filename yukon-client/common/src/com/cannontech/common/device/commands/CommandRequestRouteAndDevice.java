package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.YukonDevice;

public class CommandRequestRouteAndDevice extends CommandRequestBase {
    
    private YukonDevice device;
    private int routeId;
    
    public YukonDevice getDevice() {
        return device;
    }
    public int getRouteId() {
        return routeId;
    }
    
    public void setDevice(YukonDevice device) {
        this.device = device;
    }
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    

    @Override
    public String toString() {
        
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("device", getDevice());
        tsc.append("routeId", getRouteId());
        tsc.append("command", getCommand());
        tsc.append("backgroundPriority", isBackgroundPriority());
        return tsc.toString();
    }
}
