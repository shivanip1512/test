package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapBankAntennaType implements DisplayableEnum{
    NONE("None"), 
    YAGI("Yagi"), 
    OMNI("Omni");

    private final String displayName;

    private CapBankAntennaType(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.capbank.antennaType." + name();
    }

}