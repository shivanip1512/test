package com.cannontech.message.listener.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.SystemData;
import com.google.gson.Gson;

/**
 * Listen for system data on queue, once received call method to update cache.
 */
@Service
public class SystemDataMessageListener {
    Logger log = (Logger) LogManager.getLogger(SystemDataMessageListener.class);

    @Autowired DataProvider dataProvider;

    @JmsListener(destination = "com.eaton.eas.SystemData", containerFactory = "topicListenerFactory")
    public void receiveMessage(Message message) {
        if (message instanceof TextMessage) {
            String json = null;
            try {
                json = ((TextMessage) message).getText();
                Gson gson = new Gson();
                SystemData data = gson.fromJson(json, SystemData.class);
                log.info("System data received " + json);
                dataProvider.updateSystemInformation(data);
            } catch (JMSException e) {
                log.error("Error receiving system data " + e);
            }
        }
    }
}
