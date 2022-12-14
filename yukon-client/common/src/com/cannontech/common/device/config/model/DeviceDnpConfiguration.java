package com.cannontech.common.device.config.model;

public class DeviceDnpConfiguration {
    private DeviceConfiguration deviceConfiguration;
    private String timeOffsetValue;
    private String heartbeatModeValue;

    public DeviceConfiguration getDeviceConfiguration() {
        return deviceConfiguration;
    }

    public void setDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
        this.deviceConfiguration = deviceConfiguration;
    }

    public String getTimeOffsetValue() {
        return timeOffsetValue;
    }

    public void setTimeOffsetValue(String timeOffsetValue) {
        this.timeOffsetValue = timeOffsetValue;
    }

    public String getHeartbeatModeValue() {
        return heartbeatModeValue;
    }

    public void setHeartbeatModeValue(String heartbeatModeValue) {
        this.heartbeatModeValue = heartbeatModeValue;
    }

}
