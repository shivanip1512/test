package com.cannontech.common.bulk.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * List of possible values for update type on status points.
 */
public enum StatusPointUpdateType implements DatabaseRepresentationSource {
    ON_FIRST_CHANGE("On First Change", false),
    ON_ALL_CHANGE("On All Change", false),
    ON_TIMER("On Timer", true),
    ON_TIMER_AND_CHANGE("On Timer And Change", true),
    HISTORICAL("Historical", false),
    ;
    
    private boolean hasPeriodicRate;
    private String dbString;
    
    private StatusPointUpdateType(String dbString, boolean hasPeriodicRate) {
        this.dbString = dbString;
        this.hasPeriodicRate = hasPeriodicRate;
    }
    
    public boolean hasRate() {
        return hasPeriodicRate;
    }
    
    public String getDatabaseRepresentation() {
        return dbString;
    }
}
