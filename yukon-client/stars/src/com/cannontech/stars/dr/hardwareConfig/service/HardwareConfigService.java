package com.cannontech.stars.dr.hardwareConfig.service;

import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;

public interface HardwareConfigService {
    void disable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws WebClientException;

    void enable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws WebClientException;
}
