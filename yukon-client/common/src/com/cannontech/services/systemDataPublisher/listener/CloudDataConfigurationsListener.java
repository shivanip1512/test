package com.cannontech.services.systemDataPublisher.listener;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationsHandlerService;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CloudDataConfigurationsListener implements MessageListener {
    private static final Logger log = (Logger) LogManager.getLogger(CloudDataConfigurationsListener.class);

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private CloudDataConfigurationsHandlerService cloudDataConfigurationsHandlerService;

    private String clientId;

    @PostConstruct
    public void init() {
        try {
            Connection connection = connectionFactory.createConnection();
            clientId = connection.getClientID();
            connection.close();
        } catch (JMSException e) {
            log.error("Error occurred while retrieving clientId", e);
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String textMessage = null;
            try {
                textMessage = ((TextMessage) message).getText();
                ObjectMapper mapper = new ObjectMapper();
                CloudDataConfigurations configurations = mapper.readValue(textMessage, CloudDataConfigurations.class);
                // clientId in CloudDataConfigurations will be set when the message is published from Advisory listener. For
                // normal flow it will be null. So this clientId will be used to differentiate the flow / whether to consume
                // the message or not.
                if (StringUtils.isEmpty(configurations.getClientId()) || configurations.getClientId().equals(clientId)) {
                    cloudDataConfigurationsHandlerService.handleCloudConfiguration(configurations.getConfigurations());
                }
            } catch (Exception e) {
                log.error("Error receiving cloud data configuration ", e);
            }
        }
    }
}
