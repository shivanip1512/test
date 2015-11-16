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
public final class ReportingInterval implements DeviceConfigurationInputEnumeration {

    private static final List<Integer> minuteIntervals = 
            //  5m, 15m, 30m, 1h, 2h, 4h, 6h, 12h, 1d, 2d
            ImmutableList.of(5, 15, 30, 60, 2*60, 4*60, 6*60, 12*60, 24*60, 2*24*60);

    @Autowired private DurationFormattingService durationService;

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> recordingIntervals = new ArrayList<>();

        for (int interval : minuteIntervals) {
            recordingIntervals.add( new InputOption( Integer.toString(interval),
                durationService.formatDuration(interval, TimeUnit.MINUTES, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return recordingIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "ReportingInterval";
    }
}