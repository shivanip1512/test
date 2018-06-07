package com.cannontech.watchdog.notification;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Component
public class WatchdogNotificationServiceImpl implements WatchdogNotificationService {

    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;

    @Override
    public void sendNotification(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        // TODO: This is temporary code, this will have to be replaced
        if (canSendSmartNotification()) {
            smartNotificationEventCreationService.send(type, events);
        } else {
            sendInternalNotification();
        }

    }

    // Will check if smart notification can be send.
    // If Broker, Service Manager and Notification services are running
    // Service Status Monitor can be used for this.
    private boolean canSendSmartNotification() {
        return true;
    }

    // This will send notification via internal notification
    private void sendInternalNotification() {

    }

    public List<SmartNotificationEvent> assemble(List<WatchdogWarnings> warnings, Instant now) {
        return warnings.stream().map(warning -> WatchdogAssembler.assemble(warning, now)).collect(Collectors.toList());
    }

}
