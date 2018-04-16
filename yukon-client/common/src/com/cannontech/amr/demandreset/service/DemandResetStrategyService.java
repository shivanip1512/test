package com.cannontech.amr.demandreset.service;

import java.util.Set;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.StrategyService;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResetStrategyService extends StrategyService{

    /**
     * Filter the given devices for devices which can be sent a demand reset using RF.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);
    
    /**
     * Send a demand reset to the given devices.
     * @return Callback needed for cancellation.
     */
    void sendDemandReset(CommandRequestExecution initiatedExecution, Set<? extends YukonPao> paos,
            DemandResetCallback callback, LiteYukonUser user);

    /**
     * Send and verify demand reset.
     */
    void sendDemandResetAndVerify(CommandRequestExecution initiatedExecution,
            CommandRequestExecution verificationExecution, Set<? extends YukonPao> paos, DemandResetCallback callback,
            LiteYukonUser user);

    /**
     * Returns devices that is possible to verify
     */
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos);

    /**
     * Attempt to cancel the commands
     */
    default void cancel(CollectionActionResult result, LiteYukonUser user) {
        return;
    }
   
}
