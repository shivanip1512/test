package com.cannontech.amr.monitors.message;

import java.io.Serializable;

public class DeviceDataMonitorStatusResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean isWorkingOnObject;
    private Integer violationCount;

    public DeviceDataMonitorStatusResponse(boolean isWorkingOnObject, Integer violationCount) {
        this.isWorkingOnObject = isWorkingOnObject;
        this.violationCount = violationCount;
    }

    public boolean isWorkingOnObject() {
        return isWorkingOnObject;
    }

    @Override
    public String toString() {
        return "isWorkingOnObject=" + isWorkingOnObject + " violationCount=" + violationCount;
    }
    
    public Integer getViolationCount() {
        return violationCount;
    }

    public void setViolationCount(Integer violationCount) {
        this.violationCount = violationCount;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isWorkingOnObject ? 1231 : 1237);
        result = prime * result + ((violationCount == null) ? 0 : violationCount.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeviceDataMonitorStatusResponse other = (DeviceDataMonitorStatusResponse) obj;
        if (isWorkingOnObject != other.isWorkingOnObject) {
            return false;
        }
        if (violationCount == null) {
            if (other.violationCount != null) {
                return false;
            }
        } else if (!violationCount.equals(other.violationCount)) {
            return false;
        }
        return true;
    }
}
