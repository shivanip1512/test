package com.cannontech.amr.demandreset.service;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PlcDemandResetService {
    public <T extends YukonPao> Iterable<T> validDevices(Iterable<T> paos);
    public <T extends YukonPao> Iterable<T> invalidDevices(Iterable<T> paos);

    /**
     * Send a demand reset to the specified list of devices.  The caller should have filtered this
     * list with {@link #validDevices(Iterable)} first.  Any devices which cannot be reset will be
     * ignored.
     */
    public void sendDemandReset(Iterable<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user);
}
