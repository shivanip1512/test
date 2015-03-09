package com.cannontech.stars.dr.hardwareConfig;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.user.YukonUserContext;

public interface HardwareConfigService {
    public void disable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws CommandCompletionException;

    public void enable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws CommandCompletionException;
}
