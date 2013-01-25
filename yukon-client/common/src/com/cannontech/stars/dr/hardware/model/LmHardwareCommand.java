package com.cannontech.stars.dr.hardware.model;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

/**
 * Class representing the command parameters for a command to send to an LM device.
 * Extends {@link LmCommand} and adds the private field 'device' to target a single piece of hardware.
 */
public class LmHardwareCommand extends LmCommand {
    
    private LiteLmHardwareBase device;
    
    public LiteLmHardwareBase getDevice() {
        return device;
    }

    public void setDevice(LiteLmHardwareBase device) {
        this.device = device;
    }
    
    @Override
    public String toString() {
        return String.format("LmHardwareCommand [device=%s, type=%s, user=%s, params=%s]", device, type, user, params);
    }
}