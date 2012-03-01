package com.cannontech.amr.demandreset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.PlcDemandResetService;
import com.cannontech.amr.device.StrategyType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Predicate;

public class DemandResetPlcStrategy implements DemandResetStrategy {
    @Autowired private PlcDemandResetService plcDemandResetService;

    @Override
    public StrategyType getType() {
        return StrategyType.PLC;
    }

    @Override
    public Predicate<YukonPao> getValidDeviceFunction() {
        return plcDemandResetService.validDeviceFunction();
    }

    @Override
    public void sendDemandReset(Iterable<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user) {
        plcDemandResetService.sendDemandReset(devices, callback, user);
    }
}
