package com.cannontech.messaging.util.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.connection.amq.AmqListenerConnection;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ListenerConnectionFactory;

public class AmqConnectionListenerFactory implements ListenerConnectionFactory {

    @Autowired private @Qualifier("Internal_Messaging") javax.jms.ConnectionFactory connectionFactory;
    private final MessageFactory messageFactory;
    private final String queueName;

    public AmqConnectionListenerFactory(MessageFactory messageFactory, String queueName) {
        this.messageFactory = messageFactory;
        this.queueName = queueName;
    }

    @Override
    public ListenerConnection createListenerConnection() {

        // Setup the connectionFactoryService with that URI
        AmqConnectionFactoryService connectionSvc = new AmqConnectionFactoryService(connectionFactory);

        // Create the connection and inject the connection service and the message factory.
        AmqListenerConnection conn =
            new AmqListenerConnection("", AmqConnectionFactory.AMQ_CONNECTION_QUEUE_NAME_PREFIX + queueName);
        conn.setConnectionService(connectionSvc);
        conn.setMessageFactory(messageFactory);

        return conn;
    }
}
