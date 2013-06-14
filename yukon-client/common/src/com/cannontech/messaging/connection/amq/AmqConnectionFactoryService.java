package com.cannontech.messaging.connection.amq;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class AmqConnectionFactoryService {

    private static final String DEFAULT_CONNECTION_URI = "tcp://localhost:61616";
    private static final String DEFAULT_CONNECTION_STING = "failover:(" + DEFAULT_CONNECTION_URI + ")" +
                                                           "?useExponentialBackOff=true" +
                                                           "&initialReconnectDelay=1000" + 
                                                           "&maxReconnectDelay=30000" +
                                                           "&startupMaxReconnectAttempts=120" +
                                                           "&maxReconnectAttempts=0";

    private static AmqConnectionFactoryService defaultService;

    private ActiveMQConnectionFactory connectionFactory;

    static {
        defaultService = new AmqConnectionFactoryService();
    }

    public AmqConnectionFactoryService() {
        this(new ActiveMQConnectionFactory(DEFAULT_CONNECTION_STING));
    }

    public AmqConnectionFactoryService(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public QueueConnection createQueueConnection() throws JMSException {

        return connectionFactory.createQueueConnection();
    }

    public TopicConnection createTopicConnection() throws JMSException {
        return connectionFactory.createTopicConnection();
    }

    public ActiveMQConnection createConnection() throws JMSException {
        return (ActiveMQConnection) connectionFactory.createConnection();
    }

    public ActiveMQConnectionFactory getConnectionFactory() {
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
