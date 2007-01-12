package com.amdswireless.messages.rx;

import java.util.Date;

public class IntervalReading {
	private int reading;
	private Date timestamp;

	public IntervalReading(int reading, Date timestamp) {
		this.reading = reading;
		this.timestamp = timestamp;
	}

	public int getReading() {
		return reading;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setReading(int reading) {
		this.reading = reading;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}

