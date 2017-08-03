package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

public abstract class RfnInterval {

    @Autowired private DurationFormattingService durationService;
    @Autowired private ConfigurationSource configurationSource;

    public List<InputOption> createIntervalInputOptions(YukonUserContext userContext, Integer[] highFrequencyIntervals, Integer[] minuteIntervals) {
        boolean highFrequencyEnabled = configurationSource.getBoolean(MasterConfigBoolean.RFN_HIGH_FREQUENCY_RECORDNG_REPORTNG_INTERVALS, false);

        Stream<InputOption> high =
            Arrays.stream(highFrequencyIntervals)
                .map(getInputOptionFactory(userContext, highFrequencyEnabled));

        Stream<InputOption> normal =
            Arrays.stream(minuteIntervals)
                .map(getInputOptionFactory(userContext, true));

        return Stream.concat(high, normal).collect(Collectors.toList());
    }

    private Function<Integer, InputOption> getInputOptionFactory(YukonUserContext userContext, boolean enabled) {
        return interval -> 
                new InputOption(Integer.toString(interval), 
                                durationService.formatDuration(interval, TimeUnit.MINUTES, DurationFormat.DHMS_REDUCED, userContext),
                                enabled);
    };

}
