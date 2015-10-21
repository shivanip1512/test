package com.cannontech.services.jms;

import java.io.IOException;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PendingQueueMessageStoragePolicy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.VMPendingQueueMessageStoragePolicy;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.plugin.StatisticsBrokerPlugin;
import org.apache.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;

public class ServerEmbeddedBroker {

    private static final long DEFAULT_MEMORY_USAGE_LIMIT = 128 * 1024 * 1024; // 128 MB
    private static final long DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT = 8388608; // 2^23 (8 MB) 
    private static final long DEFAULT_WAIT_FOR_START_MILLIS = 120000; // 2 minutes
    
    private Logger log = YukonLogManager.getLogger(ServerEmbeddedBroker.class);
    
    private final String name;
    private final String listenerHost;
    private static final String localhostConnector = "tcp://localhost:61616";
    private static final String anyhostConnector = "tcp://0.0.0.0:61616";

    private long memoryUsageLimit = 
            Long.getLong("com.cannontech.services.jms.ServerEmbeddedBroker.memory.usage.limit", 
                         DEFAULT_MEMORY_USAGE_LIMIT);
    
    private long tempQueueMemoryUsageLimit = 
            Long.getLong("com.cannontech.services.jms.ServerEmbeddedBroker.tempqueue.memory.usage.limit", 
                         DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT);
    
    private long waitForStartMillis = 
            Long.getLong("com.cannontech.services.jms.ServerEmbeddedBroker.wait.for.start", 
                         DEFAULT_WAIT_FOR_START_MILLIS);

    BrokerServiceMonitor monitor=new BrokerServiceMonitor();
    /**
     * @param name name used for the broker, mostly for debug
     * @param listenerHost something like "tcp://localhost:61616"
     */
    public ServerEmbeddedBroker(String name, String listenerHost) {
        this.name = name;
        this.listenerHost = listenerHost;
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
        cachingFactory.setTargetConnectionFactory(factory);
        cachingFactory.afterPropertiesSet();
        factory = cachingFactory;
        return factory;
    }

    public void start() {
        try {
            BrokerService broker = new BrokerService();
            broker.setBrokerName(name);

            broker.setUseJmx(true);
            
            new StatisticsBrokerPlugin().installPlugin(broker.getBroker());
            
            broker.addConnector(listenerHost);
            if (!listenerHost.equals(anyhostConnector) && !listenerHost.equals(localhostConnector)) {
                log.info("Specified listener (" + listenerHost + ") doesn't include localhost:61616, adding localhost to broker.");
                // They didn't specify a listener for localhost:61616, so we will
                try {
                     broker.addConnector(localhostConnector);
                } catch (IOException e) {
                    log.error("Unable to add localhost JMS listener (localhost:61616). The specified host and port in global settings might already be bound to this address. listenerHost: (" + listenerHost + ")", e);
                }
            }

            log.info("memoryUsageLimit = "+memoryUsageLimit);
            log.info("tempQueueMemoryUsageLimit = "+tempQueueMemoryUsageLimit);

            broker.getSystemUsage().getMemoryUsage().setLimit(memoryUsageLimit);
            PolicyEntry policyEntry= new PolicyEntry();
            policyEntry.setMemoryLimit(tempQueueMemoryUsageLimit);
            policyEntry.setProducerFlowControl(true);
            
            // Force a vmQueueCursor as per http://activemq.apache.org/producer-flow-control.html
            PendingQueueMessageStoragePolicy pendingQueuePolicy=new VMPendingQueueMessageStoragePolicy();
            policyEntry.setPendingQueuePolicy(pendingQueuePolicy);
            
            PolicyMap map = new PolicyMap();
            map.put(new ActiveMQTempQueue("*") , policyEntry);
            broker.setDestinationPolicy(map);

            broker.start();

            monitor.setBroker(broker.getBroker());
            monitor.start();
            
        } catch (Exception e) {
            log.warn("Caught exception starting server broker", e);
            throw new RuntimeException("Unable to start broker", e);
        }
    }
}
