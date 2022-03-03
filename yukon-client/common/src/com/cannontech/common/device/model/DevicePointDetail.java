package com.cannontech.common.device.model;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;

public class DevicePointDetail {

    private int pointId;
    private String pointName;
    private String deviceName;
    private int stateGroupId;
    private PaoPointIdentifier paoPointIdentifier;

    private String format = "SHORT";

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

    public void setPaoPointIdentifier(PaoPointIdentifier paoPointIdentifier) {
        this.paoPointIdentifier = paoPointIdentifier;
    }

    public int getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(int stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((paoPointIdentifier == null) ? 0 : paoPointIdentifier.hashCode());
        result = prime * result + pointId;
        result = prime * result + ((pointName == null) ? 0 : pointName.hashCode());
        result = prime * result + stateGroupId;
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
        DevicePointDetail other = (DevicePointDetail) obj;
        if (deviceName == null) {
            if (other.deviceName != null)
                return false;
        } else if (!deviceName.equals(other.deviceName))
            return false;
        if (format == null) {
            if (other.format != null)
                return false;
        } else if (!format.equals(other.format))
            return false;
        if (paoPointIdentifier == null) {
            if (other.paoPointIdentifier != null)
                return false;
        } else if (!paoPointIdentifier.equals(other.paoPointIdentifier))
            return false;
        if (pointId != other.pointId)
            return false;
        if (pointName == null) {
            if (other.pointName != null)
                return false;
        } else if (!pointName.equals(other.pointName))
            return false;
        if (stateGroupId != other.stateGroupId)
            return false;
        return true;
    }

}
