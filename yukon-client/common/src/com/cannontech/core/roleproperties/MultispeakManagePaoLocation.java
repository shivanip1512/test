package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakManagePaoLocation implements DisplayableEnum {

    NONE("Auto (Meter Number First)"),
    METER("Auto (Device Name First)"),
    SERVICE_LOCATION("Device Name");
    
    private String label;
    
    MultispeakManagePaoLocation(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.common.multispeakManagePaoLocation." + name();
    }
}