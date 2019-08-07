package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import static com.google.common.base.Preconditions.checkArgument;

public enum ControlAreaProjectionType implements DisplayableEnum, DatabaseRepresentationSource {

    NONE("(none)"), 
    LSF("LSF");

    private String projectionType;
    private final static ImmutableMap<String, ControlAreaProjectionType> lookupByProjectionType;
    static {
        try {
            ImmutableMap.Builder<String, ControlAreaProjectionType> projectionTypeBuilder = ImmutableMap.builder();
            for (ControlAreaProjectionType type : values()) {
                projectionTypeBuilder.put(type.projectionType, type);
            }
            lookupByProjectionType = projectionTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private ControlAreaProjectionType(String projectionType) {
        this.projectionType = projectionType;
    }

    public String getProjectionTypeValue() {
        return projectionType;
    }

    public static ControlAreaProjectionType getProjectionValue(String value) {
        ControlAreaProjectionType controlAreaProjectionType = lookupByProjectionType.get(value);
        checkArgument(controlAreaProjectionType != null, controlAreaProjectionType);
        return controlAreaProjectionType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.controlArea.trigger." + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return projectionType;
    }
}
