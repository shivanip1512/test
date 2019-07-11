package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlAreaTriggerType implements DisplayableEnum, DatabaseRepresentationSource {
    THRESHOLD("Threshold"),
    THRESHOLD_POINT("Threshold Point"),
    STATUS("Status");

    private String baseKey = "yukon.web.modules.dr.setup.controlArea.";
    private String triggerType;

    private ControlAreaTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerTypeValue() {
        return triggerType;
    }

    public static ControlAreaTriggerType getDisplayValue(String value) {
        for (ControlAreaTriggerType areaTriggerType : ControlAreaTriggerType.values()) {
            if (areaTriggerType.getTriggerTypeValue().equals(value)) {
                return areaTriggerType;
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
        return triggerType;
    }

}
