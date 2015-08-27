package com.cannontech.amr.monitors.message;

import java.io.Serializable;

public class DeviceDataMonitorStatusResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean isWorkingOnObject;

    public DeviceDataMonitorStatusResponse(boolean isWorkingOnObject) {
        this.isWorkingOnObject = isWorkingOnObject;
    }

    public boolean isWorkingOnObject() {
        return isWorkingOnObject;
    }

    @Override
    public String toString() {
        return "isWorkingOnObject=" + isWorkingOnObject;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isWorkingOnObject ? 1231 : 1237);
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
        DeviceDataMonitorStatusResponse other = (DeviceDataMonitorStatusResponse) obj;
        if (isWorkingOnObject != other.isWorkingOnObject)
            return false;
        return true;
    }
}
