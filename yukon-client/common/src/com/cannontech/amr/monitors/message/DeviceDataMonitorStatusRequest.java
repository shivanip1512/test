package com.cannontech.amr.monitors.message;

import java.io.Serializable;

public class DeviceDataMonitorStatusRequest implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private final int monitorId;

    public DeviceDataMonitorStatusRequest(int monitorId) {
        this.monitorId = monitorId;
    }

    @Override
    public String toString() {
        return "Device Data Monitor id=" + getMonitorId();
    }

    public int getMonitorId() {
        return monitorId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + monitorId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceDataMonitorStatusRequest other = (DeviceDataMonitorStatusRequest) obj;
        if (monitorId != other.monitorId)
            return false;
        return true;
    }

}
