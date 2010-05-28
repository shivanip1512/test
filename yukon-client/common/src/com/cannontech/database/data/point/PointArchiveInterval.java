package com.cannontech.database.data.point;

import org.joda.time.Period;

public enum PointArchiveInterval {

	ZERO(0),
	ONE_SECOND(Period.seconds(1).toStandardDuration().getStandardSeconds()),
	TWO_SECOND(Period.seconds(2).toStandardDuration().getStandardSeconds()),
	FIVE_SECOND(Period.seconds(5).toStandardDuration().getStandardSeconds()),
	TEN_SECOND(Period.seconds(10).toStandardDuration().getStandardSeconds()),
	FIFTEEN_SECOND(Period.seconds(15).toStandardDuration().getStandardSeconds()),
	THIRTY_SECOND(Period.seconds(30).toStandardDuration().getStandardSeconds()),
	ONE_MINUTE(Period.minutes(1).toStandardDuration().getStandardSeconds()),
	TWO_MINUTE(Period.minutes(2).toStandardDuration().getStandardSeconds()),
	THREE_MINUTE(Period.minutes(3).toStandardDuration().getStandardSeconds()),
	FIVE_MINUTE(Period.minutes(5).toStandardDuration().getStandardSeconds()),
	TEN_MINUTE(Period.minutes(10).toStandardDuration().getStandardSeconds()),
	FIFTEEN_MINUTE(Period.minutes(15).toStandardDuration().getStandardSeconds()),
	THIRTY_MINUTE(Period.minutes(30).toStandardDuration().getStandardSeconds()),
	ONE_HOUR(Period.hours(1).toStandardDuration().getStandardSeconds()),
	TWO_HOUR(Period.hours(2).toStandardDuration().getStandardSeconds()),
	SIX_HOUR(Period.hours(6).toStandardDuration().getStandardSeconds()),
	TWELVE_HOUR(Period.hours(12).toStandardDuration().getStandardSeconds()),
	DAILY(Period.days(1).toStandardDuration().getStandardSeconds()),
	WEEKLY(Period.weeks(1).toStandardDuration().getStandardSeconds()),
	MONTHLY(Period.days(30).toStandardDuration().getStandardSeconds());
	
	private int seconds;
	
	PointArchiveInterval(long seconds) {
		this.seconds = (int)seconds;
	}
	
	public int getSeconds() {
		return seconds;
	}
}
