package com.cannontech.web.deviceConfiguration.enumeration;

import java.text.DecimalFormat;
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

    private YukonMessageSourceResolvable formatTimer(int timer) {
        /*
         * The integer value is written to the database, and represents a relay timing in the following fashion:
         *    250 = 250 ms
         *    500 = 500 ms
         *    1000 = 1 s
         *    2000 = 2 s 
         * etc.
         */
        if (timer % 1000 == 0) {
            // This timer is only seconds with no millis.
            return new YukonMessageSourceResolvable(baseKey + ".seconds", timer / 1000 );
        } else if (timer < 1000) {
            // There aren't any seconds if the dbValue is less than 1000, just millis.
            return new YukonMessageSourceResolvable(baseKey + ".millis", timer );
        } else {
            // We have a dbValue representing both seconds and millis.
            int seconds = timer / 1000;
            int millis = timer % 1000;
            return new YukonMessageSourceResolvable(baseKey + ".both", seconds, millis);
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);
        final DecimalFormat decimalFormatter = new DecimalFormat("0.00");
        List<InputOption> validTimings = new ArrayList<>();

        for (int timer = 250; timer <= 63750; timer += 250) {
            validTimings.add(new InputOption(decimalFormatter.format((double)timer/1000),
                                             messageAccessor.getMessage(formatTimer(timer))));
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