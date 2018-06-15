package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

public abstract class RfnChannelDataInterval extends Interval {

    private static final Integer[] NO_INTERVALS = new Integer[0];
    
    @Autowired public ConfigurationSource configurationSource;

    protected Integer[] getHighFrequencyIntervals() {
        return NO_INTERVALS;
    }

    protected abstract Integer[] getIntervals();

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        boolean highFrequencyEnabled = configurationSource.getBoolean(MasterConfigBoolean.RFN_HIGH_FREQUENCY_RECORDNG_REPORTNG_INTERVALS, false);

        Stream<InputOption> high =
            Arrays.stream(getHighFrequencyIntervals())
                .map(getInputOptionFactory(userContext, highFrequencyEnabled));

        Stream<InputOption> normal =
            Arrays.stream(getIntervals())
                .map(getInputOptionFactory(userContext, true));

        return Stream.concat(high, normal).collect(Collectors.toList());
    }

    @Override
    protected DurationFormat getDurationFormat() {
        return DurationFormat.DHMS_REDUCED;
    };
}