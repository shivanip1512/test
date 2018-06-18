package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class WaterReportingInterval extends RfnAuxiliaryMeterChannelDataInterval {

    private static final Integer[] hourIntervals = { 2, 4, 6, 12, 24, 48 };
    private static final Integer[] secondIntervals = hoursToSeconds(hourIntervals);
    
    @Override
    protected Integer[] getIntervals() {
        return secondIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterReportingInterval";
    }
}