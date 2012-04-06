package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.PlcDemandResetService;
import com.cannontech.amr.device.StrategyType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DemandResetPlcStrategy implements DemandResetStrategy {
    @Autowired private PlcDemandResetService plcDemandResetService;

    @Override
    public StrategyType getType() {
        return StrategyType.PLC;
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return plcDemandResetService.filterDevices(devices);
    }

    @Override
    public void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user) {
        plcDemandResetService.sendDemandReset(devices, callback, user);
    }
}
