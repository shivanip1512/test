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
public final class WaterRecordingInterval implements DeviceConfigurationInputEnumeration {

    @Autowired private DurationFormattingService durationService;

    // 15m, 30m, 1h, 2h, 4h
    private static final List<Integer> minuteIntervals = ImmutableList.of(15 * 60, 30 * 60, 60 * 60, 2 * 60 * 60, 4 * 60 * 60);
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> recordingIntervals = new ArrayList<>();

        for (int interval : minuteIntervals) {
            recordingIntervals.add( new InputOption( Integer.toString(interval), 
                durationService.formatDuration(interval, TimeUnit.SECONDS, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return recordingIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterRecordingInterval";
    }
}