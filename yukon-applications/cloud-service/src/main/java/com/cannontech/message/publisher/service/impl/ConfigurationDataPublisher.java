package com.cannontech.message.publisher.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cannontech.message.publisher.service.PublisherBase;
import com.cannontech.message.publisher.service.SupportedDataType;

/**
 * This class will publish a message on queue to get configuration information
 */
@Service
public class ConfigurationDataPublisher extends PublisherBase {
    Logger log = (Logger) LogManager.getLogger(ConfigurationDataPublisher.class);
    @Autowired JmsTemplate jmsTemplate;

    @Override
    public SupportedDataType getSupportedDataType() {
        return SupportedDataType.CONFIGURATION_DATA;
    }

    @Override
    public void publishMessage() {
        log.info("Publish request for Configuration data");
        jmsTemplate.convertAndSend("com.eaton.eas.cloud.ConfigurationSettings", getSupportedDataType().toString());
    }

}
