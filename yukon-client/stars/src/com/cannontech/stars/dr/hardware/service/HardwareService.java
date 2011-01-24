package com.cannontech.stars.dr.hardware.service;

import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;

public interface HardwareService {
	
    /**
     * If delete is true: deletes the hardware, otherwise just removes it from the 
     * account and places it back in general inventory.
     */
    public void deleteHardware(YukonUserContext userContext, boolean delete, int inventoryId, int accountId, YukonEnergyCompany yukonEnergyCompany) throws Exception;
}
