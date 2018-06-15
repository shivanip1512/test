package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

public abstract class Interval implements DeviceConfigurationInputEnumeration {

    @Autowired private DurationFormattingService durationService;

    protected abstract Integer[] getIntervals();
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        return Arrays.stream(getIntervals())
                .map(getInputOptionFactory(userContext, true))
                .collect(Collectors.toList());
    }

    protected Function<Integer, InputOption> getInputOptionFactory(YukonUserContext userContext, boolean enabled) {
        return interval -> 
                new InputOption(Integer.toString(interval), 
                                durationService.formatDuration(interval, getIntervalUnit(), getDurationFormat(), userContext),
                                enabled);
    }

    protected TimeUnit getIntervalUnit() {
        return TimeUnit.MINUTES;
    }

    protected DurationFormat getDurationFormat() {
        return DurationFormat.DHMS_SHORT_REDUCED;
    };
}
