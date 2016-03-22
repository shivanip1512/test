package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapBankPotentialTransformer implements DisplayableEnum{
    NONE("None"), 
    DEDICATED("Dedicated"), 
    DEDICATED_A("Dedicated-A"), 
    DEDICATED_B("Dedicated-B"),
    DEDICATED_C("Dedicated-C"),
    SHARED("Shared");

    private final String displayName;

    private CapBankPotentialTransformer(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.capbank.potentialTransformer." + name();
    }

}