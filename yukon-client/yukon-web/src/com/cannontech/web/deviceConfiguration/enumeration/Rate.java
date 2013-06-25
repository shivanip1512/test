package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class Rate implements DeviceConfigurationInputEnumeration {

    private static final List<DisplayableValue> rates;
    
    static {
        Builder<DisplayableValue> rateBuilder = new Builder<>();
        
        for (char rate : new char[] { 'A', 'B', 'C', 'D' }) {
            rateBuilder.add(
                new DisplayableValue(
                    Character.toString(rate), 
                    YukonMessageSourceResolvable.createDefaultWithoutCode(String.valueOf(rate))));
        }
        
        rates = rateBuilder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "Rate";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return rates;
    }
}
