package com.cannontech.amr.demandreset.service;

import java.util.Set;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface DemandResetService {
    /**
     * Filter the given devices for devices which can be sent a demand reset.
     */
    <T extends YukonPao> Set<T> filterDevices(Set<T> devices);

    /**
     * Sends and verifies demand reset.
     */
    void sendDemandResetAndVerify(Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user);

    /**
     * Sends demand reset command.
     */
    void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user);

    /**
     * Sends and verifies demand reset.
     */
    int sendDemandResetAndVerify(DeviceCollection deviceCollection,
                                      SimpleCallback<CollectionActionResult> alertCallback, YukonUserContext userContext);    
}
