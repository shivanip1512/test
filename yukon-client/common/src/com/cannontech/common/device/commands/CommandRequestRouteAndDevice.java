package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.model.SimpleDevice;

public class CommandRequestRouteAndDevice extends CommandRequestBase {
    
    private SimpleDevice device;
    private int routeId;
    
    public SimpleDevice getDevice() {
        return device;
    }
    public int getRouteId() {
        return routeId;
    }
    
    public void setDevice(SimpleDevice device) {
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
        tsc.append("command", getCommandCallback());
        return tsc.toString();
    }
}
