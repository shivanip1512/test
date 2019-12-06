package com.cannontech.services.systemDataPublisher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.processor.impl.NetworkManagerDataProcessor;
import com.cannontech.services.systemDataPublisher.processor.impl.YukonDataProcessor;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;

/**
 * 
 * Factory Class to return the processor.
 *
 */
@Service
public class SystemDataProcessorFactory {

    @Autowired private YukonDataProcessor yukonDataProcessor;
    @Autowired private NetworkManagerDataProcessor networkManagerDataProcessor;

    /**
     * This method will return the processor based on the passed publisher.
     */
    public SystemDataProcessor createProcessor(SystemDataPublisher iotPublisher) {
        SystemDataProcessor processor = null;

        if (iotPublisher == SystemDataPublisher.YUKON) {
            processor = yukonDataProcessor;
        } else if (iotPublisher == SystemDataPublisher.NETWORK_MANAGER) {
            processor = networkManagerDataProcessor;
        }
        return processor;
    }
}
