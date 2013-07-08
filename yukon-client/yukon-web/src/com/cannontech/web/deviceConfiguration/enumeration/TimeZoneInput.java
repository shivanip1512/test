package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class TimeZoneInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.timeZone";
    
    private static final List<DisplayableValue> inputs;
    
    public enum Zone {
        EST("-5", ".newYork"),
        CST("-6", ".chicago"),
        MST("-7", ".denver"),
        PST("-8", ".losAngeles"),
        ALASKA("-9", ".anchorage"),
        HAWAII("-10", ".honolulu"),
        ;
        
        private final String dbValue;
        private final String messageKey;
        
        private Zone(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> dstBuilder = new Builder<>();
        
        for (Zone zone : Zone.values()) {
            dstBuilder.add(
                new DisplayableValue(
                    zone.dbValue, new YukonMessageSourceResolvable(baseKey + zone.messageKey)));
        }
        
        inputs = dstBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "TimeZoneInput";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return inputs;
    }
}
