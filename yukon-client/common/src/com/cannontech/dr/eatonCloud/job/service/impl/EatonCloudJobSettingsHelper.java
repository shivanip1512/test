package com.cannontech.dr.eatonCloud.job.service.impl;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class EatonCloudJobSettingsHelper {
    //TODO: change to 5
    public static final int pollInMinutes = 1;
    
    public static final int firstRetryAfterPollMinutes = 2;
    
    public static int getReadTime(EventSummary summary) {
        /*int readTimeFromNowInMinutes = summary.getCommand().getDutyCyclePeriod() == null ? 5 : IntMath.divide(
                summary.getCommand().getDutyCyclePeriod() / 60,
                2, RoundingMode.CEILING);
        return readTimeFromNowInMinutes;*/
        return 2;
    }
    
}
