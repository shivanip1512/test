package com.cannontech.dr.rfn.service;

import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;

public interface RawExpressComCommandBuilder {
    
    /**
     * Builds byte[] representing a 'read now' request to an RF ExpressCom device.
     */
    public byte[] getCommand(LmHardwareCommand parameters);
    
}
