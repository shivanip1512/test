package com.cannontech.amr.demandreset.service.impl;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.device.StrategyType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Predicate;

public interface DemandResetStrategy {
    /**
     * Each strategy implementation should have a type in the {@link StrategyType} enum.
     * This returns that type for this strategy implementation.
     */
    StrategyType getType();

    /**
     * Return a predicate for use in filtering out invalid devices.  
     */
    Predicate<YukonPao> getValidDeviceFunction();

    /**
     * Send a demand reset to the given devices.
     */
    void sendDemandReset(Iterable<? extends YukonPao> devices, DemandResetCallback callback,
                         LiteYukonUser user);
}
