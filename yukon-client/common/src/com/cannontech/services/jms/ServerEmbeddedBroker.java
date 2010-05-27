package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jms.connection.CachingConnectionFactory;

public class ServerEmbeddedBroker {

    private final String name;
    private final String listenerString;
    
    /**
     * @param name name used for the broker, mostly for debug
     * @param listenerString something like "tcp://localhost:61616"
     */
    public ServerEmbeddedBroker(String name, String listenerString) {
        this.name = name;
        this.listenerString = listenerString;
    }

    /**
     * Should only be called once.
     * @return a pooled ConnectionFactory
     */
    public ConnectionFactory getConnectionFactory() {
        // Create a ConnectionFactory
        ConnectionFactory factory = new ActiveMQConnectionFactory("vm://" + name + "?create=false");
        
        // if using Spring, create a CachingConnectionFactory
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
        cachingFactory.setTargetConnectionFactory(factory);
        cachingFactory.afterPropertiesSet();
        factory = cachingFactory;
        return factory;
    }

    public void start() {
        try {
            BrokerService broker = new BrokerService();
            broker.setBrokerName(name);
            broker.addConnector(listenerString);
            broker.setUseJmx(true);
            broker.start();
        } catch (Exception e) {
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
