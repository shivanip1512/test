package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.google.common.collect.Sets;

public class YukonWatchdogEmailBuilder extends SmartNotificationEmailBuilder {

    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.YUKON_WATCHDOG;
    }

    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity,
            int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            argumentList = getSingleEventBodyArguments(events.get(0), verbosity);
        } else {
            argumentList = getMultiEventBodyArguments(events, verbosity, eventPeriodMinutes);
        }
        return argumentList.toArray();
    }

    /**
     * Builds a list of arguments for the body text.
     * 0 - warning details arguments
     * 1 - url
     */
    private List<Object> getSingleEventBodyArguments(SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
        String warningKey = warningType.getFormatKey() + "." + verbosity;
        Object[] warningArgs = null;
        if (verbosity == SmartNotificationVerbosity.SUMMARY) {
            warningArgs = WatchdogAssembler.getWarningArgumentsForSummary(event.getParameters());
        }
        String warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs);
        argumentList.add(warningTypeString);
        argumentList.add(getUrl("watchDogWarnings "));
        return argumentList;
    }

    /**
     * Builds a list of arguments for the body text.
     * 0 - number of events
     * 1 - time period minutes
     * 2 - warning details arguments
     * 3 - url
     */
    private List<Object> getMultiEventBodyArguments(List<SmartNotificationEvent> events,
            SmartNotificationVerbosity verbosity, int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        argumentList.add(events.size());
        argumentList.add(eventPeriodMinutes);
        if (verbosity == SmartNotificationVerbosity.SUMMARY) {
            argumentList.add(getWatchdogWarningArgumentsForSummary(events, verbosity));
        }
        argumentList.add(getUrl("watchDogWarnings "));
        return argumentList;
    }

    /**
     * Builds a list of arguments for the subject line
     * Multi
     * 0 - number of events
     * Single
     * 0 - watchdog name
     */
    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            SmartNotificationEvent event = events.get(0);
            WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
            argumentList.add(warningType.getWatchdogName());
        } else {
            argumentList.add(events.size());
        }
        return argumentList.toArray();
    }

    /**
     * This method will filter out duplicate smart notification event based on watchdogwarningtype . If we
     * have multiple
     * smart notification event of same watchdogwarningtype then we will only use the latest event based on
     * timestamp.
     */
    private String getWatchdogWarningArgumentsForSummary(List<SmartNotificationEvent> events,
            SmartNotificationVerbosity verbosity) {
        Set<String> watchdogWarningType = Sets.newHashSet();
        StringBuilder builder = new StringBuilder();
        List<SmartNotificationEvent> smartNotificationEventsSortedList = sortSmartNotificationEventList(events);
        builder.append("   ");
        for (SmartNotificationEvent event : smartNotificationEventsSortedList) {
            WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
            if (!watchdogWarningType.contains(warningType.name())) {
                String warningKey = warningType.getFormatKey() + "." + verbosity;
                Object[] warningArgs = WatchdogAssembler.getWarningArgumentsForSummary(event.getParameters());
                String warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs);
                builder.append(warningTypeString).append("\n   ");
                watchdogWarningType.add(warningType.name());
            }
        }
        return builder.toString();
    }

    /**
     * This method will sort the smart notification event based on timestamp - Descending order.
     */
    private List<SmartNotificationEvent> sortSmartNotificationEventList(List<SmartNotificationEvent> events) {
        List<SmartNotificationEvent> copyOfEvents = new ArrayList<>(events);
        copyOfEvents.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
        return copyOfEvents;
    }
}
