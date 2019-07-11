package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlAreaProjectionType implements DisplayableEnum, DatabaseRepresentationSource {

    NONE("(none)"), 
    LSF("LSF");

    private String baseKey = "yukon.web.modules.dr.setup.controlArea.";
    private String projectionType;

    private ControlAreaProjectionType(String projectionType) {
        this.projectionType = projectionType;
    }

    public String getProjectionTypeValue() {
        return projectionType;
    }

    public static ControlAreaProjectionType getDisplayValue(String value) {
        for (ControlAreaProjectionType projectionType : ControlAreaProjectionType.values()) {
            if (projectionType.getProjectionTypeValue().equals(value)) {
                return projectionType;
            }
        }
        return null;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return projectionType;
    }
}
