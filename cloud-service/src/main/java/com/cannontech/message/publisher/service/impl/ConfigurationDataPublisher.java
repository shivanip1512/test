package com.cannontech.message.publisher.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cannontech.message.model.SupportedDataType;
import com.cannontech.message.publisher.service.PublisherBase;

/**
 * This class will publish a message on queue to get configuration information
 */
@Service
public class ConfigurationDataPublisher extends PublisherBase {
    Logger log = (Logger) LogManager.getLogger(ConfigurationDataPublisher.class);

    @Override
    public SupportedDataType getSupportedDataType() {
        return SupportedDataType.CONFIGURATION_DATA;
    }

    @Override
    public void publishMessage() {
        log.info("Publish Configuration data");
        // prepare request
        // get broker connection
        // publish data
    }

}
