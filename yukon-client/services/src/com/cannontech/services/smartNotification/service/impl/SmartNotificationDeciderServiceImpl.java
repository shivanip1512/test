package com.cannontech.services.smartNotification.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider.ProcessorResult;
import com.cannontech.services.smartNotification.service.SmartNotificationDeciderService;
import com.google.common.collect.Iterables;

/**
 * This class is responsible for scheduling checks for unprocessed events and sending smart notification
 * message information to assemblers.
 */
public class SmartNotificationDeciderServiceImpl implements SmartNotificationDeciderService {
        
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationDeciderServiceImpl.class);
    @Autowired private ScheduledExecutor scheduledExecutor;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired SmartNotificationEventDao eventsDao;
    @Autowired private ConfigurationSource configSource;
    private JmsTemplate jmsTemplate;
    private Executor executor = Executors.newCachedThreadPool();
    private static final String queue = JmsApiDirectory.SMART_NOTIFICATION_MESSAGE_PARAMETERS.getQueue().getName();
    private static final String DEFAULT_INTERVALS = "0,1,3,5,15,30";
    private static Intervals intervals;
    
    /**
     * This class is responsible for loading and managing intervals.
     */
    private final class Intervals {
        private Set<Integer> intervals = new TreeSet<>();

        Intervals(String intervalStr) {
            parseIntervals(intervalStr);
        }

        private void parseIntervals(String intervalStr) {
            try {
                intervals.clear();
                intervals.addAll(Stream.of(intervalStr.split(",")).map(Integer::parseInt).collect(Collectors.toSet()));
                if (intervals.iterator().next() < 0) {
                    parseIntervals(DEFAULT_INTERVALS);
                }
            } catch (Exception e) {
                parseIntervals(DEFAULT_INTERVALS);
            }
            log.info("Smart Notification Intervals:" + intervals);
        }

        public Integer getNextInterval(int currentInterval) {
            Set<Integer> allIntervals = new TreeSet<>(intervals);
            allIntervals.removeIf(interval -> interval <= currentInterval);
            if (allIntervals.isEmpty()) {
                return Iterables.getLast(intervals);
            } else {
                return allIntervals.iterator().next();
            }
        }
        
        public Integer getFirstInterval() {
            return intervals.iterator().next();
        }
    }
    
    public final static class WaitTime {
        private int interval;
        private DateTime runTime;

        public WaitTime(int interval, DateTime runTime) {
            this.interval = interval;
            this.runTime = runTime;
        }

        public int getInterval() {
            return interval;
        }

        public DateTime getRunTime() {
            return runTime;
        }

        /**
         * Returns next run time info.
         */
        public WaitTime getNext(Instant now) {
            int newInterval = intervals.getNextInterval(interval);
            DateTime newRunTime = now.toDateTime().plusMinutes(newInterval);
            return new WaitTime(newInterval, newRunTime);
        }
        
        /**
         * Returns first run time info.
         */
        public static WaitTime getFirst(Instant now) {
            int newInterval = intervals.getFirstInterval();
            DateTime newRunTime = now.toDateTime().plusMinutes(newInterval);
            return new WaitTime(newInterval, newRunTime);
        }
        
        @Override
        public String toString(){
            return "[Run Time="+runTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss")+" - Interval="+interval+" minutes]";
        }
    }
    
    @PostConstruct
    private void init(){
        String intervalStr = configSource.getString(MasterConfigString.SMART_NOTIFICATION_INTERVALS,
            DEFAULT_INTERVALS);
        intervals = new Intervals(intervalStr);
        
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setPubSubDomain(false);
        
    }
    
    private void logDebug(String text, SmartNotificationDecider decider) {
        log.debug(getLogMsg(text, decider));
    }

    @Override
    public String getLogMsg(String text, SmartNotificationDecider decider){
        String waitTimeMsg = decider.getWaitTime() == null ? "" : " "+decider.getWaitTime();
        String msg = decider.getEventType() + " " + text  + waitTimeMsg;
        return msg;
    }
    
    @Override
    public int getFirstInterval() {
       return intervals.getFirstInterval();
    }
    
    private void schedule(SmartNotificationDecider decider, WaitTime waitTime) {
        decider.setWaitTime(waitTime);
        DateTime nextRun = waitTime.getRunTime();
        logDebug("Scheduling new decider to run at " + nextRun, decider);
        long delay = nextRun.getMillis() - Instant.now().getMillis();
        scheduledExecutor.schedule(new Decider(decider), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void send(ProcessorResult result) {
        if (result.isReschedule()) {
            schedule(result.getDecider(), result.getNextRun());
        }
        if (result.hasMessages()) {
            executor.execute(() -> {
                try {
                    logDebug("Sending message parameters immediately", result.getDecider());
                    WaitTime waitTime = result.getDecider().getWaitTime();
                    int interval = waitTime == null ? 0 : waitTime.interval;
                    putMessagesOnAssemblerQueue(result.getMessageParameters(), interval, false);
                } catch (Exception e) {
                    log.error("Error sending smart notification message parameters", e);
                }
            });
        } else {
            String nextRun = result.getNextRun() != null ? " Next run:" : "";
            logDebug("No messages to send." + nextRun, result.getDecider());
        }
    }
    
    @Override
    public void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval,
            boolean sendAllInOneEmail) {
        if (!messages.isEmpty()) {
            messages.forEach(m -> {
                log.debug(m.getType() + " Sending message=" + m);
            });
            jmsTemplate.convertAndSend(queue, new SmartNotificationMessageParametersMulti(messages, interval, sendAllInOneEmail));
        }
    }
    
    private class Decider implements Runnable {
        private final SmartNotificationDecider decider;
     
        private Decider(SmartNotificationDecider decider) {
            this.decider = decider;
        }

        @Override
        public void run() {
            Instant now = Instant.now();
            try {
                logDebug("Running a schedule", decider);
                ProcessorResult result = decider.process(now, eventsDao.getUnprocessedEvents(decider.getEventType()));
                if(!result.hasMessages()){
                    decider.resetWaitTime();
                    log.debug(decider.getEventType() + " No messages to send. Removing wait time.");
                } else if (result.isReschedule()){
                    schedule(decider, result.getNextRun());
                    putMessagesOnAssemblerQueue(result.getMessageParameters(), 
                            result.getDecider().getWaitTime().interval, false);
                }
            } catch (Exception e) {
                decider.resetWaitTime();
                StringWriter stack = new StringWriter();
                e.printStackTrace(new PrintWriter(stack));
                log.error(stack.toString());
            }
        }
    }
}
