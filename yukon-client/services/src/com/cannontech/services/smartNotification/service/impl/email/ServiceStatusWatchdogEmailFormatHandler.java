package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.services.smartNotification.service.WatchdogEmailFormatHandler;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.Watchdogs;

public class ServiceStatusWatchdogEmailFormatHandler extends WatchdogEmailFormatHandler {

    @Override
    public Watchdogs getWatchdogs() {
        return Watchdogs.SERVICE_STATUS;
    }

    @Override
    public String buildSingleEventEmailBodyText(WatchdogWarningType watchdogWarningType, SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {
        return buildEmailBodyText(watchdogWarningType, event, verbosity);
    }

    @Override
    public String buildMultiEventEmailBodyText(WatchdogWarningType watchdogWarningType, SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {
        return buildEmailBodyText(watchdogWarningType, event, verbosity);
    }

    /**
     * This method will build the email body text for both single and multiple events.
     * Currently it is used for both single and multiple events. In future if we need events
     * specific customization, we can do that in the overridden buildSingleEventEmailBodyText 
     * and buildMultiEventEmailBodyText method.
     */
    private String buildEmailBodyText(WatchdogWarningType watchdogWarningType, SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {
        List<String> warningArgs = new ArrayList<String>();
        String warningKey = watchdogWarningType.getWatchdogName().getFormatKey() + "." + verbosity;
        String emailBodyText = null;
        String formattedDate = getFormattedDate(event.getTimestamp().toDate());
        if (verbosity == SmartNotificationVerbosity.SUMMARY) {
            warningArgs.add(watchdogWarningType.getName());
            warningArgs.add(WatchdogAssembler.getServiceStatus(event.getParameters()));
            emailBodyText = messageSourceAccessor.getMessage(warningKey, warningArgs.toArray());
            emailBodyText = emailBodyText + " - " + formattedDate;
        } else if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            warningArgs.add(watchdogWarningType.getName());
            warningArgs.add(WatchdogAssembler.getServiceStatus(event.getParameters()));
            warningArgs.add(formattedDate);
            emailBodyText = messageSourceAccessor.getMessage(warningKey, warningArgs.toArray());
        }
        return emailBodyText;

    }

}
