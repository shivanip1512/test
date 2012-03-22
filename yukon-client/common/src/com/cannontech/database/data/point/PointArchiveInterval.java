package com.cannontech.database.data.point;

import org.joda.time.Period;

public enum PointArchiveInterval {

	ZERO(Period.seconds(0)),
	ONE_SECOND(Period.seconds(1)),
	TWO_SECOND(Period.seconds(2)),
	FIVE_SECOND(Period.seconds(5)),
	TEN_SECOND(Period.seconds(10)),
	FIFTEEN_SECOND(Period.seconds(15)),
	THIRTY_SECOND(Period.seconds(30)),
	ONE_MINUTE(Period.minutes(1)),
	TWO_MINUTE(Period.minutes(2)),
	THREE_MINUTE(Period.minutes(3)),
	FIVE_MINUTE(Period.minutes(5)),
	TEN_MINUTE(Period.minutes(10)),
	FIFTEEN_MINUTE(Period.minutes(15)),
	THIRTY_MINUTE(Period.minutes(30)),
	ONE_HOUR(Period.hours(1)),
	TWO_HOUR(Period.hours(2)),
	SIX_HOUR(Period.hours(6)),
	TWELVE_HOUR(Period.hours(12)),
	DAILY(Period.days(1)),
	WEEKLY(Period.weeks(1)),
	MONTHLY(Period.days(30));
	
	private int seconds;
	
	PointArchiveInterval(Period period) {
		this.seconds = (int)period.toStandardDuration().getStandardSeconds();
	}
	
    public static PointArchiveInterval getIntervalBySeconds(int seconds) {
        for (PointArchiveInterval value : PointArchiveInterval.values()) {
            if (value.getSeconds() == seconds) {
                return value;
            }
        }
        return ZERO;
    }

	public int getSeconds() {
		return seconds;
	}
}
