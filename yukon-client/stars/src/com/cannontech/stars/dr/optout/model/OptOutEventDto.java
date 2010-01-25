package com.cannontech.stars.dr.optout.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.program.model.Program;

/**
 * Data transfer object which holds information about an opt out event
 */
public class OptOutEventDto {

	private Integer eventId;
	private Integer inventoryId;
	private HardwareSummary inventory;
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

	public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public HardwareSummary getInventory() {
		return inventory;
	}

	public void setInventory(HardwareSummary inventory) {
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

    public Comparator<OptOutEventDto> getSorter() {
        return new Comparator<OptOutEventDto>() {

            @Override
            public int compare(OptOutEventDto event1, OptOutEventDto event2) {

                if (event1 == event2) {
                    return 0;
                }
                if (event1 == null || event1.getScheduledDate() == null || event1.getStartDate() == null) {
                    return 1;
                }
                if (event2 == null || event2.getScheduledDate() == null || event2.getStartDate() == null) {
                    return -1;
                }
                Date date1 = (event1.getState() == OptOutEventState.SCHEDULE_CANCELED) ? event1.getScheduledDate()
                        : event1.getStartDate();
                Date date2 = (event2.getState() == OptOutEventState.SCHEDULE_CANCELED) ? event2.getScheduledDate()
                        : event2.getStartDate();
                // sort descending
                int retVal = date1.compareTo(date2) * -1;
                return retVal;
            }
        };
    }
}
