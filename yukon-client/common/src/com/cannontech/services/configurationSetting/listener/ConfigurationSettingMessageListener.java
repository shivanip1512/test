package com.cannontech.services.configurationSetting.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.configurationSettingPublisher.service.ConfigurationSettingPublisherService;
import com.cannontech.configurationSettingPublisher.service.impl.ConfigurationSettingBuilder;
import com.cannontech.services.systemStatus.model.SupportedDataType;

/**
 * Listen for configuration data on queue, once received will push data.
 */
public class ConfigurationSettingMessageListener implements MessageListener {
    Logger log = (Logger) LogManager.getLogger(ConfigurationSettingMessageListener.class);

    @Autowired ConfigurationSettingBuilder configurationDataBuilder;
    @Autowired ConfigurationSettingPublisherService configurationSettingPublisherService ;

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String requestMessage = null;
            try {
                requestMessage = ((TextMessage) message).getText();
                if (requestMessage != null && requestMessage.equals(SupportedDataType.CONFIGURATION_DATA.name())) {
                    log.info("Request for Cloud Configuration Settings is received.");
                    configurationSettingPublisherService.publish(configurationDataBuilder.buildConfigurationSetting());
                }
            } catch (JMSException e) {
                log.error("Error while retrieving text message from request." + e);
            }
    }
}
}
