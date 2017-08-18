package com.cannontech.common.worker;

/**
 * Workers that are supervised by a QueueProcessorSupervisor should implement this interface.
 * Workers must expose start and stop methods for the supervisor to invoke.
 */
public interface SupervisedQueueProcessor {
    
    /**
     * When this method is invoked, the worker should begin processing.
     */
    void start();
    
    /**
     * When this method is invoked, the worker should be flagged to complete its work in progress and shut down.
     * This should be a non-blocking call.
     */
    void stop();
    
}
