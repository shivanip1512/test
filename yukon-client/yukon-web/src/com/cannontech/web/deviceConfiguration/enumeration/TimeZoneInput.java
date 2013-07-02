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
        EST("-5", ".est"),
        CST("-6", ".cst"),
        MST("-7", ".mst"),
        PST("-8", ".pst"),
        ALASKA("-9", ".alaska"),
        HAWAII("-10", ".hawaii"),
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
