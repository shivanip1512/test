package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.RfnDemandResetService;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DemandResetRfnStrategy implements DemandResetStrategy {
    
    @Autowired private RfnDemandResetService rfnDemandResetService;

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return rfnDemandResetService.filterDevices(devices);
    }



    @Override
    public CommandCompletionCallback<CommandRequestDevice> sendDemandReset(CommandRequestExecution initiatedExecution,
                                                                    Set<? extends YukonPao> paos,
                                                                    DemandResetCallback callback, LiteYukonUser user){
        rfnDemandResetService.sendDemandReset(initiatedExecution,  paos, callback, user);
        // Returning null, this strategy does not support cancellations
        return null;
        
    }

    @Override
    public Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandResetAndVerify(CommandRequestExecution initiatedExecution,
                                                                                         CommandRequestExecution verificationExecution,
                                                                                         Set<? extends YukonPao> paos,
                                                                                         DemandResetCallback callback,
                                                                                         LiteYukonUser user) {
        rfnDemandResetService.sendDemandResetAndVerify(initiatedExecution, verificationExecution, paos, callback, user);
        // Returning null, this strategy does not support cancellations
        return null;
    }


    @Override
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos) {
        return rfnDemandResetService.getVerifiableDevices(paos);
    }

    @Override
    public void cancel(Set<CommandCompletionCallback<CommandRequestDevice>> toCancel, LiteYukonUser user) {
        // For RF devices, it is not possible to cancel demand reset request
    }

    @Override
    public boolean cancellable() {
        return false;
    }
}
