package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import org.apache.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;

public class ClientEmbeddedBroker {
	
    private static final long DEFAULT_WAIT_FOR_START_MILLIS = 120000; // 2 minutes
    
    private Logger log = YukonLogManager.getLogger(ClientEmbeddedBroker.class);

    private final String name;
    private final String connectionString;
    
    private long waitForStartMillis = DEFAULT_WAIT_FOR_START_MILLIS;
    
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
        ConnectionFactory factory = new ActiveMQConnectionFactory("vm://" + name + "?create=false&waitForStart=" + waitForStartMillis);
        
        // if using Spring, create a CachingConnectionFactory
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
        cachingFactory.setCacheConsumers(false);
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
            
            //our largest hop is currently 5:
            //  network manager -> remote proxy -> local proxy -> service manager -> web
            networkConnector.setNetworkTTL(5);
            broker.setUseJmx(true);
            broker.getManagementContext().setConnectorPort(1098);
            
            broker.start();
        } catch (Exception e) {
        	log.warn("Caught exception starting client broker: " + e.toString());
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
