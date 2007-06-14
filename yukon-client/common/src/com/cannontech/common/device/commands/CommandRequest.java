package com.cannontech.common.device.commands;

public class CommandRequest {
    private String command;
    private int deviceId;
    
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

}
