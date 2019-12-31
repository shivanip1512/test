package com.cannontech.message.publisher.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cannontech.message.model.SupportedDataType;
import com.cannontech.message.publisher.service.PublisherBase;

/**
 * This class will publish a message on queue to get configuration settings
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
        boolean requestSendSuccessfully = false;
        int counter = 1;
        while (!requestSendSuccessfully) {
            try {
                if (counter != 1) {
                    Thread.sleep(30000);
                }
                jmsTemplate.convertAndSend("com.eaton.eas.cloud.ConfigurationSettingsRequest", getSupportedDataType().toString());
                requestSendSuccessfully = true;
                log.info("Published request for configuration settings");
            } catch (JmsException e) {
                counter++;
                log.info("Could not send request for configurtion setting, broker may be down. Retry after 30 sec " + e);
            } catch (InterruptedException e) {
                log.error("Could not send request for configurtion setting, broker may be down. Retry after 30 sec " + e);
            }

        }
    }
}
