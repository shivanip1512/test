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

    private YukonMessageSourceResolvable formatTimer(double timer) {
        /*
         * The integer value is written to the database, and represents a relay timing in the following fashion:
         *    .250 = 250 ms
         *    .500 = 500 ms
         *    1.000 = 1 s
         *    2.000 = 2 s 
         * etc.
         */
        if (timer == (int)timer) {
            // This timer is only seconds with no millis.
            return new YukonMessageSourceResolvable(baseKey + ".seconds", (int)timer );
        } else if (timer < 1.0) {
            // There aren't any seconds if the dbValue is less than 1000, just millis.
            return new YukonMessageSourceResolvable(baseKey + ".millis", timer * 1000 );
        } else {
            // We have a dbValue representing both seconds and millis.
            double millis = timer%1;
            double seconds = timer-millis;
            return new YukonMessageSourceResolvable(baseKey + ".both", seconds, millis*1000);
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);
        final DecimalFormat decimalFormatter = new DecimalFormat("0.00");
        List<InputOption> validTimings = new ArrayList<>();

        for (double timer = .250; timer <= 63.750; timer += .250) {
            validTimings.add(new InputOption(decimalFormatter.format(timer),
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