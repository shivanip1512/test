package com.cannontech.message.listener.service;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.data.provider.DataProvider;

/**
 * Listen for system data on queue, once received call method to update cache.
 */
@Service
public class SystemDataMessageListener implements MessageListener {

    @Autowired DataProvider dataProvider;

    public void onMessage(Message message) {
        // Process and update Cache
        String data = "";
        dataProvider.updateSystemInformation(data);
    }
}
