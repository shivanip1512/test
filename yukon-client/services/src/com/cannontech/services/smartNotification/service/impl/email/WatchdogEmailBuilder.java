package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.services.smartNotification.service.WatchdogEmailFormatHandler;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.Watchdogs;
import com.cannontech.common.stream.StreamUtils;

public class WatchdogEmailBuilder extends SmartNotificationEmailBuilder {
    private static final Logger log = YukonLogManager.getLogger(WatchdogEmailBuilder.class);

    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.YUKON_WATCHDOG;
    }

    private Map<Watchdogs, WatchdogEmailFormatHandler> emailBuilderHandler;
    
    @Autowired
    public WatchdogEmailBuilder(List<WatchdogEmailFormatHandler> watchTypeEmailHandlers) {
        // Map of watchType to their handler methods
        emailBuilderHandler = watchTypeEmailHandlers.stream()
                                                 .collect(StreamUtils.mapToSelf(handler -> handler.getWatchdogs()));
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
     * 0 - email body text
     * 1 - url
     */
    private List<Object> getSingleEventBodyArguments(SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
        WatchdogEmailFormatHandler watchdogEmailHandler = getWatchdogEmailFormatHandler(warningType);
        String emailBodyContent = watchdogEmailHandler.buildSingleEventEmailBodyText(warningType, event, verbosity);
        argumentList.add(emailBodyContent);
        argumentList.add(getUrl("watchdogWarnings"));
        return argumentList;
    }

    /**
     * Builds a list of arguments for the body text.
     * 0 - number of events
     * 1 - time period minutes
     * 2 - email body text
     * 3 - url
     */
    private List<Object> getMultiEventBodyArguments(List<SmartNotificationEvent> events,
            SmartNotificationVerbosity verbosity, int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        argumentList.add(events.size());
        argumentList.add(eventPeriodMinutes);
        StringBuilder builder = new StringBuilder();
        for(SmartNotificationEvent event : events) {
            WatchdogWarningType warningType = WatchdogAssembler.getWarningType(event.getParameters());
            WatchdogEmailFormatHandler watchdogEmailHandler = getWatchdogEmailFormatHandler(warningType);
            String emailBodyContent = watchdogEmailHandler.buildMultiEventEmailBodyText(warningType, event, verbosity);
            builder.append(emailBodyContent);
        }
        argumentList.add(builder.toString());
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
            argumentList.add(warningType.getName());
        } else {
            argumentList.add(events.size());
        }
        return argumentList.toArray();
    }

    /**
     * This method will return the email format handler based on the watchdog warning type.
     */
    private WatchdogEmailFormatHandler getWatchdogEmailFormatHandler(WatchdogWarningType warningType) {
        WatchdogEmailFormatHandler watchdogEmailHandler = emailBuilderHandler.get(warningType.getWatchdogName());
        if (watchdogEmailHandler == null) {
            log.error("Unable to format email content - unsupported watchdog type : " + warningType.getWatchdogName());
        }
        return watchdogEmailHandler;
    }

}
