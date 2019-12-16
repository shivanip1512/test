package com.cannontech.message.listener.service;

import javax.jms.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.ConfigurationSettings;

/**
 * Listen for configuration data on queue, once received will call method to update cache.
 */
@Service
public class ConfigurationDataMessageListener {
    Logger log = (Logger) LogManager.getLogger(ConfigurationDataMessageListener.class);

    @Autowired DataProvider dataProvider;

    @JmsListener(destination = "com.eaton.eas.cloud.ConfigurationSettings")
    public void receiveMessage(Message message) {
        if (message instanceof ConfigurationSettings) {
            ConfigurationSettings configurationSettings = (ConfigurationSettings) message;
            log.debug("Received configuration data " + configurationSettings.toString());
            dataProvider.updateConfigurationInformation(configurationSettings);
        }
    }

}
