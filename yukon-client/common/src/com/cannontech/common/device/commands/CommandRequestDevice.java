package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.model.SimpleDevice;

/**
 * Command request class for device based commands
 */
public class CommandRequestDevice extends CommandRequestBase {
    private SimpleDevice device;

    public SimpleDevice getDevice() {
        return device;
    }

    public void setDevice(SimpleDevice device) {
        this.device = device;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("device", getDevice());
        tsc.append("command", getCommandCallback());
        return tsc.toString();
    }
}
