package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;
import com.google.common.collect.ImmutableList;

@Component
public class DisconnectLoadLimitConnectDelay implements DeviceConfigurationInputEnumeration {

    private static final List<Integer> values = ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Autowired DurationFormattingService durationService;

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> validDelayValues = new ArrayList<>();

        for (int value : values) {
            validDelayValues.add( new InputOption( Integer.toString(value), 
                durationService.formatDuration(value, TimeUnit.MINUTES, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return validDelayValues;
    }

    @Override
    public String getEnumOptionName() {
        return "DisconnectLoadLimitConnectDelay";
    }
}