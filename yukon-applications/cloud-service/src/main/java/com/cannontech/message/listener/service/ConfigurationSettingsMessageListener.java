package com.cannontech.message.listener.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.cannontech.azure.model.AzureServices;
import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.ConfigurationSettings;
import com.google.gson.Gson;

/**
 * Listen for configuration data on queue, once received will call method to update cache.
 */
@Service
public class ConfigurationDataMessageListener {
    Logger log = (Logger) LogManager.getLogger(ConfigurationDataMessageListener.class);
    @Autowired DataProvider dataProvider;

    @JmsListener(destination = "com.eaton.eas.cloud.ConfigurationSettingsResponse")
    public void receiveMessage(Message message) {
        if (message instanceof TextMessage) {
            String json = null;
            try {
                json = ((TextMessage) message).getText();
                log.info("Configuration Settings received " + json);
                Gson gson = new Gson();
                ConfigurationSettings configurationSettings = gson.fromJson(json, ConfigurationSettings.class);
                dataProvider.updateConfigurationInformation(AzureServices.IOT_HUB_SERVICE, configurationSettings);
            } catch (JMSException e) {
                log.error("Error when receiving configuration settings " + e);
            }
        }
    }
}
