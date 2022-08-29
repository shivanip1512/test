package com.cannontech.common.log.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;

import com.google.common.collect.ImmutableMap;

public enum CustomizedSystemLogger {
    CUSTOM_API_LOGGER("com.cannontech.web.filter.TokenAuthenticationAndLoggingFilter"),
    CUSTOM_COMMS_LOGGER("com.cannontech.services.smartNotification", "com.cannontech.common.smartNotification"),
    CUSTOM_EATON_CLOUD_COMMS_LOGGER("com.cannontech.dr.eatonCloud.service.impl.v1.EatonCloudCommunicationServiceImplV1"),
    CUSTOM_RFN_COMMS_LOGGER("com.cannontech.dr.rfn", "com.cannontech.services.rfn"),
    CUSTOM_SMART_NOTIFICATION_LOGGER("com.cannontech.services.smartNotification.service.impl");

    private String[] packageNames;

    private final static ImmutableMap<String, CustomizedSystemLogger> lookupByLoggerName;
    static {
        ImmutableMap.Builder<String, CustomizedSystemLogger> nameBuilder = ImmutableMap.builder();
        for (CustomizedSystemLogger logger : values()) {
            for (String packageName : logger.packageNames) {
                nameBuilder.put(packageName, logger);
            }
        }
        lookupByLoggerName = nameBuilder.build();
    }

    CustomizedSystemLogger(String... packageNames) {
        this.packageNames = packageNames;
    }

    public String[] getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(String[] packageNames) {
        this.packageNames = packageNames;
    }

    public static boolean isCustomizedAppenderLogger(String loggerName) {
        checkArgument(loggerName != null);
        return lookupByLoggerName.keySet().stream()
                .sorted(Comparator.comparing(String::length).reversed())
                .filter(key -> loggerName.contains(key)).findAny().isPresent();
    }

    public static CustomizedSystemLogger getForLoggerName(String loggerName) {
        checkArgument(loggerName != null);
        return lookupByLoggerName.get(lookupByLoggerName.keySet()
                .stream()
                .sorted(Comparator.comparing(String::length).reversed())
                .filter(key -> loggerName.contains(key)).findAny().get());
    }
}