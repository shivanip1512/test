package com.cannontech.common.smartNotification.model;

import java.util.LinkedHashMap;
import java.util.Map;
import org.joda.time.Instant;

import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

public class WatchdogAssembler {

    public static final String WARNING_TYPE = "WarningType";
    
    public static final String ARGUMENT = "Argument";
    
    public static final String KEY_VALUE_DELIMITER = "=";
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

    /**
     * This method will return argument for summary verbosity .
     */
    public static Object[] getWarningArgumentsForSummary(Map<String, Object> parameters) {
        return parameters.entrySet().stream().map(Map.Entry::getValue)
                                             .map(obj -> WatchdogWarningType.fromObject(obj)).toArray();
    }

    /**
     * This method put watchdog warning arguments to smart notification parameters . WarningType argument will be 
     * common in each watchdog warning . Apart from WarningType argument , other arguments will be stored with key as "Argument{index}" 
     * and value as arguments key and value with "=" as delimiter.We have to form a map in this way as we don't have fixed
     * set of key value pairs for each watchdog service .
     */
    private static void addArgument(Map<String, Object> parameters, Map<String, Object> arguments) {
        int i = 0;
        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(WARNING_TYPE)) {
                parameters.put(entry.getKey(), entry.getValue());
            } else {
                parameters.put(ARGUMENT + i, entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue());
                i++;
            }
        }
    }
}
