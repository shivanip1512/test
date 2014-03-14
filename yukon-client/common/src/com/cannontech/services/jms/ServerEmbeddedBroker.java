package com.cannontech.services.jms;

import java.io.IOException;

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
    private static final long DEFAULT_WAIT_FOR_START_MILLIS = 120000; // 2 minutes
    
    private Logger log = YukonLogManager.getLogger(ServerEmbeddedBroker.class);
    
    private final String name;
    private final String listenerHost;
    private static final String localhostConnector = "tcp://localhost:61616";
    private static final String anyhostConnector = "tcp://0.0.0.0:61616";

    private long memoryUsageLimit = DEFAULT_MEMORY_USAGE_LIMIT;
    private long tempQueueMemoryUsageLimit = DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT;
    private long waitForStartMillis = DEFAULT_WAIT_FOR_START_MILLIS;

    // FIXME:
    // YUK-13147 
    // This is a part temporary fix to prevent client connection from starting the vm transport.
    // https://issues.apache.org/jira/browse/AMQ-5086
    private static boolean brokerStarted = false;
    private static final Object brokerStartedLock = new Object();

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

            broker.getSystemUsage().getMemoryUsage().setLimit(memoryUsageLimit);
            PolicyEntry policyEntry= new PolicyEntry();
            policyEntry.setMemoryLimit(tempQueueMemoryUsageLimit);
            policyEntry.setProducerFlowControl(true);
            PolicyMap map = new PolicyMap();
            map.put(new ActiveMQTempQueue("*") , policyEntry);
            broker.setDestinationPolicy(map);

            broker.start();

            // FIXME:
            // YUK-13147 
            // This is a part temporary fix to prevent client connection from starting the vm transport.
            // https://issues.apache.org/jira/browse/AMQ-5086
            synchronized (brokerStartedLock) {
                brokerStarted = true;
                brokerStartedLock.notifyAll();
            }

        } catch (Exception e) {
            log.warn("Caught exception starting server broker", e);
            throw new RuntimeException("Unable to start broker", e);
        }
    }

    // FIXME:
    // YUK-13147
    // This is a part temporary fix to prevent client connection from starting the vm transport.
    // https://issues.apache.org/jira/browse/AMQ-5086
    static public void waitForBrokerToStart(Logger log) throws InterruptedException {
        synchronized (brokerStartedLock) {
            while (!brokerStarted) {
                if (log != null) {
                    log.warn("Waiting for the broker to start.");
                }
                brokerStartedLock.wait();
            }
       }
    }
}
