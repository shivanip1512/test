package com.cannontech.messaging.message.loadcontrol.data;


public class GroupPoint extends DirectGroupBase {
    private int deviceIDUsage;
    private int pointIDUsage;
    private int startControlRawState;

    public int getDeviceIDUsage() {
        return deviceIDUsage;
    }

    public int getPointIDUsage() {
        return pointIDUsage;
    }

    public int getStartControlRawState() {
        return startControlRawState;
    }

    public void setDeviceIDUsage(int newDeviceIDUsage) {
        deviceIDUsage = newDeviceIDUsage;
    }

    public void setPointIDUsage(int newPointIDUsage) {
        pointIDUsage = newPointIDUsage;
    }

    public void setStartControlRawState(int newStartControlRawState) {
        startControlRawState = newStartControlRawState;
    }
}
