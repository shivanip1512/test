package com.cannontech.loadcontrol.service.data;

import java.util.Date;

public class OverrideHistory {

	private int serialNumber;
	private String programName;
	private String accountNumber;
	private OverrideStatus status;
	private Date scheduledDate;
	private Date startDate;
	private Date stopDate;
	private String userName;
	private long overrideNumber;
	private boolean countedAgainstLimit;
	
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
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
	
}
