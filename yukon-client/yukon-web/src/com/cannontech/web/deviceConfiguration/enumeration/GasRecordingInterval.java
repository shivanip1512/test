package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class GasRecordingInterval extends RfnAuxiliaryMeterChannelDataInterval {

    // 15m, 30m, 60m == 1h, 120m == 2h, 240m == 4h
    private static final Integer[] minuteIntervals = { 15, 30, 60, 120, 240 };
    private static final Integer[] secondIntervals = minutesToSeconds(minuteIntervals);
    
    @Override
    protected Integer[] getIntervals() {
        return secondIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "GasRecordingInterval";
    }
}