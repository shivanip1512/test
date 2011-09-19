package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;

public class ServerEmbeddedBroker {
	private Logger log = YukonLogManager.getLogger(ServerEmbeddedBroker.class);

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
            
            //@todo remove this line, no longer needed as of AMQ 5.4.2
            broker.setSchedulerSupport(false); // https://issues.apache.org/activemq/browse/AMQ-2935
            broker.start();
        } catch (Exception e) {
        	log.warn("Caught exception starting server broker: " + e.toString());
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
