package com.cannontech.core.schedule.model;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class PaoSchedule {

    private Integer id;
    private Instant nextRunTime;
    private Instant lastRunTime;
    private int repeatSeconds;
    private String name;
    private boolean disabled;

    public final Integer getId() {
        return id;
    }
    public final void setId(Integer id) {
        this.id = id;
    }
    public final Instant getNextRunTime() {
        return nextRunTime;
    }
    public final void setNextRunTime(Instant nextRunTime) {
        this.nextRunTime = nextRunTime;
    }
    public final Instant getLastRunTime() {
        return lastRunTime;
    }
    public final void setLastRunTime(Instant lastRunTime) {
        this.lastRunTime = lastRunTime;
    }
    public final int getRepeatSeconds() {
        return repeatSeconds;
    }
    public final void setRepeatSeconds(int repeatSeconds) {
        this.repeatSeconds = repeatSeconds;
    }
    public final Duration getRepeatDuration() {
        return Duration.standardSeconds(repeatSeconds);
    }
    public final void setRepeatDuration(Duration repeatDuration) {
        repeatSeconds = (int) repeatDuration.getStandardSeconds();
    }
    public final String getName() {
        return name;
    }
    public final void setName(String name) {
        this.name = name;
    }
    public final boolean isDisabled() {
        return disabled;
    }
    public final void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public enum ScheduleInterval {
        NONE(Duration.ZERO),
        FIVE_MINUTES(Duration.standardMinutes(5)),
        SEVEN_MINUTES(Duration.standardMinutes(7)),
        TEN_MINUTES(Duration.standardMinutes(10)),
        TWELEVE_MINUTES(Duration.standardMinutes(12)),
        FIFTEEN_MINUTES(Duration.standardMinutes(15)),
        TWENTY_MINUTES(Duration.standardMinutes(20)),
        TWENTY_FIVE_MINUTES(Duration.standardMinutes(25)),
        THIRTY_MINUTES(Duration.standardMinutes(30)),
        ONE_HOUR(Duration.standardHours(1)),
        TWO_HOURS(Duration.standardHours(2)),
        SIX_HOURS(Duration.standardHours(6)),
        TWELVE_HOURS(Duration.standardHours(12)),
        ONE_DAY(Duration.standardDays(1)),
        TWO_DAYS(Duration.standardDays(2)),
        FIVE_DAYS(Duration.standardDays(5)),
        SEVEN_DAYS(Duration.standardDays(7)),
        FOURTEEN_DAYS(Duration.standardDays(14)),
        THIRTY_DAYS(Duration.standardDays(30)),
        ;

        private Duration duration;

        private ScheduleInterval(Duration duration) {
            this.duration = duration;
        }

        public int getSeconds() {
            return duration.toStandardSeconds().getSeconds();
        }

        public Duration getDuration() {
            return duration;
        }

        public static ScheduleInterval fromSeconds(int seconds) {
            for (ScheduleInterval interval : values()) {
                if (interval.getSeconds() == seconds) return interval;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "[Schedule " + id + ": " + name + "]" ;
    }
}