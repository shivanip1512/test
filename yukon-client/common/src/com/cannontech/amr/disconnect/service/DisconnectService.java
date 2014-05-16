package com.cannontech.amr.disconnect.service;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.user.YukonUserContext;

public interface DisconnectService {
    
    /**
     * Executes connect, disconnect or arm commands
     */
    DisconnectResult execute(DisconnectCommand command,
                             DeviceCollection deviceCollection,
                             SimpleCallback<DisconnectResult> callback,
                             YukonUserContext userContext);

    /**
     * Returns result for the key provided.
     */
    DisconnectResult getResult(String key);
    
    /**
     * Attempts to cancel the command sent
     */
    void cancel(String key, YukonUserContext userContext, DisconnectCommand command);
    
    /**
     * Returns the list of completed and pending results
     */
    Iterable<DisconnectResult> getResults();
}
