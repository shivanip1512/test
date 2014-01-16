package com.cannontech.messaging.util.impl;

import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.connection.amq.AmqListenerConnection;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ListenerConnectionFactory;

public class AmqConnectionListenerFactory extends AmqConnectionFactoryBase implements ListenerConnectionFactory {

    public AmqConnectionListenerFactory(MessageFactory messageFactory, String queueName) {
        super(messageFactory, queueName);
    }

    @Override
    public ListenerConnection createListenerConnection() {

        // Setup the connectionFactoryService with that URI
        AmqConnectionFactoryService connectionSvc = new AmqConnectionFactoryService(getConnectionFactory());

        // Create the connection and inject the connection service and the message factory.
        AmqListenerConnection conn = new AmqListenerConnection("", getFullQueueName());
        conn.setConnectionService(connectionSvc);
        conn.setMessageFactory(getMessageFactory());
        conn.setAutoReconnect(true);

        return conn;
    }
}
