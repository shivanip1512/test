package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.RfnDemandResetService;
import com.cannontech.amr.device.StrategyType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DemandResetRfnStrategy implements DemandResetStrategy {
    @Autowired private RfnDemandResetService rfnDemandResetService;

    @Override
    public StrategyType getType() {
        return StrategyType.RF;
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return rfnDemandResetService.filterDevices(devices);
    }

    @Override
    public void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user) {
        rfnDemandResetService.sendDemandReset(devices, callback, user);
    }
}
