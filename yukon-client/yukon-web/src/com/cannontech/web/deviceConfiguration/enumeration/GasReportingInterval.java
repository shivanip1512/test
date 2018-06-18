package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class GasReportingInterval extends RfnAuxiliaryMeterChannelDataInterval {

    // 2h, 4h, 6h, 12h, 24h == 1d, 48h == 2d
    private static final Integer[] hourIntervals = { 2, 4, 6, 12, 24, 48 };
    private static final Integer[] secondIntervals =  hoursToSeconds(hourIntervals);
    
    @Override
    protected Integer[] getIntervals() {
        return secondIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "GasReportingInterval";
    }
}