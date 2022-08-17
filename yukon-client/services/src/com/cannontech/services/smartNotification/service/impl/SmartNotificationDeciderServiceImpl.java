package com.cannontech.services.smartNotification.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

/**
 * This class is responsible for scheduling checks for unprocessed events and sending smart notification
 * message information to assemblers.
 */
public class SmartNotificationDeciderServiceImpl implements SmartNotificationDeciderService {
        
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationDeciderServiceImpl.class);
    private static final String queue = JmsApiDirectory.SMART_NOTIFICATION_MESSAGE_PARAMETERS.getQueue().getName();
    private static final String DEFAULT_INTERVALS = "0,1,3,5,15,30";
    private static Intervals intervals;
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configSource;
    @Autowired private SmartNotificationEventDao eventsDao;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    
    private final Executor executor = Executors.newCachedThreadPool();
    private JmsTemplate jmsTemplate;
    
	@PostConstruct
    private void init(){
        String intervalStr = configSource.getString(MasterConfigString.SMART_NOTIFICATION_INTERVALS,
            DEFAULT_INTERVALS);
        intervals = new Intervals(intervalStr, DEFAULT_INTERVALS);
        WaitTime.setIntervals(intervals);
        
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
        scheduledExecutor.schedule(() -> runDecider(decider), delay, TimeUnit.MILLISECONDS);
    }
    
    private void runDecider(SmartNotificationDecider decider) {
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
                        result.getDecider().getWaitTime().getPreviousInterval(), false);
            }
        } catch (Exception e) {
            decider.resetWaitTime();
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            log.error(stack.toString());
        }
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
                    int interval = waitTime == null ? 0 : waitTime.getPreviousInterval();
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
    
}
