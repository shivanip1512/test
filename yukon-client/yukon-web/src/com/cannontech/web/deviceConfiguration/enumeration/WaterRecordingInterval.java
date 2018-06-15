package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public final class WaterRecordingInterval extends RfnChannelDataInterval {

    // 15m, 30m, 60m == 1h, 120m == 2h, 240m == 4h
    private static final Integer[] minuteIntervals = { 15, 30, 60, 120, 240 };
    private static final Integer[] secondsIntervals = minutesToSeconds(minuteIntervals);
    
    private static Integer[] minutesToSeconds(Integer[] minuteIntervals) {
        return Arrays.stream(minuteIntervals)
            .mapToInt(minutes -> minutes * 60)
            .boxed()
            .toArray(Integer[]::new);
    }
    
    @Override
    protected Integer[] getIntervals() {
        return secondsIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "WaterRecordingInterval";
    }

    @Override
    protected TimeUnit getIntervalUnit() {
        return TimeUnit.SECONDS;
    }
}