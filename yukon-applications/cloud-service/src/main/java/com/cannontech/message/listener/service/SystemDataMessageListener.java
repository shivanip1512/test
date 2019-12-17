package com.cannontech.message.listener.service;

import javax.jms.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.SystemData;

/**
 * Listen for system data on queue, once received call method to update cache.
 */
@Service
public class SystemDataMessageListener {
    Logger log = (Logger) LogManager.getLogger(SystemDataMessageListener.class);

    @Autowired DataProvider dataProvider;

    @JmsListener(destination = "com.eaton.eas.SystemData", containerFactory = "topicListenerFactory")
    public void receiveMessage(final Message message) {
        if (message instanceof SystemData) {
            SystemData systemData = (SystemData) message;
            log.debug("Received system data " + systemData.toString());
            dataProvider.updateSystemInformation(systemData);
        }
    }
}
