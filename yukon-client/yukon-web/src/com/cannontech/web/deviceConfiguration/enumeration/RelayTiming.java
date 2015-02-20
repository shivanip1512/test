package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class RelayTiming implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.relayTiming";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private YukonMessageSourceResolvable formatDbValue(int dbValue) {
        /*
         * The integer value is written to the database, and represents a relay timing in the following fashion:
         *    1 = 250 ms
         *    2 = 500 ms
         *    4 = 1 s
         *    8 = 2 s 
         * etc.
         */
        if (dbValue % 4 == 0) {
            // If dbValue mod 4 is zero, this value is only seconds with no millis.
            return new YukonMessageSourceResolvable(baseKey + ".seconds", dbValue / 4);
        } else if (dbValue < 4) {
            // There aren't any seconds if the dbValue is less than 4, just millis.
            return new YukonMessageSourceResolvable(baseKey + ".millis", dbValue * 250);
        } else {
            // We have a dbValue representing both seconds and millis.
            int seconds = dbValue / 4;
            int millis = (dbValue % 4) * 250;
            return new YukonMessageSourceResolvable(baseKey + ".both", seconds, millis);
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> validTimings = new ArrayList<>();

        for (int dbValue = 1; dbValue <= 50; dbValue++) {
            validTimings.add( new InputOption( Integer.toString(dbValue), messageAccessor.getMessage(formatDbValue(dbValue))));
        }
        return validTimings;
    }

    @Override
    public String getEnumOptionName() {
        return "RelayTiming";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.CHOSEN;
    }
}