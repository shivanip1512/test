package com.cannontech.message.service;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * This class provides Active MQ connection to Yukon Message Broker
 */
public class ActiveMQConnectionProvider {

    public static ActiveMQConnectionFactory getConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(getBrokerConnection());
        try {
            Connection connection = (ActiveMQConnection) factory.createConnection();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return factory;
    }

    public static String getBrokerConnection() {
        // This is hardcoded now, will be read from a conf file.
        return "tcp://localhost:61616";
    }
}
