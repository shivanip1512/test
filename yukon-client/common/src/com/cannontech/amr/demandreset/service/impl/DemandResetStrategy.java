package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.device.StrategyType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResetStrategy {
    /**
     * Each strategy implementation should have a type in the {@link StrategyType} enum.
     * This returns that type for this strategy implementation.
     */
    StrategyType getType();

    /**
     * Filter the given devices for devices which can be sent a demand reset using RF.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);

    /**
     * Send a demand reset to the given devices.
     */
    void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback,
                         LiteYukonUser user);
}
