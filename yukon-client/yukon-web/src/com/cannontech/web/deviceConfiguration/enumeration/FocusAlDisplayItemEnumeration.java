package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class FocusAlDisplayItemEnumeration implements DeviceConfigurationInputEnumeration {
    
    public enum Item {
        SLOT_DISABLED,
        DELIVERED_KWH,
        REVERSE_KWH,
        TOTAL_KWH,
        NET_KWH,
        DIAGNOSTIC_FLAGS,
        ALL_SEGMENTS,
        FIRMWARE_VERSION,
        ;
        
        private final String messageKey;
        
        private Item() {
            this.messageKey = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
        }
    }
    
    private static final String baseKey = "yukon.web.modules.tools.configs.enum.focusAlDisplayItem";
    
    private static final List<DisplayableValue> focusAlDisplayItems;
    
    static {
        Builder<DisplayableValue> displayBuilder = new Builder<>();
        
        for (Item item : Item.values()) {
            displayBuilder.add(new DisplayableValue(item.name(), baseKey + item.messageKey));
        }
        
        focusAlDisplayItems = displayBuilder.build();
    }
            
    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return focusAlDisplayItems;
    }
    
    @Override
    public String getEnumOptionName() {
        return "FocusAlDisplayItem";
    }
}
