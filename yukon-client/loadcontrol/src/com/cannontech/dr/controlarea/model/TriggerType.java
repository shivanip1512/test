package com.cannontech.dr.controlarea.model;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum TriggerType {
    THRESHOLD_POINT("Threshold Point"),
    THRESHOLD("Threshold"),
    STATUS("Status");

    private final String dbString;

    private final static ImmutableMap<String, TriggerType> lookupByDbString;
    static {
        Builder<String, TriggerType> dbBuilder = ImmutableMap.builder();
        for (TriggerType triggerType : values()) {
            dbBuilder.put(triggerType.dbString.toLowerCase(), triggerType);
        }
        lookupByDbString = dbBuilder.build();
    }

    /**
     * Looks up the the TriggerType based on the string that is used in the
     * database.
     * @param dbString - type name to lookup, case insensitive
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static TriggerType getForDbString(String dbString) throws IllegalArgumentException {
        TriggerType deviceType = lookupByDbString.get(dbString.toLowerCase());
        Validate.notNull(deviceType, dbString);
        return deviceType;
    }

    private TriggerType(String dbString) {
        this.dbString = dbString;
    }

    public String getDbString() {
        return dbString;
    }
}
