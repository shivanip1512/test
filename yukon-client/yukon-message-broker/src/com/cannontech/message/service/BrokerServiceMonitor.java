package com.cannontech.message.service;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cannontech.clientutils.YukonLogManager;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;


/**
 *  This class runs a thread that monitors the ActiveMQ broker.  It periodically outputs queue depths
 *  and memory usage as well as JVM memory statistics from the GC. 
 */
public class BrokerServiceMonitor extends Thread implements NotificationListener {

    /** General class logger */
    private Logger log = YukonLogManager.getLogger(BrokerServiceMonitor.class);
    
    /** Put this under a sub-logger so it can be independently manipulated.
     *  
     * To enable, put this in yukonLogging.xml: 
     * 
     *   <logger name="com.cannontech.services.jms.BrokerServiceMonitor.gc"> 
     *     <level value="DEBUG"/>
     *   </logger>
     **/
    private Logger logGC = YukonLogManager.getLogger(BrokerServiceMonitor.class.getCanonicalName()+".gc");

    DateTime nextGCReport;
    
    /** Broker instance */
    private Broker broker;
    /** BrokerService instance */
    private BrokerService brokerService;

    /** Time period for checking on the queue depths from the Java System Properties */
    private Duration reportingPeriod = Duration.standardSeconds(
        Integer
            .getInteger("com.cannontech.services.jms.BrokerServiceMonitor.reportingPeriodSeconds",
                        5 /* minutes */* 60 /* Seconds/Minute */));

    /** Constructor. Registers this class for GC notifications as well. */
    public BrokerServiceMonitor() {
        super();

        List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();

        for (GarbageCollectorMXBean gcbean : gcbeans) {
            NotificationEmitter emitter = (NotificationEmitter) gcbean;

            // Add the a listener
            emitter.addNotificationListener(this, null, null);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (ActiveMQDestination aMQdest : broker.getDestinations()) {
                    Destination dest = brokerService.getDestination(aMQdest);
                    MemoryUsage usage = dest.getMemoryUsage();
                    DestinationStatistics stat = dest.getDestinationStatistics();

                    if (usage.getUsage() > 0) {
                        log.info(aMQdest.getQualifiedName() + ":" + 
                                "Enque=" + stat.getEnqueues().getCount() +
                                ", Deque=" + stat.getDequeues().getCount() + 
                                ", InFlight=" + stat.getInflight().getCount() +
                                ", Dispatch=" + stat.getDispatched().getCount() +
                                ", Expired=" + stat.getExpired().getCount() + 
                                ", Mem=" + usage.getUsage());
                    }
                }

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
        this.brokerService = broker.getBrokerService();
    }

    // implement the notifier callback handler
    @Override
    public void handleNotification(Notification notification, Object handback) {
        // we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
        if (notification.getType()
                        .equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            if (nextGCReport == null || nextGCReport.isBeforeNow() || freeMemory < totalMemory / 3) {
                nextGCReport = DateTime.now().plusHours(1);

                // get notification information
                GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

                // GC info
                GcInfo gcInfo = info.getGcInfo();
                logGC.debug("GC start: " + gcInfo.getStartTime() + ", end: " + gcInfo.getEndTime() + ", duration: " + gcInfo.getDuration() + " (ms)");
                logGC.debug("   reason: " + info.getGcCause());

                // Get before and after GC stats
                Map<String, java.lang.management.MemoryUsage> memBefore = gcInfo.getMemoryUsageBeforeGc();
                Map<String, java.lang.management.MemoryUsage> memAfter = gcInfo.getMemoryUsageAfterGc();

                // Print in a pretty table
                logGC.debug(String.format("%22s|%7s|%7s", "Heap Used", "Before", "After"));
                for (Entry<String, java.lang.management.MemoryUsage> entry : memBefore.entrySet()) {
                    String name = entry.getKey();
                    long memUsedBefore = entry.getValue().getUsed();
                    long memUsedAfter = memAfter.get(name).getUsed();
                    logGC.debug(String.format("%22s|%7d|%7d",
                                              name,
                                              memUsedBefore / 1024,
                                              memUsedAfter / 1024));
                }
            }

            // Just for grins...
            logGC.debug("Free/Total memory: " + freeMemory / 1024 + "k/" + totalMemory / 1024 + "k");
        }
    }
}
