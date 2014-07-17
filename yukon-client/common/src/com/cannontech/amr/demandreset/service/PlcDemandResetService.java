package com.cannontech.amr.demandreset.service;

import java.util.Set;

import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PlcDemandResetService {
    
    /**
     * Filter the given devices for devices which can be sent a demand reset.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);

    /**
     * Send a demand reset to the specified list of devices.  The caller should have filtered this
     * list with {@link #validDevices(Iterable)} first.  Any devices which cannot be reset will be
     * ignored.
     * @return CommandCompletionCallbacks needed for cancellation
     */
    public Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandReset(CommandRequestExecution sendExecution, CommandRequestExecution verificationExecution,
                                Set<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user);

    /**
     * Returns set of devices that can be verified
     */
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos);

    /**
     * Send cancel command to the given devices.
     */
    void cancel(Set<CommandCompletionCallback<CommandRequestDevice>> toCancel, LiteYukonUser user);
}
