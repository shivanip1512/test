package com.cannontech.stars.dr.account.model;

import java.util.Date;

public class CallReport {

	private Integer callId = null;
	private String callNumber;
	private int callTypeId;
	private Date dateTaken;
	private String takenBy;
	private String description;
	private int accountId;
	
	public Integer getCallId() {
		return callId;
	}
	public void setCallId(Integer callId) {
		this.callId = callId;
	}
	public String getCallNumber() {
		return callNumber;
	}
	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}
	public int getCallTypeId() {
		return callTypeId;
	}
	public void setCallTypeId(int callTypeId) {
		this.callTypeId = callTypeId;
	}
	public Date getDateTaken() {
		return dateTaken;
	}
	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}
	public String getTakenBy() {
		return takenBy;
	}
	public void setTakenBy(String takenBy) {
		this.takenBy = takenBy;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
}
