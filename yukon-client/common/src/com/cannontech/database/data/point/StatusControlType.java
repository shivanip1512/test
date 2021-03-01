package com.cannontech.database.data.point;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

/**
 * This enum represents the ControlType field of the PointStatusControl table.
 */
public enum StatusControlType implements DisplayableEnum, DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal"),
    LATCH("Latch"),
    PSEUDO("Pseudo"),
    SBOLATCH("SBO Latch"),
    SBOPULSE("SBO Pulse");
    
    private static final Logger log = YukonLogManager.getLogger(StatusControlType.class);
    private static final String baseKey = "yukon.common.point.statusControlType.";
    
    private final String controlName;

    private final static ImmutableMap<String, StatusControlType> lookupByControlTypeName;
    static {
        try {
            ImmutableMap.Builder<String, StatusControlType> controlTypeBuilder = ImmutableMap.builder();
            for (StatusControlType controlType : values()) {
                controlTypeBuilder.put(controlType.getControlName(), controlType);
            }
            lookupByControlTypeName = controlTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name.", e);
            throw e;
        }
    }

    public static StatusControlType getStatusControlType(String controlName) {
        StatusControlType controlType = lookupByControlTypeName.get(controlName);
        checkArgument(controlType != null, controlType);
        return controlType;
    }
    
    private StatusControlType(String controlName) {
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
