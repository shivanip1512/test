package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

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
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("command", getCommand());
        return tsc.toString();
    }

}
