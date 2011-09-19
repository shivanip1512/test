package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import org.apache.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;

public class ClientEmbeddedBroker {
	private Logger log = YukonLogManager.getLogger(ClientEmbeddedBroker.class);

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
            
            //connect to the service manager broker
            NetworkConnector networkConnector = broker.addNetworkConnector(discoveryAddress);
            networkConnector.setDuplex(true);
            
            //our largest hop is 3 eg. network manager -> service manager -> web
            networkConnector.setNetworkTTL(3);
            broker.setUseJmx(true);
            
          //@todo remove this line, no longer needed as of AMQ 5.4.2
            broker.setSchedulerSupport(false);// https://issues.apache.org/activemq/browse/AMQ-2935
            broker.start();
        } catch (Exception e) {
        	log.warn("Caught exception starting client broker: " + e.toString());
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
