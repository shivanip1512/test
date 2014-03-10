package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class DisconnectMode implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.disconnectMode";
    
    private static final List<DisplayableValue> configTypes;
    
    public enum ConfigurationType {
        ON_DEMAND(".onDemand"),
        DEMAND_THRESHOLD(".demandThreshold"),
        CYCLING(".cycling")
        ;
        
        private final String messageKey;
        
        private ConfigurationType(String messageKey) {
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> typeBuilder = new Builder<>();
        
        for (ConfigurationType configType : ConfigurationType.values()) {
            typeBuilder.add(new DisplayableValue(configType.name(), baseKey + configType.messageKey));
        }
        
        configTypes = typeBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "DisconnectMode";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return configTypes;
    }
}
