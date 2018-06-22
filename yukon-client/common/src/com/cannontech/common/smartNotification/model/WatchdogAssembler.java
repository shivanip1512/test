package com.cannontech.common.smartNotification.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

public class WatchdogAssembler {

    public static final String WARNING_TYPE = "WarningType";
    
    public static final String ARGUMENT = "Argument";
    
    public static final String KEY_VALUE_DELIMITER = "=";
    
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

    /**
     * This method will return argument for summary verbosity .
     */
    public static Object[] getWarningArgumentsForSummary(Map<String, Object> parameters) {
        return parameters.entrySet().stream().map(Map.Entry::getValue)
                                             .map(obj -> WatchdogWarningType.fromObject(obj)).toArray();
    }

    /**
     * This method will return argument for detailed verbosity .
     */
    public static List<Object> getWarningArgumentsForDetailed(Map<String, Object> parameters, String date) {
        List<Object> parametersList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append("  ");
        builder.append(WARNING_TYPE + " : " + getWarningType(parameters).toString());
        builder.append("\n    ");
        parameters.entrySet().stream().filter(entry -> !WARNING_TYPE.equalsIgnoreCase(entry.getKey()))
                                             .map(Map.Entry::getValue)
                                             .forEach(value -> {
                                                 String[] splitArray = value.toString().split(KEY_VALUE_DELIMITER);
                                                 builder.append(splitArray[0] + " : " + splitArray[1]);
                                                 builder.append("\n    ");
                                             });;
       builder.append(TIMESTAMP + " : " + date).append("\n  ");
       parametersList.add(builder.toString());
       return parametersList;
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
