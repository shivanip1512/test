package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class ReconnectParameter implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.reconnectType";
    
    private static final List<DisplayableValue> reconnectTypes;
    
    public enum ReconnectType {
        ARM(".arm"),
        IMMEDIATE(".immediate")
        ;
        
        private final String messageKey;
        
        private ReconnectType(String messageKey) {
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> typeBuilder = new Builder<>();
        
        for (ReconnectType configType : ReconnectType.values()) {
            typeBuilder.add(new DisplayableValue(configType.name(), baseKey + configType.messageKey));
        }
        
        reconnectTypes = typeBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "ReconnectParameter";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return reconnectTypes;
    }
}
