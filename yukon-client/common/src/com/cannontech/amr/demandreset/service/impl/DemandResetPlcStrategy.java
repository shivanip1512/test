package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.PlcDemandResetService;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DemandResetPlcStrategy implements DemandResetStrategy {
    @Autowired private PlcDemandResetService plcDemandResetService;

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return plcDemandResetService.filterDevices(devices);
    }

    @Override
    public Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandReset(CommandRequestExecution sendExecution, CommandRequestExecution verificationExecution,
                                Set<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user) {
        return plcDemandResetService.sendDemandReset(sendExecution, verificationExecution, devices, callback, user);
    }
    
    @Override
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos){
        return plcDemandResetService.getVerifiableDevices(paos);
    }

    @Override
    public void cancel(Set<CommandCompletionCallback<CommandRequestDevice>> toCancel, LiteYukonUser user) {
        plcDemandResetService.cancel(toCancel, user);
    }

    @Override
    public boolean cancellable() {
        return true;
    }
}
