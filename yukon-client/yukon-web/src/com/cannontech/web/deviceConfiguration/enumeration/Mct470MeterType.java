package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class Mct470MeterType implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.meterType";
    
    private static final List<DisplayableValue> meterTypes;
    
    public enum Meter {
        CHANNEL_NOT_USED("0", ".channelNotUsed"),
        ELECTRONIC_METER("1", ".electronicMeter"),
        TWO_WIRE_KYZ_FORM_A("2", ".twoWire"),
        THREE_WIRE_KYZ_FORM_B("3", ".threeWire"),
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
        return "Mct470MeterType";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return meterTypes;
    }
}
