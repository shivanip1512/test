package com.cannontech.stars.dr.optout.model;

import java.util.Date;
import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.OpenInterval;
import com.cannontech.stars.dr.program.model.Program;
import com.google.common.collect.Lists;

public class OverrideHistory {

	private Integer inventoryId;
	private String serialNumber;
	private List<Program> programs = Lists.newArrayList();
	private String accountNumber;
	private OverrideStatus status;
	private Date scheduledDate;
	private Date startDate;
	private Date stopDate;
	private String userName;
	private long overrideNumber;
	private boolean countedAgainstLimit;

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public List<Program> getPrograms() {
		return programs;
	}
	
	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public OverrideStatus getStatus() {
		return status;
	}

	public void setStatus(OverrideStatus status) {
		this.status = status;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getOverrideNumber() {
		return overrideNumber;
	}

	public void setOverrideNumber(long overrideNumber) {
		this.overrideNumber = overrideNumber;
	}

	public boolean isCountedAgainstLimit() {
		return countedAgainstLimit;
	}

	public void setCountedAgainstLimit(boolean countedAgainstLimit) {
		this.countedAgainstLimit = countedAgainstLimit;
	}
	
    /**
     * This returns an instant of the override history start date.
     * This method will return null if the start date is null. 
     */
	public Instant getStartInstant(){
	    if (startDate != null) {
	        return new Instant(startDate);
	    }
	    return null;
	}
	
	/**
	 * This returns an instant of the override history stop date.
	 * This method will return null if the stop date is null. 
	 */
	public Instant getStopInstant(){
	    if (stopDate != null) {
	        return new Instant(stopDate);
	    }
	    return null;
	}

	public OpenInterval getOpenInterval() {
        if (startDate == null && stopDate == null) {
            return OpenInterval.createUnBounded();
        }
        if (startDate == null) {
            return OpenInterval.createOpenStart(stopDate);
        }
        if (stopDate == null) {
            return OpenInterval.createOpenEnd(startDate);
        }
        
        return OpenInterval.createClosed(startDate, stopDate);
    }

	
	public OverrideHistory getACopy() {

		OverrideHistory history = new OverrideHistory();
		history.setAccountNumber(this.getAccountNumber());
		history.setCountedAgainstLimit(this.isCountedAgainstLimit());
		history.setInventoryId(this.getInventoryId());
		history.setOverrideNumber(this.getOverrideNumber());
		history.setPrograms(this.getPrograms());
		history.setScheduledDate(this.getScheduledDate());
		history.setStartDate(this.getStartDate());
		history.setStopDate(this.getStopDate());
		history.setSerialNumber(this.getSerialNumber());
		history.setUserName(this.getUserName());
		history.setStatus(this.getStatus());
		
		return history;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountNumber == null) ? 0 : accountNumber.hashCode());
		result = prime * result + (countedAgainstLimit ? 1231 : 1237);
		result = prime * result
				+ ((inventoryId == null) ? 0 : inventoryId.hashCode());
		result = prime * result
				+ (int) (overrideNumber ^ (overrideNumber >>> 32));
		result = prime * result
				+ ((programs == null) ? 0 : programs.hashCode());
		result = prime * result
				+ ((scheduledDate == null) ? 0 : scheduledDate.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((stopDate == null) ? 0 : stopDate.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OverrideHistory other = (OverrideHistory) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		if (countedAgainstLimit != other.countedAgainstLimit)
			return false;
		if (inventoryId == null) {
			if (other.inventoryId != null)
				return false;
		} else if (!inventoryId.equals(other.inventoryId))
			return false;
		if (overrideNumber != other.overrideNumber)
			return false;
		if (programs == null) {
			if (other.programs != null)
				return false;
		} else if (!programs.equals(other.programs))
			return false;
		if (scheduledDate == null) {
			if (other.scheduledDate != null)
				return false;
		} else if (!scheduledDate.equals(other.scheduledDate))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (stopDate == null) {
			if (other.stopDate != null)
				return false;
		} else if (!stopDate.equals(other.stopDate))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

}
