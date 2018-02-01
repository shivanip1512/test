package com.cannontech.amr.errors.dao;

import java.util.Arrays;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.stream.StreamUtils;
import com.google.common.collect.ImmutableMap;

public enum DeviceErrorCategory implements DisplayableEnum {
    // NOTE: Remember to add any new error categories to the top of deviceError.xml

    BULK_COMMUNICATIONS("Bulk Communications"),
    DATA_VALIDATION("Data Validation"),
    POWERLINE_CARRIER("Powerline Carrier"),
    YUKON_SYSTEM("Yukon System"),
    TRANSMITTER("Transmitter"),
    NETWORK_MANAGER("Network Manager"),
    NA("N/A"), 
    ;
    
    private final String defaultCategory;
    
    private final static ImmutableMap<String, DeviceErrorCategory> lookupByName = 
            ImmutableMap.copyOf(
                Arrays.stream(values()).collect(
                    StreamUtils.mapToSelf(DeviceErrorCategory::getDefaultCategory)));
    
    public static DeviceErrorCategory getByName(String name) {
        return lookupByName.getOrDefault(name, null);
     }

    DeviceErrorCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }
    
    public String getDefaultCategory() {
        return defaultCategory;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.error.category." + name();
    }
}
