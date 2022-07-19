package com.cannontech.dr.eatonCloud.job.service.impl;

import java.math.RoundingMode;

import org.joda.time.Minutes;

import com.google.common.math.IntMath;

public class EatonCloudJobSettingsHelper {
    //TODO: change to 5
    public static final Minutes pollInMinutes = Minutes.minutes(5);
    
    public static final Minutes firstRetryAfterPollMinutes = Minutes.TWO;
    
    public static Minutes getReadTime(EventSummary summary) {
        int readTimeFromNowInMinutes = summary.getCommand().getDutyCyclePeriod() == null ? 5 : IntMath.divide(
                summary.getCommand().getDutyCyclePeriod() / 60,
                2, RoundingMode.CEILING);
        return Minutes.minutes(readTimeFromNowInMinutes);
    }
}
