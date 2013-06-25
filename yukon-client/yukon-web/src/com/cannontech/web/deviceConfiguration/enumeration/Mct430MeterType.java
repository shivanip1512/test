package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class Mct430MeterType implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.meterType";
    
    private static final List<DisplayableValue> meterTypes;
    
    public enum Meter {
        CHANNEL_NOT_USED("0", ".channelNotUsed"),
        ELECTRONIC_METER("1", ".electronicMeter"),
        ;
        
        private final String dbValue;
        private final String messageKey;
        
        private Meter(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> typeBuilder = new Builder<>();
        
        for (Meter meter : Meter.values()) {
            typeBuilder.add(new DisplayableValue(meter.dbValue, baseKey + meter.messageKey));
        }
        
        meterTypes = typeBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "Mct430MeterType";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return meterTypes;
    }
}
