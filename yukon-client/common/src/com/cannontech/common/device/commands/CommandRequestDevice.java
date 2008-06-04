package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.YukonDevice;

/**
 * Command request class for device based commands
 */
public class CommandRequestDevice extends CommandRequestBase {
    private YukonDevice device;

    public YukonDevice getDevice() {
        return device;
    }

    public void setDevice(YukonDevice device) {
        this.device = device;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("device", getDevice());
        tsc.append("command", getCommand());
        tsc.append("backgroundPriority", isBackgroundPriority());
        return tsc.toString();
    }
}
