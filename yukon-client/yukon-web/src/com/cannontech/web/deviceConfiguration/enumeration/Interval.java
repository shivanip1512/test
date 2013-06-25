package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public class Interval implements DeviceConfigurationInputEnumeration {
    
    private final List<DisplayableValue> validIntervals;
    
    public Interval() {
        Builder<DisplayableValue> intervalBuilder = new Builder<>();
        
        String[] values = new String[]{ "5", "15", "30", "60" };
        for (String value : values) {
            intervalBuilder.add(
               new DisplayableValue(
                   value, YukonMessageSourceResolvable.createDefaultWithoutCode(value)));
        }
        
        validIntervals = intervalBuilder.build();
    }
        
    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return validIntervals;
    }
    
    @Override
    public String getEnumOptionName() {
        return "Interval";
    }
}
