package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class PhysicalChannel implements DeviceConfigurationInputEnumeration {

    private static final List<DisplayableValue> channels;
    
    static {
        Builder<DisplayableValue> channelBuilder = new Builder<>();
        
        for (int channel = 1; channel <= 16; channel++) {
            channelBuilder.add(
                new DisplayableValue(
                    Integer.toString(channel - 1),
                    YukonMessageSourceResolvable.createDefaultWithoutCode(Integer.toString(channel))));
        }
        
        channels = channelBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "PhysicalChannel";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return channels;
    }
}
