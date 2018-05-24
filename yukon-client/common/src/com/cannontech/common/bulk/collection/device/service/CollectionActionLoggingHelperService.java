package com.cannontech.common.bulk.collection.device.service;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;

public interface CollectionActionLoggingHelperService {
    /**
     * logs event to event log for collection actions
     * @param result
     * @throws ClassNotFoundException 
     */
    void log(CollectionActionResult result);

}
