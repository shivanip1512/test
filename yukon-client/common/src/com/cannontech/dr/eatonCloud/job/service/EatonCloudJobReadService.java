package com.cannontech.dr.eatonCloud.job.service;

import com.cannontech.dr.eatonCloud.job.service.impl.EventSummary;

public interface EatonCloudJobReadService {

    /**
     * This method is to setup next read.
     * 
     * After a DR control is initiated for Eaton Cloud LCRs, Yukon will automatically initiate a data read to retrieve the event
     * state of the devices. We will only read devices with result SUCCESS_RECEIVED.
     * 
     * Example of when read occurs after shed:
     * cycle = 30 minutes; so cycle time / 2 = 15 min
     */
    void setupDeviceRead(EventSummary summary);

}
