package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public class DisconnectLoadLimitConnectDelay implements DeviceConfigurationInputEnumeration {
    
    private final List<DisplayableValue> validDelayValues;
    
    public DisconnectLoadLimitConnectDelay() {
        Builder<DisplayableValue> builder = new Builder<>();
        
        String[] values = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        for (String value : values) {
            builder.add(new DisplayableValue(value, YukonMessageSourceResolvable.createDefaultWithoutCode(value)));
        }
        
        validDelayValues = builder.build();
    }
    
    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return validDelayValues;
    }
    
    @Override
    public String getEnumOptionName() {
        return "DisconnectLoadLimitConnectDelay";
    }
}
