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
public final class WaterReportingInterval implements DeviceConfigurationInputEnumeration {

    @Autowired private DurationFormattingService durationService;

    // 2h, 4h, 6h, 12h, 1d, 2d
    private static final List<Integer> hourIntervals = ImmutableList.of(2 * 60 * 60, 4 * 60 * 60, 6 * 60 * 60, 12 * 60 * 60, 24 * 60 * 60, 2 * 24 * 60 * 60);
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> reportingIntervals = new ArrayList<>();

        for (int interval : hourIntervals) {
            reportingIntervals.add( new InputOption( Integer.toString(interval), 
                durationService.formatDuration(interval, TimeUnit.SECONDS, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return reportingIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterReportingInterval";
    }
}