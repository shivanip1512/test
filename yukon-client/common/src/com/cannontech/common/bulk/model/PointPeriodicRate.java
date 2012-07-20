package com.cannontech.common.bulk.model;

import org.joda.time.Duration;

/**
 * List of possible values for periodic rate for point import.
 */
public enum PointPeriodicRate {
    ONE_SECOND(Duration.standardSeconds(1)),
    TWO_SECOND(Duration.standardSeconds(2)),
    FIVE_SECOND(Duration.standardSeconds(5)),
    TEN_SECOND(Duration.standardSeconds(10)),
    FIFTEEN_SECOND(Duration.standardSeconds(15)),
    THIRTY_SECOND(Duration.standardSeconds(30)),
    ONE_MINUTE(Duration.standardMinutes(1)),
    TWO_MINUTE(Duration.standardMinutes(2)),
    THREE_MINUTE(Duration.standardMinutes(3)),
    FIVE_MINUTE(Duration.standardMinutes(5)),
    TEN_MINUTE(Duration.standardMinutes(10)),
    FIFTEEN_MINUTE(Duration.standardMinutes(15)),
    THIRTY_MINUTE(Duration.standardMinutes(30)),
    ONE_HOUR(Duration.standardHours(1)),
    TWO_HOUR(Duration.standardHours(2)),
    SIX_HOUR(Duration.standardHours(6)),
    TWELVE_HOUR(Duration.standardHours(12)),
    TWENTY_FOUR_HOUR(Duration.standardDays(1)),
    ;
    
    private Duration duration;
    
    private PointPeriodicRate(Duration duration) {
        this.duration = duration;
    }
    
    public Duration getPeriod() {
        return duration;
    }
    
    public int getSeconds() {
        return (int) duration.getStandardSeconds();
    }
}
