package com.cannontech.messaging.util.impl;

import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.amq.AmqClientConnection;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ConnectionFactory;

public class AmqConnectionFactory extends AmqConnectionFactoryBase implements ConnectionFactory {

    public AmqConnectionFactory(MessageFactory messageFactory, String queueName) {
        super(messageFactory, queueName);
    }

    @Override
    public Connection createConnection() {

        // Create the connection and inject the message factory.
        AmqClientConnection conn = new AmqClientConnection("", getFullQueueName(), getConnectionFactory());
        conn.setMessageFactory(getMessageFactory());

        return conn;
    }
}
