package com.cannontech.common.smartNotification.model;

import java.util.LinkedHashMap;
import java.util.Map;


import org.joda.time.Instant;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

public class WatchdogAssembler {

    public static final String WARNING_TYPE = "WarningType";

    public static final String SERVICE_STATUS = "Status";

    public static final String TIMESTAMP = "Timestamp";

    /**
     * This will convert an WatchdogWarnings into SmartNotificationEvent .
     */
    public static SmartNotificationEvent assemble(WatchdogWarnings watchdogWarning, Instant now) {
        SmartNotificationEvent smartNotificationEvent = new SmartNotificationEvent(now);
        Map<String, Object> parameters = new LinkedHashMap<>();
        addArgument(parameters ,watchdogWarning.getArguments());
        smartNotificationEvent.setParameters(parameters);
        return smartNotificationEvent;
    }

    public static WatchdogWarningType getWarningType(Map<String, Object> parameters) {
        return WatchdogWarningType.valueOf(parameters.get(WARNING_TYPE).toString());
    }

    public static String getServiceStatus(Map<String, Object> parameters) {
        return parameters.get(SERVICE_STATUS).toString();
    }

    /**
     * This method puts watchdog warning arguments to smart notification events parameters map .
     */
    private static void addArgument(Map<String, Object> parameters, Map<String, Object> arguments) {
        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                parameters.put(entry.getKey(), entry.getValue());
        }
    }

}
