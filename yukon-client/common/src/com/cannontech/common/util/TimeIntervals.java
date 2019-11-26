package com.cannontech.common.util;

import java.util.Set;

import org.joda.time.Duration;

import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.point.Point;
import com.google.common.collect.ImmutableSet;

public enum TimeIntervals {
    NONE(Duration.ZERO),
    RESTORE(Duration.ZERO),
    CONTINUOUS_LATCH(Duration.ZERO),
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
    MINUTES_7_SECONDS_30(Duration.standardSeconds(450)),
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
    MINUTES_35(Duration.standardMinutes(35)),
    MINUTES_40(Duration.standardMinutes(40)),
    MINUTES_45(Duration.standardMinutes(45)),
    MINUTES_50(Duration.standardMinutes(50)),
    MINUTES_55(Duration.standardMinutes(55)),
    MINUTES_60(Duration.standardMinutes(60)),
    HOURS_1(Duration.standardHours(1)),
    HOURS_2(Duration.standardHours(2)),
    HOURS_3(Duration.standardHours(3)),
    HOURS_4(Duration.standardHours(4)),
    HOURS_5(Duration.standardHours(5)),
    HOURS_6(Duration.standardHours(6)),
    HOURS_7(Duration.standardHours(7)),
    HOURS_8(Duration.standardHours(8)),
    HOURS_10(Duration.standardHours(10)),
    HOURS_12(Duration.standardHours(12)),
    HOURS_15(Duration.standardHours(15)),
    HOURS_20(Duration.standardHours(20)),
    HOURS_30(Duration.standardHours(30)),
    HOURS_45(Duration.standardHours(45)),
    DAYS_1(Duration.standardDays(1)), 
    DAYS_7(Duration.standardDays(7)),
    DAYS_30(Duration.standardDays(30)),
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
        SECONDS_1,
        SECONDS_2,
        SECONDS_5,
        SECONDS_10,
        SECONDS_15,
        SECONDS_30,
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_5,
        MINUTES_10,
        MINUTES_15,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1,
        DAYS_7,
        DAYS_30);

    /**
     * Used for {@link Point#getArchiveInterval()}
     */
    public static Set<TimeIntervals> getArchiveIntervals() {
        return archiveIntervals;
    }

    private static final Set<TimeIntervals> updateAndScanRate = ImmutableSet.of(
        SECONDS_1,
        SECONDS_2,
        SECONDS_5,
        SECONDS_10,
        SECONDS_15,
        SECONDS_30,
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_5,
        MINUTES_10,
        MINUTES_15,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1);

    /**
     * Used for {@link Point#getUpdateRate() and RTUs}
     */
    public static Set<TimeIntervals> getUpdateAndScanRate() {
        return updateAndScanRate;
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
    
    private static final Set<TimeIntervals> shedRestoreTimeOptions = ImmutableSet.of(
        MINUTES_1,
        MINUTES_5,
        MINUTES_7,
        MINUTES_10,
        MINUTES_15,
        MINUTES_20,
        MINUTES_30,
        MINUTES_45,
        HOURS_1,
        HOURS_2,
        HOURS_3,
        HOURS_4,
        HOURS_6,
        HOURS_8,
        RESTORE);

    /**
     * Used for Timed Shed or Restore options
     */
    public static Set<TimeIntervals> getShedRestoreTimeOptions() {
        return shedRestoreTimeOptions;
    }
    
    private static final Set<TimeIntervals> altIntervals = ImmutableSet.of(
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
        MINUTES_5,
        MINUTES_10,
        MINUTES_15,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1);
    
    /**
     * Used for RTU Alt Intervals 
     */
    
    public static Set<TimeIntervals> getAltIntervals() {
        return altIntervals;
    }
    
    private static final Set<TimeIntervals> scanIntervals = ImmutableSet.of(
        SECONDS_5,
        SECONDS_10,
        SECONDS_15,
        SECONDS_30,
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_5,
        MINUTES_10,
        MINUTES_15,
        MINUTES_30,
        HOURS_1,
        HOURS_2,
        HOURS_6,
        HOURS_12,
        DAYS_1);
    
    private static final Set<TimeIntervals> rotationShedTime = ImmutableSet.of(
        MINUTES_5,
        MINUTES_7,
        MINUTES_10,
        MINUTES_15,
        MINUTES_20,
        MINUTES_30,
        MINUTES_45,
        HOURS_1,
        HOURS_2,
        HOURS_3,
        HOURS_4,
        HOURS_6,
        HOURS_8);

    /**
     * Used for RTU Intervals for Class 0,1,2,3
     */
    
    public static Set<TimeIntervals> getScanIntervals() {
        return scanIntervals;
    }

    public static Set<TimeIntervals> getRotationshedtime() {
        return rotationShedTime;
    }
    
    private static final Set<TimeIntervals> controlAreaInterval = ImmutableSet.of(
        NONE,
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_4,
        MINUTES_5,
        MINUTES_10,
        MINUTES_15,
        MINUTES_30
        );
    
    public static Set<TimeIntervals> getControlAreaInterval() {
        return controlAreaInterval;
    }
    
    private static final Set<TimeIntervals> projectionAheadDuration = ImmutableSet.of(
        MINUTES_5,
        MINUTES_10,
        MINUTES_15,
        MINUTES_20,
        MINUTES_25,
        MINUTES_30,
        MINUTES_35,
        MINUTES_40,
        MINUTES_45,
        MINUTES_50,
        MINUTES_55,
        HOURS_1
        );
    
    public static Set<TimeIntervals> getProjectionAheadDuration() {
        return projectionAheadDuration;
    }

    private static final Set<TimeIntervals> commandResendRate = ImmutableSet.of(
        NONE,
        MINUTES_1,
        MINUTES_2,
        MINUTES_5,
        MINUTES_8,
        MINUTES_10,
        MINUTES_15,
        MINUTES_20,
        MINUTES_30,
        MINUTES_45,
        HOURS_1,
        HOURS_2,
        HOURS_5,
        HOURS_8,
        HOURS_10,
        HOURS_15,
        HOURS_20,
        HOURS_30,
        HOURS_45);

    /**
     * Used for Command Resend Rate for Gears
     */
    public static Set<TimeIntervals> getCommandResendRate() {
        return commandResendRate;
    }

    private static final Set<TimeIntervals> shedTime = ImmutableSet.of(
        MINUTES_1,
        MINUTES_2,
        MINUTES_3,
        MINUTES_4,
        MINUTES_5,
        MINUTES_6,
        MINUTES_7,
        MINUTES_8,
        MINUTES_10,
        MINUTES_15,
        MINUTES_20,
        MINUTES_30,
        MINUTES_45,
        HOURS_1,
        HOURS_2,
        HOURS_3,
        HOURS_4,
        HOURS_5,
        HOURS_6,
        HOURS_7,
        HOURS_8,
        HOURS_10,
        HOURS_15,
        HOURS_20,
        HOURS_30,
        HOURS_45);

    /**
     * Used for Shed Time for Gears
     */
    public static Set<TimeIntervals> getShedtime() {
        return shedTime;
    }
    
    private static final Set<TimeIntervals> rippleShedTime = ImmutableSet.of(
       NONE,
       MINUTES_7_SECONDS_30,
       MINUTES_15,
       MINUTES_30,
       MINUTES_60
      );
    
    /**
     * Used for Shed Time for ripple load group.
     */
    public static Set<TimeIntervals> getRippleShedtime() {
        return rippleShedTime;
    }

}
