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

    // 2h, 4h, 6h, 12h, 24h == 1d, 48h == 2d
    private static final List<Integer> hourIntervals = ImmutableList.of(2, 4, 6, 12, 24, 48);
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> reportingIntervals = new ArrayList<>();

        for (int interval : hourIntervals) {
        	int intervalAsSeconds = interval * 60 * 60;
            reportingIntervals.add( new InputOption( Integer.toString(intervalAsSeconds), 
                durationService.formatDuration(intervalAsSeconds, TimeUnit.SECONDS, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return reportingIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterReportingInterval";
    }
}