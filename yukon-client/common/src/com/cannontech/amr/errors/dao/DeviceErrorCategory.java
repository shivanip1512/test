package com.cannontech.amr.errors.dao;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DeviceErrorCategory implements DisplayableEnum {
    BULK_COMMUNICATIONS("Bulk Communtications"),
    DATA_VALIDATION("Data Validation"),
    POWERLINE_CARRIER("Powerline Carrier"),
    YUKON_SYSTEM("Yukon System"),
    TRANSMITTER("Transmitter"),
    NETWORK_MANAGER("Network Manager"),
    NA("N/A"), 
    ;

    DeviceErrorCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }
    
    private final String defaultCategory;
    
    public String getDefaultCategory() {
        return defaultCategory;
    }
    @Override
    public String getFormatKey() {
        return "yukon.web.error.category." + name();
    }
}
