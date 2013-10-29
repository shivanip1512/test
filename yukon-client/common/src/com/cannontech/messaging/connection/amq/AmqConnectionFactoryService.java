package com.cannontech.messaging.connection.amq;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class AmqConnectionFactoryService {

    private static final int _1MB = 1048576; // 2^20 (1 MB)
    //private static final int _8MB = 8388608; // 2^23>(8 MB)

    private static final String DEFAULT_CONNECTION_URI = "tcp://localhost:61616";

    private static AmqConnectionFactoryService defaultService;

    private ConnectionFactory connectionFactory;

    static {
        defaultService = new AmqConnectionFactoryService();
    }

    public AmqConnectionFactoryService() {
        this(DEFAULT_CONNECTION_URI);
    }

    public AmqConnectionFactoryService(String connectionURI) {
        this(new ActiveMQConnectionFactory(connectionURI));
    }

    public AmqConnectionFactoryService(ConnectionFactory connectionFactory) {
        if(connectionFactory == null) {
            connectionFactory = new ActiveMQConnectionFactory(DEFAULT_CONNECTION_URI);
        }
        this.connectionFactory = connectionFactory;
    }

    public ActiveMQConnection createConnection() throws JMSException {
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.setProducerWindowSize(_1MB);        
        return connection;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;        
    }

    public static AmqConnectionFactoryService getDefaultService() {
        return defaultService;
    }

    public static void setDefaultService(AmqConnectionFactoryService defaultConnectionFactoryService) {
        AmqConnectionFactoryService.defaultService = defaultConnectionFactoryService;
    }
}
