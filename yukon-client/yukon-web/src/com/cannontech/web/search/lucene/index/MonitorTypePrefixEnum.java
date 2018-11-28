package com.cannontech.web.search.lucene.index;

/**
 * This Enum defines the Monitor with prefix that is used inside MonitorIndexManager to create
 * id field with prefix and monitorId.
 *
 */
public enum MonitorTypePrefixEnum {

    DEVICE_DATA_MONITOR("1"),
    OUTAGE_MONITOR("2"),
    TAMPER_FLAG_MONITOR("3"),
    STATUS_POINT_MONITOR("4"),
    PORTER_RESPONSE_MONITOR("5"),
    VALIDATION_MONITOR("6");

    private final String prefix;

    private MonitorTypePrefixEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static MonitorTypePrefixEnum fromName(String prefix) {
        for (MonitorTypePrefixEnum monitor : values()) {
            if (monitor.getPrefix().equalsIgnoreCase(prefix)) {
                return monitor;
            }
        }
        return null;
    }
}
