package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public final class WaterReportingInterval extends RfnChannelDataInterval {

    private static final Integer[] hourIntervals = { 2, 4, 6, 12, 24, 48 };
    private static final Integer[] secondIntervals = hoursToSeconds(hourIntervals);
    
    private static Integer[] hoursToSeconds(Integer[] hourIntervals) {
        return Arrays.stream(hourIntervals)
            .mapToInt(hours -> hours * 3600)
            .boxed()
            .toArray(Integer[]::new);
    }
    
    @Override
    protected Integer[] getIntervals() {
        return secondIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterReportingInterval";
    }

    @Override
    protected TimeUnit getIntervalUnit() {
        return TimeUnit.SECONDS;
    }
}