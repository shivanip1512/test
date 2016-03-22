package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapBankConfig implements DisplayableEnum{
    NONE("None"), 
    WYE("Wye"), 
    DELTA("Delta"), 
    SERIAL("Serial");

    private final String displayName;

    private CapBankConfig(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.capbank.config." + name();
    }

}