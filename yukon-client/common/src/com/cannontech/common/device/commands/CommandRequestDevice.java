package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

/**
 * Command request class for device based commands
 */
public class CommandRequestDevice extends CommandRequestBase {
    private int deviceId;

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
        tsc.append("backgroundPriority", isBackgroundPriority());
        return tsc.toString();
    }
}
