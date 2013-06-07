package com.cannontech.messaging.util.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.connection.amq.AmqListenerConnection;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ListenerConnectionFactory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class AmqConnectionListenerFactory implements ListenerConnectionFactory {

    @Autowired
    private GlobalSettingDao globalSettingDao;

    private final MessageFactory messageFactory;
    private final String queueName;

    public AmqConnectionListenerFactory(MessageFactory messageFactory, String queueName) {
        this.messageFactory = messageFactory;
        this.queueName = queueName;
    }

    @Override
    public ListenerConnection createListenerConnection(int port) {
        String bindAddress = globalSettingDao.getString(GlobalSettingType.NOTIFICATION_HOST);
        String url = "tcp://" + bindAddress + ":61616";

        // Setup the connectionFactoryService with that URL
        AmqConnectionFactoryService connectionSvc = new AmqConnectionFactoryService();
        connectionSvc.getConnectionFactory().setBrokerURL(url);

        // Create the connection and inject the connection service and the message factory.
        AmqListenerConnection conn =
            new AmqListenerConnection("", AmqConnectionFactory.AMQ_CONNECTION_QUEUE_NAME_PREFEIX + queueName);
        conn.setConnectionService(connectionSvc);
        conn.setMessageFactory(messageFactory);

        return conn;
    }
}
