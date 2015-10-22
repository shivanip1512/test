package com.cannontech.services.jms;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.log4j.Logger;
import org.joda.time.Duration;

import com.cannontech.clientutils.YukonLogManager;

public class BrokerServiceMonitor extends Thread {

    private Logger log = YukonLogManager.getLogger(BrokerServiceMonitor.class);

    private Broker broker;
    private BrokerService brokerService;
    
    /** Time period for checking on the queue depths from the Java System Properties */
    private Duration reportingPeriod = Duration.standardSeconds(
            Integer.getInteger("com.cannontech.services.jms.BrokerServiceMonitor.reportingPeriodSeconds", 
                         5 /* minutes */ * 60 /* Seconds/Minute */));
    
    @Override
    public void run() {
        while (true)
        {
            try {
                for (ActiveMQDestination aMQdest : broker.getDestinations())
                {
                    Destination dest = brokerService.getDestination(aMQdest);
                    MemoryUsage usage = dest.getMemoryUsage();
                    DestinationStatistics stat = dest.getDestinationStatistics();
                    
                    if (usage.getUsage()>0 && aMQdest.isTemporary())
                    {
                        log.info(aMQdest.getQualifiedName() + ":" +
                                 "Enque=" + stat.getEnqueues().getCount() +
                                 ", Deque=" + stat.getDequeues().getCount() +
                                 ", InFlight=" + stat.getInflight().getCount() +
                                 ", Dispatch=" + stat.getDispatched().getCount() +
                                 ", Expired=" + stat.getExpired().getCount() +
                                 ", Mem=" + usage.getUsage());
                    }
                }
                
                log.info("Free memory: "+Runtime.getRuntime().freeMemory());
                Thread.sleep(reportingPeriod.getMillis());
            } catch (Exception e) {
                log.warn("caught exception in run", e);
            }
        }

    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
        this.brokerService=broker.getBrokerService();
    }

}
