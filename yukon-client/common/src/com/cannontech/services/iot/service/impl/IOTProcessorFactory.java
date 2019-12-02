package com.cannontech.services.iot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.services.iot.processor.IOTProcessor;
import com.cannontech.services.iot.processor.impl.IOTNetworkManagerProcessor;
import com.cannontech.services.iot.processor.impl.IOTYukonProcessor;
import com.cannontech.services.iot.service.IOTPublisher;

/**
 * 
 * Factory Class to return the processor.
 *
 */
@Service
public class IOTProcessorFactory {

    @Autowired private IOTYukonProcessor iotYukonProcessor;
    @Autowired private IOTNetworkManagerProcessor iotNetworkManagerProcessor;

    /**
     * This method will return the processor based on the passed publisher.
     */
    public IOTProcessor createProcessor(IOTPublisher iotPublisher) {
        IOTProcessor processor = null;

        if (iotPublisher == IOTPublisher.YUKON) {
            processor = iotYukonProcessor;
        } else if (iotPublisher == IOTPublisher.NETWORK_MANAGER) {
            processor = iotNetworkManagerProcessor;
        }
        return processor;
    }
}
