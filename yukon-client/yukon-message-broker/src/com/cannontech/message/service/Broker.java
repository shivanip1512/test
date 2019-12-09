package com.cannontech.message.service;

import java.io.IOException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.activemq.ActiveMQHelper;
import com.cannontech.common.util.jmx.JmxHelper;

public class Broker {

    private static final long DEFAULT_MEMORY_USAGE_LIMIT = 128 * 1024 * 1024; // 128 MB
    private static final long DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT = 8388608; // 2^23 (8 MB)

    private Logger log = YukonLogManager.getLogger(Broker.class);

    private final ApplicationId applicationId;
    private final String listenerHost;
    private static final String localhostConnector = "tcp://localhost:61616";
    private static final String anyhostConnector = "tcp://0.0.0.0:61616";

    private long memoryUsageLimit = Long.getLong("com.cannontech.services.jms.ServerBroker.memoryUsageLimit",
                                                 DEFAULT_MEMORY_USAGE_LIMIT);

    private long tempQueueMemoryUsageLimit = Long.getLong("com.cannontech.services.jms.ServerBroker.tempqueueMemoryUsageLimit",
                                                          DEFAULT_TEMPQUEUE_MEMORY_USAGE_LIMIT);

    BrokerServiceMonitor monitor = new BrokerServiceMonitor();

    /**
     * @param applicationId Application ID of the broker, passed in by BrokerService.
     * @param listenerHost something like "tcp://localhost:61616"
     */
    public Broker(ApplicationId applicationId, String listenerHost) {
        this.applicationId = applicationId;
        this.listenerHost = listenerHost;
    }

    public void start() {
        try {
            BrokerService broker = new BrokerService();
            broker.setTmpDataDirectory(ActiveMQHelper.getTmpDataDirectory(applicationId));
            broker.getPersistenceAdapter().setDirectory(ActiveMQHelper.getKahaDbDirectory(applicationId));
            broker.setBrokerName(ActiveMQHelper.resolveBrokerName(applicationId));

            broker.setUseJmx(true);
            broker.getManagementContext().setConnectorPort(JmxHelper.getApplicationJmxPort(applicationId));

            broker.addConnector(listenerHost);
            if (!listenerHost.startsWith(anyhostConnector) && !listenerHost.startsWith(localhostConnector)) {
                log.info("Specified listener (" + listenerHost + ") doesn't include localhost:61616, adding localhost to broker.");
                // They didn't specify a listener for localhost:61616, so we
                // will
                try {
                    broker.addConnector(localhostConnector);
                } catch (IOException e) {
                    log.error("Unable to add localhost JMS listener (localhost:61616). The specified host and port in global settings might already be bound to this address. listenerHost: (" + listenerHost + ")",
                              e);
                }
            }

            log.info("memoryUsageLimit = " + memoryUsageLimit);
            log.info("tempQueueMemoryUsageLimit = " + tempQueueMemoryUsageLimit);

            broker.getSystemUsage().getMemoryUsage().setLimit(memoryUsageLimit);
            PolicyEntry policyEntry = new PolicyEntry();
            policyEntry.setMemoryLimit(tempQueueMemoryUsageLimit);
            policyEntry.setProducerFlowControl(true);
            PolicyMap map = new PolicyMap();
            map.put(new ActiveMQTempQueue("*"), policyEntry);
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
