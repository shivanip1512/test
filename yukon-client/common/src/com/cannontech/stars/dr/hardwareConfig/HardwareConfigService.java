package com.cannontech.stars.dr.hardwareConfig;

import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.user.YukonUserContext;

public interface HardwareConfigService {
    public void disable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws CommandCompletionException;

    public void enable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws CommandCompletionException;

    /**
     * Get a list of commands which would need to be sent to the device to properly configure it.
     */
    public List<String> getConfigCommands(int inventoryId, int energyCompanyId,
            boolean includeInService);
}
