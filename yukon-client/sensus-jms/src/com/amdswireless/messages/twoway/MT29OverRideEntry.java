package com.amdswireless.messages.twoway;

import java.util.Date;

public class MT29OverRideEntry {
	private int entryNumber = 1;
	private boolean isValid=false;
	private int overRideTier=0;
	private boolean applyToSummation=false;
	private boolean applyToDemand=false;
	private Date startTime = new Date();
	private Date endTime = new Date();
	
	public void processTierInfoByte( int i ) {
		setValid( ( (i & 0x01) == 0x01 ));
		setOverRideTier( (i & 0xE ) >> 1 );
		setApplyToSummation( ( i & 0x10 ) == 0x10 );
		setApplyToDemand( ( i & 0x20 ) == 0x20 );		
	}
	
	public boolean isApplyToDemand() {
		return applyToDemand;
	}
	public void setApplyToDemand(boolean applyToDemand) {
		this.applyToDemand = applyToDemand;
	}
	public boolean isApplyToSummation() {
		return applyToSummation;
	}
	public void setApplyToSummation(boolean applyToSummation) {
		this.applyToSummation = applyToSummation;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public int getOverRideTier() {
		return overRideTier;
	}
	public void setOverRideTier(int overRideTier) {
		this.overRideTier = overRideTier;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(int entryNumber) {
		this.entryNumber = entryNumber;
	}
}
