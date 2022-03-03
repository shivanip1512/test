package com.cannontech.services.smartNotification.service;

import static com.cannontech.common.smartNotification.model.SmartNotificationFrequency.COALESCING;
import static com.cannontech.common.smartNotification.model.SmartNotificationFrequency.IMMEDIATE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters.ProcessingType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.services.smartNotification.service.impl.Intervals;
import com.cannontech.services.smartNotification.service.impl.WaitTime;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.SetMultimap;

public abstract class SmartNotificationDecider {

    @Autowired protected SmartNotificationEventDao eventDao;
    @Autowired protected SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired protected IDatabaseCache cache;
    @Autowired private ConfigurationSource configSource;

    protected SmartNotificationEventType eventType;
    private static final String DEFAULT_INTERVALS = "0,1,3,5,15,30";
    private static Intervals intervals;

    private static Logger snLogger = YukonLogManager.getSmartNotificationsLogger(SmartNotificationDecider.class);

    // cache key/ wait time
    /*
     * Example: SMART_NOTIFICATION_INTERVALS: 0,1,2
     * 0 - send immediately
     * 1 - intervalCache contains key -> WaitTime ( 1, time when first event was received on interval 0 + 1 minute)
     * 
     * If there is no unprocessed events on the 1 interval, the key is removed from cached and the next event will be processed
     * with interval 0.
     * 
     * Each decider is responsible for the creating it's own cache key, see SmartNotifInfrastructureWarningsDecider vs
     * SmartNotifDeviceDataMonitorDecider getCacheKey method
     * 
     * A subclass does not inherit the private members of its parent class. Cache is only 1 copy.
     */
    private Map<String, WaitTime> intervalCache = new ConcurrentHashMap<String, WaitTime>();

    private Map<String, List<Statistics>> statistics = new ConcurrentHashMap<String, List<Statistics>>();

    @PostConstruct
    private void init() {
        if (intervals == null) {
            String intervalStr = configSource.getString(MasterConfigString.SMART_NOTIFICATION_INTERVALS,
                    DEFAULT_INTERVALS);
            intervals = new Intervals(intervalStr, DEFAULT_INTERVALS);
        }
    }

    /**
     * On start up marks all unprocessed events as processed. Sends all notifications immediately.
     */
    public ProcessorResult processOnStartup(List<SmartNotificationEvent> unprocessed, SmartNotificationFrequency frequency) {
        Instant now = Instant.now();
        ProcessorResult result = new ProcessorResult(this, now, new WaitTime(now, 0));
        try {
            eventDao.markEventsAsProcessed(unprocessed, result.getProcessedTime(), frequency);
            // match only valid events to subscriptions
            SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = getSubscriptionsForEvents(
                    validate(unprocessed), frequency);

            if (!subscriptions.isEmpty()) {
                result.addMessageParameters(
                        MessageParametersHelper.getMessageParameters(eventType, subscriptions, 0, ProcessingType.START_UP));
                snLogger.info("On Startup unproccessed: {} frequency:{} result:{}", unprocessed.size(), frequency
                        + result.loggingString(snLogger.getLevel()));
            } else {
                snLogger.info("On Startup didn't find any unprocessed events for frequency: " + frequency);
            }
        } catch (Exception e) {
            snLogger.error("Exception processing event on startup", e);
        }

        return result;
    }

    /**
     * Returns events that can be processed.
     */
    public abstract List<SmartNotificationEvent> validate(List<SmartNotificationEvent> events);

    /**
     * Returns map of subscriptions to events.
     */
    protected abstract SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> getSubscriptionsForEvents(
            List<SmartNotificationEvent> events, SmartNotificationFrequency frequency);

    /**
     * Returns map of subscriptions to events.
     */
    public abstract SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> mapSubscriptionsToEvents(
            Set<SmartNotificationSubscription> allSubscriptions, List<SmartNotificationEvent> allEvents);

    /**
     * Returns unprocessed grouped events.
     */
    protected abstract List<SmartNotificationEvent> getUnprocessedGroupedEvents(String cacheKey);

    /**
     * Returns cache key.
     */
    protected abstract String getCacheKey(SmartNotificationSubscription subscription);

    /**
     * Returns event type.
     */
    public SmartNotificationEventType getEventType() {
        return eventType;
    }

