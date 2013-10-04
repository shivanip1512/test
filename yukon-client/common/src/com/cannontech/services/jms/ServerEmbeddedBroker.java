package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;

public class ServerEmbeddedBroker {

    private static final long DEFAULT_MEMORY_USAGE_LIMIT = 67108864; // 2^26 (64 MB) 
    private static final long DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT = 8388608; // 2^23 (8 MB) 
    
    private Logger log = YukonLogManager.getLogger(ServerEmbeddedBroker.class);
	
    private final String name;
    private final String listenerString;
    private static final String DEFAULT_CONNECTOR = "tcp://localhost:61616"; 
    
    private long memoryUsageLimit = DEFAULT_MEMORY_USAGE_LIMIT;
    private long tempQueueMemoryUsageLimit = DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT;
    
    
    
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
            broker.addConnector(DEFAULT_CONNECTOR);  //  always listen on localhost
            if (!listenerString.equals(DEFAULT_CONNECTOR)) {
                broker.addConnector(listenerString);
            }
            broker.setUseJmx(true);
            
            broker.getSystemUsage().getMemoryUsage().setLimit(memoryUsageLimit);            
            PolicyEntry policyEntry= new PolicyEntry(); 
            policyEntry.setMemoryLimit(tempQueueMemoryUsageLimit);
            policyEntry.setProducerFlowControl(true);
            PolicyMap map = new PolicyMap();
            map.put(new ActiveMQTempQueue("*") , policyEntry);
            broker.setDestinationPolicy(map);
            
            broker.start();
        } catch (Exception e) {
        	log.warn("Caught exception starting server broker: " + e.toString());
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
