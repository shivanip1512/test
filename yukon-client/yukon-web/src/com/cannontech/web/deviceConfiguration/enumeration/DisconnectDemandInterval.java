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
public class DisconnectDemandInterval implements DeviceConfigurationInputEnumeration {

    private static final List<Integer> intervals = ImmutableList.of(5, 10, 15);

    @Autowired private DurationFormattingService durationService;

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> validIntervals = new ArrayList<>();

        for (int value : intervals) {
            validIntervals.add( new InputOption( Integer.toString(value), 
                durationService.formatDuration(value, TimeUnit.MINUTES, DurationFormat.DHMS_SHORT_REDUCED, userContext)));
        }

        return validIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "DisconnectDemandInterval";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}