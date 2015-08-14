package com.cannontech.stars.dr.hardware.service;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface HardwareConfigService {

    public static enum Status {
        UNPROCESSED, SUCCESS, FAIL, UNSUPPORTED
    }
    
    /**
     * Attempts to send addressing/In Service/Out of Service commands to the device.
     * Example:
     * 
     * 2 devices selected one enrolled the other one is not.
     * 
     * 1. In Service only checked:
     * Send addressing to enrolled device
     * Send "In Service" to enrolled device
     * 
     * 2. Out of service only checked:
     * Send addressing to enrolled device
     * Send "Out of service" to unenrolled device
     * 
     * 3. In Service and Out of service checked:
     * Send addressing to enrolled device
     * Send "In Service" to enrolled device
     * Send "Out of service" to unenrolled device
     * 
     * @param forceInService - if true send 'In Service' command to enrolled devices
     * @param sendOutOfService - if true send 'Out of Service' command to unenrolled devices
     * 
     * @return SUCCESS or UNSUPPORTED, throws exception if there was an error
     * 
     * @throws CommandCompletionException
     */
    Status config(int inventoryId, boolean forceInService, boolean sendOutOfService, LiteYukonUser user)
            throws CommandCompletionException;

    /**
     * Sends 'Out of Service' command to a device.
     */
    void disable(int inventoryId, int accountId, int energyCompanyId, YukonUserContext userContext)
            throws CommandCompletionException;
    /**
     * Sends 'In Service' command to a device.
     */
    void enable(int inventoryId, int accountId, int energyCompanyId, YukonUserContext userContext)
            throws CommandCompletionException;
        
}
