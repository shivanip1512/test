package com.cannontech.messaging.util.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.amq.AmqClientConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ConnectionFactory;

public class AmqConnectionFactory implements ConnectionFactory {

    public static final String AMQ_CONNECTION_QUEUE_NAME_PREFIX = "com.cooper.eas.yukon.";

    @Autowired private @Qualifier("Internal_Messaging") javax.jms.ConnectionFactory connectionFactory;
    private final MessageFactory messageFactory;
    private final String queueName;

    public AmqConnectionFactory(MessageFactory messageFactory, String queueName) {
        this.messageFactory = messageFactory;
        this.queueName = queueName;
    }

    @Override
    public Connection createConnection() {

        // Setup the connectionFactoryService with that URL
        AmqConnectionFactoryService connectionSvc = new AmqConnectionFactoryService(connectionFactory);

        // Create the connection and inject the connection service and the message factory.
        AmqClientConnection conn = new AmqClientConnection("", AMQ_CONNECTION_QUEUE_NAME_PREFIX + queueName);
        conn.setConnectionService(connectionSvc);
        conn.setMessageFactory(messageFactory);
        return conn;
    }
}
