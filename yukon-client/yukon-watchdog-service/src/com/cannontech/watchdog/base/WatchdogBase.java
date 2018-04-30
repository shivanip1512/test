package com.cannontech.watchdog.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.notification.WatchdogNotificationService;

public abstract class WatchdogBase implements Watchdog {

    @Autowired WatchdogNotificationService watchDogNotif;

    @Override
    public void sendNotification(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        watchDogNotif.sendNotification(type, events);
    }

    @Override
    public List<SmartNotificationEvent> assemble(List<WatchdogWarnings> warnings) {
        return watchDogNotif.assemble(warnings);
    }

    @Override
    public void watchAndNotify() {
        List<WatchdogWarnings> warnings = watch();
        if (warnings.size() > 1) {
            List<SmartNotificationEvent> events = assemble(warnings);
            sendNotification(SmartNotificationEventType.YUKON_WATCHDOG, events);
        }
    }

}
