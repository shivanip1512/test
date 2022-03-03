package com.cannontech.common.bulk.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * List of possible values for update type on analog points.
 */
public enum AnalogPointUpdateType implements DatabaseRepresentationSource, DisplayableEnum {

    ON_FIRST_CHANGE("On First Change", false),
    ON_ALL_CHANGE("On All Change", false),
    ON_TIMER("On Timer", true),
    ON_TIMER_AND_CHANGE("On Timer+Change", true),
    CONSTANT("Constant", false),
    HISTORICAL("Historical", false),
    BACKFILLING_HISTORICAL("Backfilling", false)
    ;
    
    private static String baseKey = "yukon.common.point.updateType.";

    private boolean hasPeriodicRate;
    private String dbString;
    
    private AnalogPointUpdateType(String dbString, boolean hasPeriodicRate) {
        this.dbString = dbString;
        this.hasPeriodicRate = hasPeriodicRate;
    }
    
    public boolean hasRate() {
        return hasPeriodicRate;
    }
    
    public String getDatabaseRepresentation() {
        return dbString;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
