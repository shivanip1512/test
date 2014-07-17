package com.cannontech.amr.demandreset.service.impl;

import java.util.Set;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResetStrategy {
    /**
     * Filter the given devices for devices which can be sent a demand reset using RF.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);

    /**
     * Send a demand reset to the given devices.
     * @return Callbacks needed for cancellation.
     */
    Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandReset(CommandRequestExecution sendExecution, CommandRequestExecution verificationExecution,
                         Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user);
    
    
    /**
     * Returns devices that is possible to verify
     */
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos);

    /**
     * Attempts to cancel the command
     */
    void cancel(Set<CommandCompletionCallback<CommandRequestDevice>> toCancel, LiteYukonUser user);
    
    /**
     * Returns true is the strategy supports command cancellations
     */
    boolean cancellable();
}
