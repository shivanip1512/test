package com.cannontech.database.data.point;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

/**
 * This enum doesn't represent any field in the database. As of this time, 
 * only one type of control exists for analog points, so an analog point has 
 * an entry in the PointControl table if it has an AnalogControlType of NORMAL,
 * and has no entry in that table otherwise.
 */
public enum AnalogControlType implements DisplayableEnum, DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal");
    
    private static final String baseKey = "yukon.common.point.analogControlType.";
    
    private final String controlName;
    private static final Logger log = YukonLogManager.getLogger(AnalogControlType.class);
    
    private final static ImmutableMap<String, AnalogControlType> lookupByControlType;
    static {
        try {
            ImmutableMap.Builder<String, AnalogControlType> controlTypeBuilder = ImmutableMap.builder();
            for (AnalogControlType type : values()) {
                controlTypeBuilder.put(type.controlName, type);
            }
            lookupByControlType = controlTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
    }

    public static AnalogControlType getAnalogControlTypeValue(String value) {
        AnalogControlType analogControlType = lookupByControlType.get(value);
        checkArgument(analogControlType != null, analogControlType);
        return analogControlType;
    }

    private AnalogControlType(String controlName) {
        this.controlName = controlName;
    }
    
    public String getControlName() {
        return controlName;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return getControlName();
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
