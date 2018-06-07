package com.cannontech.common.smartNotification.model;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.Instant;

import com.cannontech.watchdog.model.WatchdogWarnings;

public class WatchdogAssembler {

    /**
     * This will convert an WatchdogWarnings into SmartNotificationEvent .
     */
    public static SmartNotificationEvent assemble(WatchdogWarnings watchdogWarning, Instant now) {
        SmartNotificationEvent smartNotificationEvent = new SmartNotificationEvent(now);
        Map<String, Object> parameters = new HashMap<>();
        parameters.putAll(watchdogWarning.getArguments());
        smartNotificationEvent.setParameters(parameters);
        return smartNotificationEvent;
    }

}
