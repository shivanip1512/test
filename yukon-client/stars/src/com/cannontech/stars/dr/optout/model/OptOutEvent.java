package com.cannontech.stars.dr.optout.model;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Instant;

/**
 * Model object which represents an Opt out event
 */
public class OptOutEvent {

	private Integer eventId;
	private Integer customerAccountId;
	private Integer inventoryId;
	private Instant scheduledDate;
	private Instant startDate;
	private Instant stopDate;
	private OptOutCounts eventCounts;
	private OptOutEventState state;

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Integer getCustomerAccountId() {
		return customerAccountId;
	}

	public void setCustomerAccountId(Integer customerAccountId) {
		this.customerAccountId = customerAccountId;
	}

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer inventoryId) {
		this.inventoryId = inventoryId;
	}

	public Instant getScheduledDate() {
		return scheduledDate;
	}
	
	public void setScheduledDate(Instant scheduledDate) {
		this.scheduledDate = scheduledDate;
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

	public OptOutCounts getEventCounts() {
		return eventCounts;
	}

	public void setEventCounts(OptOutCounts eventCounts) {
		this.eventCounts = eventCounts;
	}

	public OptOutEventState getState() {
		return state;
	}

	public void setState(OptOutEventState state) {
		this.state = state;
	}
	
	public Integer getDurationInHours() {
		
		if(startDate == null || stopDate == null) {
			return null;
		}

		// Use Joda to get correct duration in hours
		Period period = new Period(startDate, stopDate, PeriodType.time());
		
		return period.get(DurationFieldType.hours());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((customerAccountId == null) ? 0 : customerAccountId
						.hashCode());
		result = prime * result
				+ ((eventCounts == null) ? 0 : eventCounts.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result
				+ ((inventoryId == null) ? 0 : inventoryId.hashCode());
		result = prime * result
				+ ((scheduledDate == null) ? 0 : scheduledDate.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((stopDate == null) ? 0 : stopDate.hashCode());
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
		OptOutEvent other = (OptOutEvent) obj;
		if (customerAccountId == null) {
			if (other.customerAccountId != null)
				return false;
		} else if (!customerAccountId.equals(other.customerAccountId))
			return false;
		if (eventCounts == null) {
			if (other.eventCounts != null)
				return false;
		} else if (!eventCounts.equals(other.eventCounts))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (inventoryId == null) {
			if (other.inventoryId != null)
				return false;
		} else if (!inventoryId.equals(other.inventoryId))
			return false;
		if (scheduledDate == null) {
			if (other.scheduledDate != null)
				return false;
		} else if (!scheduledDate.equals(other.scheduledDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (stopDate == null) {
			if (other.stopDate != null)
				return false;
		} else if (!stopDate.equals(other.stopDate))
			return false;
		return true;
	}
	
	

}