    /**
     * This method is called every minute from the schedule
     */
    public synchronized List<ProcessorResult> processOnInterval() {
        List<ProcessorResult> results = new ArrayList<>();
        try {
            Iterator<String> iter = intervalCache.keySet().iterator();
            // For each key in cache
            while (iter.hasNext()) {
                String cacheKey = iter.next();
                // get time when it supposed to be processed
                WaitTime currentInterval = intervalCache.get(cacheKey);
                // the time have passed
                if (currentInterval.getRunTime().isEqualNow() || currentInterval.getRunTime().isBeforeNow()) {
                    // start processing
                    // replace current interval with the next interval
                    WaitTime newInterval = replaceCachedInterval(cacheKey, currentInterval);

                    snLogger.info("UPDATING cache for key:{} to:{} from:{}", cacheKey, newInterval, currentInterval);

                    // all unprocessed events
                    List<SmartNotificationEvent> unprocessed = getUnprocessedGroupedEvents(cacheKey);
                    if (!unprocessed.isEmpty()) {
                        // get subscriptions for events
                        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionsToEvents = getSubscriptionsForEvents(
                                validate(unprocessed), COALESCING);

                        ProcessorResult result = new ProcessorResult(this, Instant.now(), currentInterval);

                        // no subscriptions found
                        if (subscriptionsToEvents.isEmpty()) {
                            // if there is no events to process, remove the key from cache
                            iter.remove();
                            snLogger.info("REMOVING from cache (No subscriptions for events):{}/{} all:{} statistics:{}",
                                    cacheKey, newInterval, intervals, getStatistics(cacheKey));

                        } else {
                            snLogger.info("PROCESSING cache key:{}/{} events:{} subscriptions:{}", cacheKey, currentInterval,
                                    subscriptionsToEvents.values().size(),
                                    subscriptionsToEvents.keys().stream().distinct().count());
                            // subscription found add info used to create email message
                            result.addMessageParameters(
                                    MessageParametersHelper.getMessageParameters(eventType, subscriptionsToEvents,
                                            result.getInterval(), ProcessingType.ON_INTERVAL));

                            cacheStatistics(cacheKey, result);
                        }
                        eventDao.markEventsAsProcessed(unprocessed, result.getProcessedTime(), COALESCING);
                        results.add(result);
                    } else {
                        // if there is no events to process, remove the key from cache
                        iter.remove();
                        snLogger.info("REMOVING from cache (No unprocessed events):{}/{} all:{} statistics:{}",
                                cacheKey, newInterval, intervals, getStatistics(cacheKey));

                    }
                } else {
                    // It is not time to process events yet
                    snLogger.info("WAITING TO PROCESS Cache key:{} Next run:{} Now:{}", cacheKey, currentInterval,
                            new DateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                }
            }
        } catch (Exception e) {
            snLogger.error("Exception processing on interval", e);
        }
        return results;
    }

    /**
     * Caches debugging statistics
     */
    private void cacheStatistics(String cacheKey, ProcessorResult result) {
        if (statistics.get(cacheKey) == null) {
            statistics.put(cacheKey, new ArrayList<>());
        }
        Statistics statistics = new Statistics(result.intervalTime);
        result.getMessageParameters().forEach(p -> {
            statistics.statistics.add(new RecipientStatistics(p.getRecipients(), p.getEvents().size()));
        });
        this.statistics.get(cacheKey).add(statistics);
    }
    
    /**
     * Logs and removes statistics from cache 
     */
    private String getStatistics(String cacheKey) {
        List<Statistics> stats = statistics.remove(cacheKey);
        
        if(stats == null || stats.isEmpty()) {
            return "";
        }
        
        int total = 0;
         
        for(Statistics stat: stats) {
            total = total + stat.statistics.stream().map(s -> s.events)
                    .collect(Collectors.summingInt(Integer::intValue));
        }
    
        return "STATISTICS Events processed:" + total + " Details:" + stats;
    }

    private class Statistics {
        private WaitTime currentInterval;
        private List<RecipientStatistics> statistics = new ArrayList<>();

        public Statistics(WaitTime currentInterval) {
           this.currentInterval = currentInterval;
        }

        @Override
        public String toString() {
            return "["+currentInterval+":"+ statistics +"]";
        }

    }

    private class RecipientStatistics {
        private List<String> emails = new ArrayList<>();
        private int events;

        public RecipientStatistics(List<String> recipients, int events) {
            emails.addAll(recipients);
            this.events = events;
        }

        @Override
        public String toString() {
            return "events:" + events + " " + emails;
        }
    }

    /**
     * Replaces the current interval in cache with a new interval. New processing interval/time is returned.
     */
    private WaitTime replaceCachedInterval(String cacheKey, WaitTime currentInterval) {
        // if intervals in master.cfg are 0, 1, 2, and we are on the interval 0, interval 1 will be returned, if we are on the
        // last
        // interval (2), the last interval (2) will always be returned.
        int nextInterval = intervals.getNextInterval(currentInterval.getCurrentInterval());
        Instant nextRunTime = new DateTime().plusMinutes(nextInterval).toInstant();
        WaitTime newInterval = new WaitTime(nextRunTime, nextInterval);
        // the processing time in cache is replaced with now + nextInterval
        intervalCache.replace(cacheKey, newInterval);
        return newInterval;
    }

    /**
     * Process all immediate subscriptions when event is received
     */
    public synchronized ProcessorResult processImmediateSubscriptions(List<SmartNotificationEvent> events) {
        Instant now = Instant.now();
        eventDao.markEventsAsProcessed(events, now, IMMEDIATE);
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> immediate = getSubscriptionsForEvents(
                validate(events), IMMEDIATE);
        ProcessorResult result = new ProcessorResult(this, now, new WaitTime(now, 0));
        if (!immediate.isEmpty()) {
            result.addMessageParameters(
                    MessageParametersHelper.getMessageParameters(eventType, immediate, 0, ProcessingType.IMMEDIATE));
        }
        return result;
    }

    /**
     * Process all grouped subscriptions when event is received
     */
    public synchronized ProcessorResult processGroupedSubscriptions(List<SmartNotificationEvent> events) {
        WaitTime current = new WaitTime(Instant.now(), 0);
        ProcessorResult result = new ProcessorResult(this, Instant.now(), current);
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionsToEvents = getSubscriptionsForEvents(
                validate(events), COALESCING);
        
        if (subscriptionsToEvents.isEmpty()) {
            // there are no coalescing subscriptions
            eventDao.markEventsAsProcessed(events, result.getProcessedTime(), COALESCING);
            return result;
        }

        /*
         * When we received a batch of events, they are sent per type or per monitor. So there events above can't be for different
         * monitors, or different events type.
         * Taking the example of DDM, the subscription might be for a different people but they will always be for the same
         * monitor. We need subscription parameters to get the key.
         * Getting a cache key for the first subscription should work.
         */
        String cacheKey = getCacheKey(subscriptionsToEvents.keySet().iterator().next());
        if (intervalCache.get(cacheKey) != null) {
            // We found result in cache so we are in the process of grouping events before sending, the events will be processed
            // via schedule that runs every minute.
            return result;
        }

        // Mark events that do not have subscription as processed
        List<SmartNotificationEvent> invalidEvents = new ArrayList<SmartNotificationEvent>(events);
        invalidEvents.removeAll(subscriptionsToEvents.values());
        eventDao.markEventsAsProcessed(invalidEvents, result.getProcessedTime(), COALESCING);

        int firstInterval = intervals.getFirstInterval();
        // send notifications immediately and create processing time for the next interval
        if (firstInterval == 0) {
            int nextInterval = intervals.getNextInterval(firstInterval);
            addCachedInterval(cacheKey, nextInterval);
            if (!subscriptionsToEvents.isEmpty()) {
                result.addMessageParameters(MessageParametersHelper.getMessageParameters(eventType, subscriptionsToEvents, 0,
                        ProcessingType.ON_INTERVAL));
            }
            cacheStatistics(cacheKey, result);
            eventDao.markEventsAsProcessed(new ArrayList<>(subscriptionsToEvents.values()), result.getProcessedTime(),
                    COALESCING);
        } else {
            // first interval is not 0, reschedule for processing, the new time is now + the first interval
            addCachedInterval(cacheKey, firstInterval);
        }
        return result;
    }

    /**
     * Add a new interval/processing time to cache
     */
    private void addCachedInterval(String cacheKey, int nextInterval) {
        Instant nextRunTime = new DateTime().plusMinutes(nextInterval).toInstant();
        WaitTime newInterval = new WaitTime(nextRunTime, nextInterval);
        intervalCache.put(cacheKey, newInterval);
        snLogger.info("Adding interval {} to cache for key:{}", newInterval, cacheKey);
    }

    public final class ProcessorResult {
        private SmartNotificationDecider decider;
        private List<SmartNotificationMessageParameters> messageParameters = new ArrayList<>();
        private Instant processedTime;
        private WaitTime intervalTime;
        private String cacheKey;

        public ProcessorResult(SmartNotificationDecider decider, Instant processedTime, WaitTime intervalTime) {
            this.decider = decider;
            this.processedTime = processedTime;
            this.intervalTime = intervalTime;
        }

        public SmartNotificationDecider getDecider() {
            return decider;
        }

        public List<SmartNotificationMessageParameters> getMessageParameters() {
            return messageParameters;
        }

        public void addMessageParameters(List<SmartNotificationMessageParameters> messageParameters) {
            this.messageParameters.addAll(messageParameters);
        }

        public boolean hasMessages() {
            return !messageParameters.isEmpty();
        }

        public int getInterval() {
            return intervalTime.getCurrentInterval();
        }

        public Instant getProcessedTime() {
            return processedTime;
        }

        public void setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        @Override
        public String toString() {
            ToStringBuilder tsb = getLogMsg();
            tsb.append("Messages", messageParameters);
            return tsb.toString();
        }

        private ToStringBuilder getLogMsg() {
            ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
            tsb.appendSuper(super.toString());
            tsb.append("Interval", intervalTime + " of " + intervals);
            tsb.append("Total messages", messageParameters.size());
            tsb.append("Processed Time", processedTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
            if (cacheKey != null) {
                tsb.append("CacheKey", cacheKey);
            }
            return tsb;
        }

        public String loggingString(Level level) {
            if (level.isMoreSpecificThan(Level.INFO)) {
                return toString();
            } else {
                return getLogMsg().toString();
            }
        }
    }
}
