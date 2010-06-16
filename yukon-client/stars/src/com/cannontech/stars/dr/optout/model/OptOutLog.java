package com.cannontech.stars.dr.optout.model;

import org.joda.time.Instant;

/**
 * Model object which represents and opt out log event
 * 
 */
public class OptOutLog {

	private Integer logId;
	private Integer inventoryId;
	private Integer customerAccountId;
	private OptOutAction action;
	private Instant logDate;
	private Instant startDate;
	private Instant stopDate;
	private Integer userId;
	private String username;
	private Integer eventId;
	private OptOutCounts eventCounts;

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer inventoryId) {
		this.inventoryId = inventoryId;
	}

	public Integer getCustomerAccountId() {
		return customerAccountId;
	}

	public void setCustomerAccountId(Integer customerAccountId) {
		this.customerAccountId = customerAccountId;
	}

	public OptOutAction getAction() {
		return action;
	}

	public void setAction(OptOutAction action) {
		this.action = action;
	}

	public Instant getLogDate() {
		return logDate;
	}

	public void setLogDate(Instant logDate) {
		this.logDate = logDate;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getStopDate() {
		return stopDate;
	}

	public void setStopDate(Instant stopDate) {
		this.stopDate = stopDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public OptOutCounts getEventCounts() {
		return eventCounts;
	}

	public void setEventCounts(OptOutCounts eventCounts) {
		this.eventCounts = eventCounts;
	}

}
