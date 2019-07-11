package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum TriggerActiveFlag implements DisplayableEnum, DatabaseRepresentationSource {
    TRUE('T'),
    FALSE('F');

    private String baseKey = "yukon.web.modules.dr.setup.controlArea.";

    private final Character activeFlag;

    private TriggerActiveFlag(Character activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Character getActiveFlagValue() {
        return activeFlag;
    }

    public static TriggerActiveFlag getDisplayValue(Character value) {
        for (TriggerActiveFlag triggerActiveFlag : TriggerActiveFlag.values()) {
            if (triggerActiveFlag.getActiveFlagValue().equals(value)) {
                return triggerActiveFlag;
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
        return activeFlag;
    }
}
