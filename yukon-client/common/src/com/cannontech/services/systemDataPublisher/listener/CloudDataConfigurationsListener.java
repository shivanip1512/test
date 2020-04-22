package com.cannontech.services.systemDataPublisher.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.services.systemDataPublisher.service.impl.SystemDataServiceInitializer;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CloudDataConfigurationsListener implements MessageListener {
    Logger log = (Logger) LogManager.getLogger(CloudDataConfigurationsListener.class);
    @Autowired private SystemDataServiceInitializer systemDataServiceInitializer;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String textMessage = null;
            try {
                textMessage = ((TextMessage) message).getText();
                ObjectMapper mapper = new ObjectMapper();
                CloudDataConfigurations configurations = mapper.readValue(textMessage, CloudDataConfigurations.class);
                systemDataServiceInitializer.handleCloudConfiguration(configurations.getConfigurations());
            } catch (Exception e) {
                log.error("Error receiving cloud data configuration ", e);
            }
        }
    }
}
