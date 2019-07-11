package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum EmetconRelayUsage implements DisplayableEnum, DatabaseRepresentationSource {
    RELAY_A('A'),
    RELAY_B('B'),
    RELAY_C('C'),
    RELAY_ALL('S');

    private final Character relayUsage;

    private EmetconRelayUsage(Character relayUsage) {
        this.relayUsage = relayUsage;
    }

    public Character getRelayUsageValue() {
        return relayUsage;
    }

    public static EmetconRelayUsage getDisplayValue(Character value) {
        for (EmetconRelayUsage relayUsage : EmetconRelayUsage.values()) {
            if (relayUsage.getRelayUsageValue().equals(value)) {
                return relayUsage;
            }
        }
        return null;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return relayUsage;
    }

}