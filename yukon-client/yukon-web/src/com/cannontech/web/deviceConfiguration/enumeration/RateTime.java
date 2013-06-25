package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public class RateTime implements DeviceConfigurationInputEnumeration {
    
    private final List<DisplayableValue> validTimes;
    
    public RateTime() {
        Builder<DisplayableValue> builder = new Builder<>();
        
        /*
         * This loop builds up the String representation of the TOU Times as an offset into the day 
         * in 5-minute intervals starting with 00:00 and incrementing to 23:55.
         */
        for (int minutes = 0; minutes < (60 * 24); minutes += 5) {
            int hour = minutes / 60;
            int minute = minutes % 60;
            
            String display = "";
            if (hour < 10) {
                display += "0";
            }
                
            display += Integer.toString(hour) + ":";
            
            if (minute < 10) {
                display += "0";
            } 
            
            display += Integer.toString(minute);
            
            builder.add(new DisplayableValue(display, YukonMessageSourceResolvable.createDefaultWithoutCode(display)));
        }
        
        validTimes = builder.build();
    }
    
    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return validTimes;
    }
    
    @Override
    public String getEnumOptionName() {
        return "RateTime";
    }
}
