package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public abstract class RfnAuxiliaryMeterChannelDataInterval extends RfnChannelDataInterval {

    protected static Integer[] minutesToSeconds(Integer[] minuteIntervals) {
        return Arrays.stream(minuteIntervals)
            .mapToInt(minutes -> minutes * 60)
            .boxed()
            .toArray(Integer[]::new);
    }

    protected static Integer[] hoursToSeconds(Integer[] hourIntervals) {
        return Arrays.stream(hourIntervals)
            .mapToInt(hours -> hours * 3600)
            .boxed()
            .toArray(Integer[]::new);
    }

    @Override
    protected TimeUnit getIntervalUnit() {
        return TimeUnit.SECONDS;
    }

}