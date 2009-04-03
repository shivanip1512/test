package com.cannontech.stars.dr.optout.model;

import java.util.Date;
import java.util.List;

import com.cannontech.stars.dr.hardware.model.LiteHardware;
import com.cannontech.stars.dr.program.model.Program;

/**
 * Data transfer object which holds information about an opt out event
 */
public class OptOutEventDto {

	private Integer eventId;
	private LiteHardware inventory;
	private List<Program> programList;
	private OptOutEventState state;
	private Date scheduledDate;
	private Date startDate;
	private Date stopDate;

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public LiteHardware getInventory() {
		return inventory;
	}

	public void setInventory(LiteHardware inventory) {
		this.inventory = inventory;
	}

	public List<Program> getProgramList() {
		return programList;
	}

	public void setProgramList(List<Program> programList) {
		this.programList = programList;
	}

	public OptOutEventState getState() {
		return state;
	}

	public void setState(OptOutEventState state) {
		this.state = state;
	}
	
	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	
	public Date getScheduledDate() {
		return scheduledDate;
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
}
