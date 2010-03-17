package com.cannontech.services.jms;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

public class YukonJmsConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory {
    
    private CachingConnectionFactory cachingDelegate;
    
    @PostConstruct
    public void initialize() {
        cachingDelegate = new CachingConnectionFactory();
        ActiveMQConnectionFactory realDelegate = new ActiveMQConnectionFactory();
        realDelegate.setBrokerURL("tcp://localhost:61616");
        cachingDelegate.setTargetConnectionFactory(realDelegate);
    }

    @Override
    public Connection createConnection() throws JMSException {
        return cachingDelegate.createConnection();
    }

    @Override
    public Connection createConnection(String userName, String password)
            throws JMSException {
        return cachingDelegate.createConnection(userName, password);
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return cachingDelegate.createQueueConnection();
    }

    @Override
    public QueueConnection createQueueConnection(String userName,
            String password) throws JMSException {
        return cachingDelegate.createQueueConnection(userName, password);
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return cachingDelegate.createTopicConnection();
    }

    @Override
    public TopicConnection createTopicConnection(String userName,
            String password) throws JMSException {
        return cachingDelegate.createTopicConnection(userName, password);
    }

}
