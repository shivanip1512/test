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
public class VoltageAveragingInterval implements DeviceConfigurationInputEnumeration {
    
    @Autowired private DurationFormattingService durationService;
    
    //15sec, 30sec, 45sec, 60sec, 90sec, 120sec, 180sec, 5 min, 10 min, 15 min
    private static final List<Integer> values = ImmutableList.of(15, 30, 45, 60, 90, 120, 180, 300, 600, 900);
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> readIntervals = new ArrayList<>();

        for (Integer value : values) {
            if (value <= 180) {
                readIntervals.add(new InputOption(Integer.toString(value), durationService.formatDuration(value,
                    TimeUnit.SECONDS, DurationFormat.S, userContext)));
            }else{
                readIntervals.add(new InputOption(Integer.toString(value), durationService.formatDuration(value,
                    TimeUnit.SECONDS, DurationFormat.M, userContext)));
            }
        }

        return readIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "VoltageAveragingInterval";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.CHOSEN;
    }
}