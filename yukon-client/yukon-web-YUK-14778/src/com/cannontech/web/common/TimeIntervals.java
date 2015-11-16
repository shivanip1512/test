package com.cannontech.web.common;

import java.util.Set;

import org.joda.time.Duration;

import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.point.Point;
import com.google.common.collect.ImmutableSet;

public enum TimeIntervals {
    NONE(Duration.ZERO),
    SECONDS_1(Duration.standardSeconds(1)),
    SECONDS_2(Duration.standardSeconds(2)),
    SECONDS_5(Duration.standardSeconds(5)),
    SECONDS_10(Duration.standardSeconds(10)),
    SECONDS_15(Duration.standardSeconds(15)),
    SECONDS_30(Duration.standardSeconds(30)),
    MINUTES_1(Duration.standardMinutes(1)),
    MINUTES_2(Duration.standardMinutes(2)),
    MINUTES_3(Duration.standardMinutes(3)),
    MINUTES_4(Duration.standardMinutes(4)),
    MINUTES_5(Duration.standardMinutes(5)),
    MINUTES_6(Duration.standardMinutes(6)),
    MINUTES_7(Duration.standardMinutes(7)),
    MINUTES_8(Duration.standardMinutes(8)),
    MINUTES_9(Duration.standardMinutes(9)),
    MINUTES_10(Duration.standardMinutes(10)),
    MINUTES_11(Duration.standardMinutes(11)),
    MINUTES_12(Duration.standardMinutes(12)),
    MINUTES_13(Duration.standardMinutes(13)),
    MINUTES_14(Duration.standardMinutes(14)),
    MINUTES_15(Duration.standardMinutes(15)),
    MINUTES_20(Duration.standardMinutes(20)),
    MINUTES_25(Duration.standardMinutes(25)),
    MINUTES_30(Duration.standardMinutes(30)),
    HOURS_1(Duration.standardHours(1)),
    HOURS_2(Duration.standardHours(2)),
    HOURS_6(Duration.standardHours(6)),
    HOURS_12(Duration.standardHours(12)),
    DAYS_1(Duration.standardDays(1)), 
    ;

    private Duration duration;

    TimeIntervals(Duration duration) {
        this.duration = duration;
    }

    public int getSeconds() {
        return duration.toStandardSeconds().getSeconds();
    }

    public Duration getDuration() {
        return duration;
    }

    public static TimeIntervals fromSeconds(int seconds) {
        for (TimeIntervals interval : values()) {
            if (interval.getSeconds() == seconds)
                return interval;
        }
        return null;
    }

    private static final Set<TimeIntervals> analysisIntervals = ImmutableSet.of(
        NONE,
        SECONDS_1,
        SECONDS_2,
        SECONDS_5,
        SECONDS_10,
        SECONDS_15,
        SECONDS_30,
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_4,
        MINUTES_5,
        MINUTES_7,
        MINUTES_10,
        MINUTES_12,
        MINUTES_15,
        MINUTES_20,
        MINUTES_25,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1);

    /**
     * Used for {@link CapControlStrategy#getControlInterval()},
     * {@link CapControlStrategy#getMinResponseTime()} {@link CapControlStrategy#getControlDelayTime()}.
     */
    public static Set<TimeIntervals> getAnalysisIntervals() {
        return analysisIntervals;
    }

    private static final Set<TimeIntervals> integrateIntervals = ImmutableSet.of(
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_4,
        MINUTES_5,
        MINUTES_6,
        MINUTES_7,
        MINUTES_8,
        MINUTES_9,
        MINUTES_10,
        MINUTES_11,
        MINUTES_12,
        MINUTES_13,
        MINUTES_14,
        MINUTES_15);

    /**
     * Used for {@link CapControlStrategy#getIntegratePeriod()}
     */
    public static Set<TimeIntervals> getIntegrateIntervals() {
        return integrateIntervals;
    }

    private static final Set<TimeIntervals> archiveIntervals = ImmutableSet.of(
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_4,
        MINUTES_5,
        MINUTES_7,
        MINUTES_10,
        MINUTES_12,
        MINUTES_15,
        MINUTES_20,
        MINUTES_25,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1);

    /**
     * Used for {@link Point#getArchiveInterval()}
     */
    public static Set<TimeIntervals> getArchiveIntervals() {
        return archiveIntervals;
    }

    private static final Set<TimeIntervals> capControlIntervals = ImmutableSet.of(
        SECONDS_1,
        SECONDS_2,
        SECONDS_5,
        SECONDS_10,
        SECONDS_15,
        SECONDS_30,
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_4,
        MINUTES_5,
        MINUTES_7,
        MINUTES_10,
        MINUTES_12,
        MINUTES_15,
        MINUTES_20,
        MINUTES_25,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1);
    
    /**
     * Used forCapControl CBC Setup
     */
    public static Set<TimeIntervals> getCapControlIntervals() {
        return capControlIntervals;
    }
}
