package com.cannontech.message.model;

import java.io.Serializable;

public class RfnDeviceDescendantCountData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String deviceName;
    private long serialNumber;
    private long descendantCount;
    private String deviceType;

    public RfnDeviceDescendantCountData(String deviceName, long serialNumber, long descendantCount, String deviceType) {
        this.deviceName = deviceName;
        this.serialNumber = serialNumber;
        this.descendantCount = descendantCount;
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public long getDescendantCount() {
        return descendantCount;
    }
    public void setDescendantCount(long descendantCount) {
        this.descendantCount = descendantCount;
    }

    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (descendantCount ^ (descendantCount >>> 32));
        result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
        result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
        result = prime * result + (int) (serialNumber ^ (serialNumber >>> 32));
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
        RfnDeviceDescendantCountData other = (RfnDeviceDescendantCountData) obj;
        if (descendantCount != other.descendantCount)
            return false;
        if (deviceName == null) {
            if (other.deviceName != null)
                return false;
        } else if (!deviceName.equals(other.deviceName))
            return false;
        if (deviceType == null) {
            if (other.deviceType != null)
                return false;
        } else if (!deviceType.equals(other.deviceType))
            return false;
        if (serialNumber != other.serialNumber)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnDeviceDescedantCountData [deviceName=" + deviceName + ", serialNumber=" + serialNumber
                + ", descendantCount=" + descendantCount + ", deviceType=" + deviceType + "]";
    }
}
