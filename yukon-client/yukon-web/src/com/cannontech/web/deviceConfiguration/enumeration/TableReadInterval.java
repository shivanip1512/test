package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList.Builder;

@Component
public class TableReadInterval implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.tableReadInterval";
    
    private static final List<DisplayableValue> readIntervals;
    
    static {
        Builder<DisplayableValue> intervalBuilder = new Builder<>();
        
        MessageSourceResolvable resolvable;
        for (int dbValue = 1; dbValue <= 30; dbValue++) {
            if (dbValue % 4 == 0) {
                // If dbValue mod 4 is zero, this value is only minutes with no seconds.
                resolvable = new YukonMessageSourceResolvable(baseKey + ".minutes", dbValue / 4 );
            } else if (dbValue < 4) {
                // There aren't any minutes if dbValue is less than 4, just seconds.
                resolvable = new YukonMessageSourceResolvable(baseKey + ".seconds", dbValue * 15);
            } else {
                // We have a dbValue representing both minutes and seconds.
                int minutes = dbValue / 4;
                int seconds = (dbValue % 4) * 15;
                resolvable = new YukonMessageSourceResolvable(baseKey + ".both", minutes, seconds);
            }
            
            intervalBuilder.add(new DisplayableValue(Integer.toString(dbValue), resolvable));
        }
        
        readIntervals = intervalBuilder.build();
    }
    
    
    @Override
    public String getEnumOptionName() {
        return "TableReadInterval";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return readIntervals;
    }
}
