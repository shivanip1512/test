package com.cannontech.services.rfn;

public interface RfnArchiveProcessor {
    
    /**
     * Process object received from NM
     * @param obj - object to process
     * @param processor - Thread information that is processing the request used for debugging
     */
    void process(Object obj, String processor);
}
