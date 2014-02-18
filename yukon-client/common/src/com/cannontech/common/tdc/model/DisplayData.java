package com.cannontech.common.tdc.model;

import org.joda.time.Instant;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.point.PointType;

public class DisplayData {
    private int pointId;
    private int deviceId;
    private SimpleDevice device;
    private String pointName;
    private String deviceName;
    private PointType pointType;
    private String deviceCurrentState;
    private Instant date;
    private String userName;
    private String additionalInfo;
    private String description;
    private String tagName;
    private String textMessage;
    private int condition;
    private boolean pointEnabled;
    private Cog cog;

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getDeviceCurrentState() {
        return deviceCurrentState;
    }

    public void setDeviceCurrentState(String deviceCurrentState) {
        this.deviceCurrentState = deviceCurrentState;
    }

    public boolean isPointEnabled() {
        return pointEnabled;
    }

    public void setPointEnabled(boolean pointEnabled) {
        this.pointEnabled = pointEnabled;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public Cog getCog() {
        return cog;
    }

    public void setCog(Cog cog) {
        this.cog = cog;
    }

    public SimpleDevice getDevice() {
        return device;
    }

    public void setDevice(SimpleDevice device) {
        this.device = device;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public boolean isBlank(){
        return pointId == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((additionalInfo == null) ? 0 : additionalInfo.hashCode());
        result = prime * result + ((cog == null) ? 0 : cog.hashCode());
        result = prime * result + condition;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result =
            prime * result + ((deviceCurrentState == null) ? 0 : deviceCurrentState.hashCode());
        result = prime * result + deviceId;
        result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
        result = prime * result + (pointEnabled ? 1231 : 1237);
        result = prime * result + pointId;
        result = prime * result + ((pointName == null) ? 0 : pointName.hashCode());
        result = prime * result + ((pointType == null) ? 0 : pointType.hashCode());
        result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
        result = prime * result + ((textMessage == null) ? 0 : textMessage.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
        DisplayData other = (DisplayData) obj;
        if (additionalInfo == null) {
            if (other.additionalInfo != null)
                return false;
        } else if (!additionalInfo.equals(other.additionalInfo))
            return false;
        if (cog == null) {
            if (other.cog != null)
                return false;
        } else if (!cog.equals(other.cog))
            return false;
        if (condition != other.condition)
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (device == null) {
            if (other.device != null)
                return false;
        } else if (!device.equals(other.device))
            return false;
        if (deviceCurrentState == null) {
            if (other.deviceCurrentState != null)
                return false;
        } else if (!deviceCurrentState.equals(other.deviceCurrentState))
            return false;
        if (deviceId != other.deviceId)
            return false;
        if (deviceName == null) {
            if (other.deviceName != null)
                return false;
        } else if (!deviceName.equals(other.deviceName))
            return false;
        if (pointEnabled != other.pointEnabled)
            return false;
        if (pointId != other.pointId)
            return false;
        if (pointName == null) {
            if (other.pointName != null)
                return false;
        } else if (!pointName.equals(other.pointName))
            return false;
        if (pointType != other.pointType)
            return false;
        if (tagName == null) {
            if (other.tagName != null)
                return false;
        } else if (!tagName.equals(other.tagName))
            return false;
        if (textMessage == null) {
            if (other.textMessage != null)
                return false;
        } else if (!textMessage.equals(other.textMessage))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }
}
