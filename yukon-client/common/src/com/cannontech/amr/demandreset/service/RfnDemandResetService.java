package com.cannontech.amr.demandreset.service;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RfnDemandResetService {
    /**
     * Filter the given devices for devices which can be sent a demand reset using RF.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);

    /**
     * Send a demand reset to the specified list of devices.  The caller should have filtered this
     * list with {@link #validDevices(Iterable)} first.  Any devices which cannot be reset will be
     * ignored.
     */
    void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user);
}
