package com.cannontech.stars.dr.hardware.service;

import java.sql.SQLException;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;

public interface HardwareService {
	
    /**
     * If delete is true: deletes the hardware, otherwise just removes it from the 
     * account and places it back in general inventory.
     */
    public void deleteHardware(YukonUserContext userContext, boolean delete, int inventoryId, int accountId) 
    throws NotFoundException, CommandCompletionException, SQLException, PersistenceException, WebClientException;
}