package com.cannontech.services.smartNotification.service.impl;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider.ProcessorResult;
import com.cannontech.services.smartNotification.service.SmartNotificationDeciderService;

/**
 * This class is responsible for scheduling checks for unprocessed events and sending smart notification
 * message information to assemblers.
 */
public class SmartNotificationDeciderServiceImpl implements SmartNotificationDeciderService {
        
    private static Logger commsLogger = YukonLogManager.getCommsLogger();
    private static final String DEFAULT_INTERVALS = "0,1,3,5,15,30";
    private static Intervals intervals;
    
    @Autowired private ConfigurationSource configSource;
    @Autowired private SmartNotificationEventDao eventsDao;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private final Executor executor = Executors.newCachedThreadPool();

    @PostConstruct
    private void init(){
        String intervalStr = configSource.getString(MasterConfigString.SMART_NOTIFICATION_INTERVALS,
            DEFAULT_INTERVALS);
        intervals = new Intervals(intervalStr, DEFAULT_INTERVALS);
        WaitTime.setIntervals(intervals);
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_MESSAGE_PARAMETERS);
    }

    @Override
    public void logInfo(String text, Object obj) {
        if (obj instanceof SmartNotificationDecider) {
            SmartNotificationDecider decider = (SmartNotificationDecider) obj;
            commsLogger.info("[SN:{}:{}] {}", decider.getClass().getSimpleName(), decider.getEventType(), text);
        } else {
            commsLogger.info("[SN:{}] {}", obj.getClass().getSimpleName(), text);
        }
    }

    @Override
    public int getFirstInterval() {
       return intervals.getFirstInterval();
    }
    
    private void schedule(SmartNotificationDecider decider, WaitTime waitTime) {
        decider.setWaitTime(waitTime);
        DateTime nextRun = waitTime.getRunTime();
        logInfo("Scheduling new decider to run at " + nextRun, decider);
        long delay = nextRun.getMillis() - Instant.now().getMillis();
        scheduledExecutor.schedule(() -> runDecider(decider), delay, TimeUnit.MILLISECONDS);
    }
    
    private void runDecider(SmartNotificationDecider decider) {
        Instant now = Instant.now();
        try {
            ProcessorResult result = decider.process(now, eventsDao.getUnprocessedEvents(decider.getEventType()));
            if(!result.hasMessages()){
                decider.resetWaitTime();
                logInfo("Running a schedule. No messages to send. Removing wait time.", decider);
            } else if (result.isReschedule()){
                schedule(decider, result.getNextRun());
                putMessagesOnAssemblerQueue(result.getDecider(), result.getMessageParameters(),
                        result.getDecider().getWaitTime().getPreviousInterval());
            }
        } catch (Exception e) {
            commsLogger.error("Exception", e);
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
                    WaitTime waitTime = result.getDecider().getWaitTime();
                    int interval = waitTime == null ? 0 : waitTime.getPreviousInterval();
                    putMessagesOnAssemblerQueue(result.getDecider(), result.getMessageParameters(), interval);
                } catch (Exception e) {
                    commsLogger.error("Error sending smart notification message parameters", e);
                }
            });
        } 
    }
    
   private void putMessagesOnAssemblerQueue(SmartNotificationDecider decider, List<SmartNotificationMessageParameters> messages, int interval) {
        if (!messages.isEmpty()) {
            SmartNotificationMessageParametersMulti msg = new SmartNotificationMessageParametersMulti(messages, interval, false);
            logInfo("Prepared message to send: "+ msg, decider);
            jmsTemplate.convertAndSend(new SmartNotificationMessageParametersMulti(messages, interval, false));
        }
    }
    
    
    @Override
    public void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval,
            boolean sendAllInOneEmail, String digestTime) {
        if (!messages.isEmpty()) {
            SmartNotificationMessageParametersMulti msg = new SmartNotificationMessageParametersMulti(messages, interval, sendAllInOneEmail);
            logInfo("Digest Time:" + digestTime + " One email:" + sendAllInOneEmail + " Prepared message to send: " + msg, this);
            jmsTemplate.convertAndSend(new SmartNotificationMessageParametersMulti(messages, interval, sendAllInOneEmail));
        }
    }
    
}
