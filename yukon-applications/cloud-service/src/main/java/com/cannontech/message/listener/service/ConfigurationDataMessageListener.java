package com.cannontech.message.listener.service;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.data.provider.DataProvider;

/**
 * Listen for configuration data on queue, once received will call method to update cache.
 */
public class ConfigurationDataMessageListener implements MessageListener {

    @Autowired DataProvider dataProvider;

    @Override
    public void onMessage(Message message) {
        // Process and update Cache
        String data = "";
        dataProvider.updateSystemInformation(data);
    }

}
