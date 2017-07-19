package com.cannontech.amr.demandreset.service;

import java.util.Set;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RfnDemandResetService {
    /**
     * Filter the given devices for devices which can be sent a demand reset using RF.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);

    /**
     * Returns set of devices that can be verified
     */
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> devices);

    /**
     * Send a demand reset to the specified list of devices.  The caller should have filtered this
     * list with {@link #validDevices(Iterable)} first.  Any devices which cannot be reset will be
     * ignored.
     */
    void sendDemandReset(CommandRequestExecution sendExecution, Set<? extends YukonPao> paos,
                         DemandResetCallback callback, LiteYukonUser user);

    /**
     * Send and verify demand reset.  The caller should have filtered this
     * list with {@link #validDevices(Iterable)} first.  Any devices which cannot be reset will be
     * ignored.
     */
    void sendDemandResetAndVerify(CommandRequestExecution sendExecution, CommandRequestExecution verificationExecution,
                                  Set<? extends YukonPao> paos, DemandResetCallback callback, LiteYukonUser user);

    /**
     * Stops listening to point data. It is not possible to cancel Demand Reset for RF devices.
     */
    void cancel(DemandResetResult result);
}
