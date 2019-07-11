package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum DailyDefaultState implements DisplayableEnum, DatabaseRepresentationSource {

    NONE("NONE"), 
    ENABLE("Enabled"), 
    DISABLE("Disabled");

    private String baseKey = "yukon.web.modules.dr.setup.controlArea.";

    private final String defaultState;

    private DailyDefaultState(String defaultState) {
        this.defaultState = defaultState;
    }

    public String getDefaultStateValue() {
        return defaultState;
    }

    public static DailyDefaultState getDisplayValue(String value) {
        for (DailyDefaultState defaultSate : DailyDefaultState.values()) {
            if (defaultSate.getDefaultStateValue().equals(value)) {
                return defaultSate;
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
        return defaultState;
    }

}
