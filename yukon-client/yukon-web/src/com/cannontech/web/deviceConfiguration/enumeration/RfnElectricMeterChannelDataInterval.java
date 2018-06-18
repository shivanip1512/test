package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

public abstract class RfnElectricMeterChannelDataInterval extends RfnChannelDataInterval {

    @Autowired public ConfigurationSource configurationSource;
    
    protected abstract Integer[] getHighFrequencyIntervals();

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

}