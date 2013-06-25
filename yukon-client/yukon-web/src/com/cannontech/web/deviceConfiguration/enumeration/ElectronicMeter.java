package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class ElectronicMeter implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.electronicMeter";
    
    private static final List<DisplayableValue> displayableValues;
    
    public enum MeterType {
        NONE("0", ".none"),
        S4("1", ".s4"),
        ALPHA_A3("2", ".alphaA3"),
        ALPHA_PPLUS("3", ".alphaPPlus"),
        GEKV("4", ".gekv"),
        GEKV2("5", ".gekv2"),
        SENTINAL("6", ".sentinel"),
        DNP("7", ".dnp"),
        GEKV2C("8", ".gekv2c"),
        ;
        
        private final String dbValue;
        private final String messageKey;
        
        private MeterType(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> builder = new Builder<>();
        
        for (MeterType meterType : MeterType.values()) {
            builder.add(
                new DisplayableValue(
                    meterType.dbValue, 
                    new YukonMessageSourceResolvable(baseKey + meterType.messageKey)));
        }
        
        displayableValues = builder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "ElectronicMeter";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return displayableValues;
    }

}
