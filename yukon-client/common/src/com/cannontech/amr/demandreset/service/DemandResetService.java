package com.cannontech.amr.demandreset.service;

import java.util.Set;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
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
     * Send a demand reset to the specified list of devices. The caller may filter this
     * list with {@link #validDevices(Iterable)} first. If the user doesn't filter the devices,
     * unsupported devices will be ignored and the entry will be added to CommandRequestUnsupported.
     */
    void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback,
                         LiteYukonUser user);
    /**
     * Returns result for the key provided.
     */
    DemandResetResult getResult(String key);
    
    /**
     * Returns the list of completed and pending results ordered by newest first
     */
    Iterable<DemandResetResult> getResults();

    /**
     * Sends demand reset to the specified list of devices.
     */
    DemandResetResult sendDemandReset(DeviceCollection deviceCollection, SimpleCallback<DemandResetResult> callback,
                                      YukonUserContext userContext);
    
    /**
     * Attempts to cancel the command sent. 
     */
    void cancel(String key, LiteYukonUser user);
}
