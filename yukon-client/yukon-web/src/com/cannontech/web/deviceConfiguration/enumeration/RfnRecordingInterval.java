package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class RfnRecordingInterval extends RfnChannelDataInterval {

    // 5m
    private static final Integer[] highFrequencyIntervals = { 5 };
    // 15m, 30m, 1h, 2h, 4h
    private static final Integer[] minuteIntervals = { 15, 30, 60, 2 * 60, 4 * 60 };
    
    @Override
    protected Integer[] getHighFrequencyIntervals() {
        return highFrequencyIntervals;
    }
    
    @Override
    protected Integer[] getIntervals() {
        return minuteIntervals;
    }
    
    @Override
    public String getEnumOptionName() {
        return "RecordingInterval";
    }
}