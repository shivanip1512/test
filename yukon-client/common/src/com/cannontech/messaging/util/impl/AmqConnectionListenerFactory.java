package com.cannontech.messaging.util.impl;

import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.amq.AmqListenerConnection;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ListenerConnectionFactory;

public class AmqConnectionListenerFactory extends AmqConnectionFactoryBase implements ListenerConnectionFactory {

    public AmqConnectionListenerFactory(MessageFactory messageFactory, String queueName) {
        super(messageFactory, queueName);
    }

    @Override
    public ListenerConnection createListenerConnection() {

        // Create the connection and inject the message factory.
        AmqListenerConnection conn = new AmqListenerConnection("", getFullQueueName(), getConnectionFactory());
        conn.setMessageFactory(getMessageFactory());
        conn.setAutoReconnect(true);

        return conn;
    }
}
