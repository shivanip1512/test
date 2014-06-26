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

@Component
public final class RecordingInterval implements DeviceConfigurationInputEnumeration {

    @Autowired private DurationFormattingService durationService;

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> recordingIntervals = new ArrayList<>();

        //  15m, 30m, 1h, 2h, 4h
        for (int intervalMinutes = 15; intervalMinutes <= 240; intervalMinutes *= 2) {
            recordingIntervals.add( new InputOption( Integer.toString(intervalMinutes), 
                durationService.formatDuration(intervalMinutes, TimeUnit.MINUTES, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return recordingIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "RecordingInterval";
    }
}