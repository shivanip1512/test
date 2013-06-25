package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class DemandResolution implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.demandResolution";
    
    private static final List<DisplayableValue> resolutions;
    
    /**
     * Enum values represent the order of magnitude of the resolution in Wh.
     */
    public enum Resolution {
        ZERO("1.0", ".zero"),
        ONE("10.0", ".one"),
        ;
        
        private final String dbValue; // represented as string because of poor double representation.
        private final String messageKey;
        
        private Resolution(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> resolutionBuilder = new Builder<>();
        
        for (Resolution resolution : Resolution.values()) {
            resolutionBuilder.add(
                new DisplayableValue(
                    resolution.dbValue, new YukonMessageSourceResolvable(baseKey + resolution.messageKey)));
        }
        
        resolutions = resolutionBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "DemandResolution";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return resolutions;
    }
}
