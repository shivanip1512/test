package com.cannontech.cc.model;

public enum ProgramParameterKey {
    DEFAULT_EVENT_OFFSET_MINUTES("60","Event Time Offset Minutes"),
    DEFAULT_NOTIFICATION_OFFSET_MINUTES("60","Notification Time Offset Minutes"),
    MINIMUM_NOTIFICATION_MINUTES("60","Minimum Notification Time Minutes"),
    DEFAULT_EVENT_DURATION_MINUTES("240","Default Event Duration"),
    MINIMUM_EVENT_DURATION_MINUTES("240","Minimum Event Duration"),
    DEFAULT_ENERGY_PRICE(".01","Energy Price");
    
    private final String defaultValue;
    private final String longDescription;

    private ProgramParameterKey(String defaultValue, String longDescription) {
        this.defaultValue = defaultValue;
        this.longDescription = longDescription;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return longDescription;
    }
}
