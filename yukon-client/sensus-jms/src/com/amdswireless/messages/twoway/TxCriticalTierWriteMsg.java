package com.amdswireless.messages.twoway;

import java.util.Date;

public class TxCriticalTierWriteMsg extends TxPadCommand {
	private int entry;
	private Date startTime;
	private Date stopTime;
	private int tier;
	private boolean summationOverride = true;
	private boolean demandOverride = true;
	
	public boolean isDemandOverride() {
		return demandOverride;
	}
	public Date getStartTime() {
		if ( startTime == null ) {
			return new Date();
		} else {
			return startTime;
		}
	}
	public Date getStopTime() {
		if ( stopTime == null ) {
			return new Date();
		} else {
			return stopTime;
		}
	}
	public boolean isSummationOverride() {
		return summationOverride;
	}
	public int getTier() {
		return tier;
	}
	public int getEntry() {
		return entry;
	}
}
