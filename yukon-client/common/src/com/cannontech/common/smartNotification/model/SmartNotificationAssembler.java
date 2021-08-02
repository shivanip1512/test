package com.cannontech.common.smartNotification.model;

import java.util.Map;

public class SmartNotificationAssembler {

    public static int getIntValue(Map<String, Object> parameters, String type) {
        return Integer.parseInt(parameters.get(type).toString());
    }

    public static String getStringValue(Map<String, Object> parameters, String type) {
        return parameters.get(type).toString();
    }
}
