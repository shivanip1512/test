package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class RfnReportingInterval extends RfnElectricMeterChannelDataInterval {

    //  5m, 15m, 30m, 1h
    private static final Integer[] highFrequencyIntervals = { 5, 15, 30, 60 };
    //  2h, 4h, 6h, 12h, 1d, 2d
    private static final Integer[] minuteIntervals = { 2*60, 4*60, 6*60, 12*60, 24*60, 2*24*60 };

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
        return "ReportingInterval";
    }
}