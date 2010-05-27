package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import org.springframework.jms.connection.CachingConnectionFactory;

public class ClientEmbeddedBroker {

    private final String name;
    private final String connectionString;
    
    /**
     * @param name name used for the broker, mostly for debug
     * @param connectionString something like "tcp://10.10.10.10:61616"
     */
    public ClientEmbeddedBroker(String name, String connectionString) {
        this.name = name;
        this.connectionString = connectionString;
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
            String discoveryAddress = "static:(" + connectionString + ")";
            NetworkConnector networkConnector = broker.addNetworkConnector(discoveryAddress);
            networkConnector.setDuplex(true);
            networkConnector.setNetworkTTL(3);
            broker.setUseJmx(true);
            broker.start();
        } catch (Exception e) {
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
