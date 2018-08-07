package com.cannontech.services.smartNotification.service.impl.email;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.watchdog.model.WatchdogWarningType;

public class YukonWatchdogEmailBuilder extends SmartNotificationEmailBuilder {
    @Autowired private DateFormattingService dateFormattingService;

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
        String warningKey = warningType.getWatchdogName().getFormatKey() + "." + verbosity;
        Object[] warningArgs = null;
        String warningTypeString = null;
        String formattedDate = getFormattedDate(event.getTimestamp().toDate());
        if (verbosity == SmartNotificationVerbosity.SUMMARY) {
            warningArgs = WatchdogAssembler.getWarningArgumentsForSummary(event.getParameters());
            warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs);
            warningTypeString = warningTypeString + " - " + formattedDate;
        } else if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            List<Object> objectList = WatchdogAssembler.getWarningArgumentsForDetailed(event.getParameters(), formattedDate);
            warningArgs = objectList.toArray();
            warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs);
        }
        argumentList.add(warningTypeString);
        argumentList.add(getUrl("watchdogWarnings"));
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
        } else if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            argumentList.add(getWatchdogWarningArgumentsForDetailed(events, verbosity));
        }
        argumentList.add(getUrl("watchdogWarnings"));
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
            argumentList.add(warningType);
        } else {
            argumentList.add(events.size());
        }
        return argumentList.toArray();
    }

    /**
     * This method will return the warning arguments for summary verbosity.
     * Events will be sorted in descending order based on timestamp .
     */
    private String getWatchdogWarningArgumentsForSummary(List<SmartNotificationEvent> events,
            SmartNotificationVerbosity verbosity) {
        StringBuilder builder = new StringBuilder();
        List<SmartNotificationEvent> smartNotificationEventsSortedList = sortSmartNotificationEventList(events);
        builder.append("   ");
        for (SmartNotificationEvent event : smartNotificationEventsSortedList) {
            WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
                String warningKey = warningType.getWatchdogName().getFormatKey() + "." + verbosity;
                Object[] warningArgs = WatchdogAssembler.getWarningArgumentsForSummary(event.getParameters());
                String warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs);
                String formattedDate = getFormattedDate(event.getTimestamp().toDate());
                builder.append(warningTypeString + " - " + formattedDate).append("\n   ");
        }
        return builder.toString();
    }

    /**
     * This method will return the warning arguments for detailed verbosity.
     * Events will be sorted in descending order based on timestamp .
     */
    private String getWatchdogWarningArgumentsForDetailed(List<SmartNotificationEvent> events,
            SmartNotificationVerbosity verbosity) {
        StringBuilder builder = new StringBuilder();
        List<SmartNotificationEvent> smartNotificationEventsSortedList = sortSmartNotificationEventList(events);
        builder.append("  ");
        for (SmartNotificationEvent event : smartNotificationEventsSortedList) {
            WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
                String warningKey = warningType.getWatchdogName().getFormatKey() + "." + verbosity;
                String formattedDate = getFormattedDate(event.getTimestamp().toDate());
                List<Object> warningArgs = WatchdogAssembler.getWarningArgumentsForDetailed(event.getParameters(), formattedDate);
                String warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs.toArray());
                builder.append(warningTypeString).append("\n  ");
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

    private String getFormattedDate(Date date) {
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.FULL, YukonUserContext.system);
        return dateFormatter.format(date);
    }
}
