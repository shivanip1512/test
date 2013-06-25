package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class RelayTiming implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.relayTiming";

    private static final List<DisplayableValue> validTimings;
    
    static {
        Builder<DisplayableValue> timingsBuilder = new Builder<>();
        
        MessageSourceResolvable resolvable;
        /*
         * The integer value is written to the database, and represents a relay timing in the following fashion:
         *    1 = 250 ms
         *    2 = 500 ms
         *    4 = 1 s
         *    8 = 2 s 
         * etc.
         */
        for (int dbValue = 1; dbValue <= 50; dbValue++) {
            if (dbValue % 4 == 0) {
                // If dbValue mod 4 is zero, this value is only seconds with no millis.
                resolvable = new YukonMessageSourceResolvable(baseKey + ".seconds", dbValue / 4);
            } else if (dbValue < 4) {
                // There aren't any seconds if the dbValue is less than 4, just millis.
                resolvable = new YukonMessageSourceResolvable(baseKey + ".millis", dbValue * 250);
            } else {
                // We have a dbValue representing both seconds and millis.
                int seconds = dbValue / 4;
                int millis = (dbValue % 4) * 250;
                resolvable = new YukonMessageSourceResolvable(baseKey + ".both", seconds, millis);
            }
            
            timingsBuilder.add(new DisplayableValue(Integer.toString(dbValue), resolvable));
        }
        
        validTimings = timingsBuilder.build();
    }
    
    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return validTimings;
    }
    
    @Override
    public String getEnumOptionName() {
        return "RelayTiming";
    }
}
