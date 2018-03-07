package com.cannontech.common.bulk.collection.device.service;

import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CollectionActionCancellationService {
    
    /**
     * Returns true if the action can be canceled by the service
     */
    boolean isCancellable(CollectionAction action);

    /**
     * Attempts to cancel execution.
     */
    void cancel(int key, LiteYukonUser user);
}
