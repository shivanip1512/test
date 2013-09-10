package com.cannontech.messaging.util.impl;

import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.amq.AmqClientConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ConnectionFactory;

public class AmqConnectionFactory extends AmqConnectionFactoryBase implements ConnectionFactory {

    public AmqConnectionFactory(MessageFactory messageFactory, String queueName) {
        super(messageFactory, queueName);
    }

    @Override
    public Connection createConnection() {

        // Setup the connectionFactoryService with that URL
        AmqConnectionFactoryService connectionSvc = new AmqConnectionFactoryService(getConnectionFactory());

        // Create the connection and inject the connection service and the message factory.
        AmqClientConnection conn = new AmqClientConnection("", getFullQueueName());
        conn.setConnectionService(connectionSvc);
        conn.setMessageFactory(getMessageFactory());

        return conn;
    }
}
