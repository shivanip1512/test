package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum EmetconAddressUsage implements DisplayableEnum, DatabaseRepresentationSource {
    GOLD('G'),
    SILVER('S');

    private final Character addressUsage;

    private EmetconAddressUsage(Character addressUsage) {
        this.addressUsage = addressUsage;
    }

    public Character getAddressUsageValue() {
        return addressUsage;
    }

    public static EmetconAddressUsage getDisplayValue(Character value) {
        for (EmetconAddressUsage addressUsage : EmetconAddressUsage.values()) {
            if (addressUsage.getAddressUsageValue().equals(value)) {
                return addressUsage;
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
        return addressUsage;
    }

}
