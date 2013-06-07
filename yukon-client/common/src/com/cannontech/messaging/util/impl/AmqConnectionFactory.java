package com.cannontech.messaging.util.impl;

import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.amq.AmqClientConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.util.ConnectionFactory;

public class AmqConnectionFactory implements ConnectionFactory {

    public static final String AMQ_CONNECTION_QUEUE_NAME_PREFEIX = "com.cooper.eas.yukon.";

    private final MessageFactory messageFactory;
    private final String queueName;

    public AmqConnectionFactory(MessageFactory messageFactory, String queueName) {
        this.messageFactory = messageFactory;
        this.queueName = queueName;
    }

    @Override
    public Connection createConnection(String host, int port) {
        // Construct the broker URL
        String url = "tcp://" + host + ":61616";

        // Setup the connectionFactoryService with that URL
        AmqConnectionFactoryService connectionSvc = new AmqConnectionFactoryService();
        connectionSvc.getConnectionFactory().setBrokerURL(url);

        // Create the connection and inject the connection service and the message factory.
        AmqClientConnection conn = new AmqClientConnection("", AMQ_CONNECTION_QUEUE_NAME_PREFEIX + queueName);
        conn.setConnectionService(connectionSvc);
        conn.setMessageFactory(messageFactory);

        return conn;
    }
}
