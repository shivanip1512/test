package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;
import org.springframework.stereotype.Component;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class RfnRecordingInterval extends RfnInterval implements DeviceConfigurationInputEnumeration {

    // 5m
    private static final Integer[] highFrequencyIntervals = { 5 };
    // 15m, 30m, 1h, 2h, 4h
    private static final Integer[] minuteIntervals = { 15, 30, 60, 2 * 60, 4 * 60 };
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        return createIntervalInputOptions(userContext, highFrequencyIntervals, minuteIntervals);
    }

    @Override
    public String getEnumOptionName() {
        return "RecordingInterval";
    }
}