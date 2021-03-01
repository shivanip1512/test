package com.cannontech.web.tools.points.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

public enum CalcUpdateType implements DatabaseRepresentationSource, DisplayableEnum {
    
    ON_FIRST_CHANGE("On First Change", false),
    ON_ALL_CHANGE("On All Change", false),
    ON_TIMER("On Timer", true),
    ON_TIMER_AND_CHANGE("On Timer+Change", true),
    CONSTANT("Constant", false),
    HISTORICAL("Historical", false),
    BACKFILLING_HISTORICAL("Backfilling", false);
    
    private static String baseKey = "yukon.common.point.updateType.";

    private boolean isIntervalRequired;
    private String calcUpdateType;
    
    private CalcUpdateType(String calcUpdateType, boolean isIntervalRequired) {
        this.calcUpdateType = calcUpdateType;
        this.isIntervalRequired = isIntervalRequired;
    }
    
    private static final Logger log = YukonLogManager.getLogger(CalcUpdateType.class);
    private final static ImmutableMap<String, CalcUpdateType> lookupByCalcUpdateType;
    static {
        try {
            ImmutableMap.Builder<String, CalcUpdateType> calcUpdateTypeBuilder = ImmutableMap.builder();
            for (CalcUpdateType calcUpdateType : values()) {
                calcUpdateTypeBuilder.put(calcUpdateType.getCalcUpdateType(), calcUpdateType);
            }
            lookupByCalcUpdateType = calcUpdateTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name.", e);
            throw e;
        }
    }

    public static CalcUpdateType getCalcUpdateType(String calcUpdateType) {
        CalcUpdateType updateType = lookupByCalcUpdateType.get(calcUpdateType);
        checkArgument(updateType != null, updateType);
        return updateType;
    }
    
    public boolean isIntervalRequired() {
        return isIntervalRequired;
    }
    
    public String getCalcUpdateType() {
        return calcUpdateType;
    }
    
    public String getDatabaseRepresentation() {
        return getCalcUpdateType();
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
