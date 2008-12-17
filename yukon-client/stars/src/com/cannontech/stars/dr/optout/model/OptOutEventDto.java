package com.cannontech.stars.dr.optout.model;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.dr.program.model.Program;

/**
 * Data transfer object which holds information about an opt out event
 */
public class OptOutEventDto {

	private Integer eventId;
	private LiteInventoryBase inventory;
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

	public LiteInventoryBase getInventory() {
		return inventory;
	}

	public void setInventory(LiteInventoryBase inventory) {
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
