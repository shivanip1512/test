package com.cannontech.stars.dr.optout.model;

import java.util.Date;

public class OverrideHistory {

	private Integer inventoryId;
	private String serialNumber;
	private String programName;
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

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
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
	
	public OverrideHistory getACopy() {

		OverrideHistory history = new OverrideHistory();
		history.setAccountNumber(this.getAccountNumber());
		history.setCountedAgainstLimit(this.isCountedAgainstLimit());
		history.setInventoryId(this.getInventoryId());
		history.setOverrideNumber(this.getOverrideNumber());
		history.setProgramName(this.getProgramName());
		history.setScheduledDate(this.getScheduledDate());
		history.setStartDate(this.getStartDate());
		history.setStopDate(this.getStopDate());
		history.setSerialNumber(this.getSerialNumber());
		history.setUserName(this.getUserName());
		history.setStatus(this.getStatus());
		
		
		return history;
	}

}
