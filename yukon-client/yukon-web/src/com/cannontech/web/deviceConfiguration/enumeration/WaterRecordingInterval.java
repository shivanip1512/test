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

    // 15m, 30m, 60m == 1h, 120m == 2h, 240m == 4h
    private static final List<Integer> minuteIntervals = ImmutableList.of(15, 30, 60, 120, 240);
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> recordingIntervals = new ArrayList<>();

        for (int interval : minuteIntervals) {
        	int intervalAsSeconds = interval * 60;
            recordingIntervals.add( new InputOption( Integer.toString(intervalAsSeconds), 
                durationService.formatDuration(intervalAsSeconds, TimeUnit.SECONDS, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return recordingIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterRecordingInterval";
    }
}