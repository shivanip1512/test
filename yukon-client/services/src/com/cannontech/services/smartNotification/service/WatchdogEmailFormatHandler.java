package com.cannontech.services.smartNotification.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.Watchdogs;

public abstract class WatchdogEmailFormatHandler {
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    protected MessageSourceAccessor messageSourceAccessor;

    @PostConstruct
    public void init() {
        messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
    }
    public abstract Watchdogs getWatchdogs();

    /**
     * Builds message body text for single smart notification event.
     */
    public abstract String buildSingleEventEmailBodyText(WatchdogWarningType watchdogWarningType, SmartNotificationEvent event, SmartNotificationVerbosity verbosity);
    
    /**
     * Builds message body text for multiple smart notification events.
     */
    public abstract String buildMultiEventEmailBodyText(WatchdogWarningType watchdogWarningType, SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity);

    /**
     * This method will sort the smart notification event based on timestamp - Descending order.
     */
    public List<SmartNotificationEvent> sortSmartNotificationEventList(List<SmartNotificationEvent> events) {
        List<SmartNotificationEvent> copyOfEvents = new ArrayList<>(events);
        copyOfEvents.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
        return copyOfEvents;
    }

    public String getFormattedDate(Date date) {
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.FULL, YukonUserContext.system);
        return dateFormatter.format(date);
    }
}
