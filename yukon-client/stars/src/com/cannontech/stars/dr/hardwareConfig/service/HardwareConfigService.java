package com.cannontech.stars.dr.hardwareConfig.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;

public interface HardwareConfigService {
    public void disable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws WebClientException;

    public void enable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws WebClientException;

    /**
     * Get a list of commands which would need to be sent to the device to properly configure it.
     * @throws WebClientException 
     */
    public List<String> getConfigCommands(int inventoryId, int energyCompanyId,
            boolean includeInService, LiteYukonUser user) throws WebClientException;
}
