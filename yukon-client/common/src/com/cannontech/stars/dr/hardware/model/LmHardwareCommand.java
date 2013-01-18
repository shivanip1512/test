package com.cannontech.stars.dr.hardware.model;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

/**
 * Class representing the command parameters for a command to send to an LM device.  Uses the builder pattern.
 * {@link LmHardwareCommand.Builder} constructor takes the required arguments. Optional arguments are stored
 * as {@link LmHardwareCommandParam} in the params via {@link LmHardwareCommand.Builder#withParam} 
 * and retrieved via {@link LmHardwareCommand#findParam}.
 * 
 * The {@link LmHardwareCommandParam.PRIORITY} param is automatically added to the parameters as a '7'.
 * Adding this parameter via {@link LmHardwareCommand.Builder#withParam} would then override it.
 * 
 * {@link LmHardwareCommandParam} values can only be added if they are the proper class type for that particular
 * parameter, otherwise {@link IllegalArgumentException} is thrown by {@link LmHardwareCommand.Builder#withParam}.
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