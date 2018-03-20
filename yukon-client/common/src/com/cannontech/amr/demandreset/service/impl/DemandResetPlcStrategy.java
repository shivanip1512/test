package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.PlcDemandResetService;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
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
    public CommandCompletionCallback<CommandRequestDevice> sendDemandReset(CommandRequestExecution initiatedExecution,
                                                                           Set<? extends YukonPao> paos,
                                                                           DemandResetCallback callback,
                                                                           LiteYukonUser user) {
        return plcDemandResetService.sendDemandReset(initiatedExecution, paos, callback, user);
    }

    @Override
    public Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandResetAndVerify(CommandRequestExecution initiatedExecution,
                                                                                         CommandRequestExecution verificationExecution,
                                                                                         Set<? extends YukonPao> paos,
                                                                                         DemandResetCallback callback,
                                                                                         LiteYukonUser user) {
        return plcDemandResetService.sendDemandResetAndVerify(initiatedExecution,
                                                              verificationExecution,
                                                              paos,
                                                              callback,
                                                              user);
    }
    
    @Override
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos){
        return plcDemandResetService.getVerifiableDevices(paos);
    }

    @Override
    public void cancel(DemandResetResult result, LiteYukonUser user) {
        plcDemandResetService.cancel(result.getCommandCompletionCallbacks(), user);
    }

    @Override
    public StrategyType getStrategy() {
        return StrategyType.PLC;
    }
}
