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

    /**
     * This method will build the email body text for both single and multiple events.
     */
    @Override
    public String buildEmailBodyText(WatchdogWarningType watchdogWarningType, SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {
        List<String> warningArgs = new ArrayList<>();
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
