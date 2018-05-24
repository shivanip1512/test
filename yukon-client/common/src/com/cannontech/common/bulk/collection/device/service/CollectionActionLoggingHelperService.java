package com.cannontech.common.bulk.collection.device.service;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;

public interface CollectionActionLoggingHelperService {
    /**
     * logs event to event log for collection actions initiating
     * @param result
     * @throws ClassNotFoundException 
     */
    void logActionInitiated(CollectionActionResult result) throws ClassNotFoundException;

    /**
     * logs event to event log for collection actions completed or canceled
     * @param result
     * @throws ClassNotFoundException 
     */
    void logActionCompletedCanceled(CollectionActionResult result) throws ClassNotFoundException;

}
