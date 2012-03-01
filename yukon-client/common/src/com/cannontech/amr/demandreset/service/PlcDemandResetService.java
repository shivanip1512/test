package com.cannontech.amr.demandreset.service;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Predicate;

public interface PlcDemandResetService {
    public Predicate<YukonPao> validDeviceFunction();

    /**
     * Send a demand reset to the specified list of devices.  The caller should have filtered this
     * list with {@link #validDevices(Iterable)} first.  Any devices which cannot be reset will be
     * ignored.
     */
    public void sendDemandReset(Iterable<? extends YukonPao> devices,
                                DemandResetCallback callback, LiteYukonUser user);
}
