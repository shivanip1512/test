package com.cannontech.stars.dr.workOrder.model;

import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.stars.dr.event.model.EventBase;
import com.cannontech.user.YukonUserContext;

public class WorkOrderDto {
    private WorkOrderBase workOrderBase;
    private DateTime eventDate = new DateTime();
    
    public WorkOrderDto(){
        this.workOrderBase = new WorkOrderBase();
    }
    public WorkOrderDto(WorkOrderBase workOrderBase, 
                        List<EventBase> eventBases,
                        YukonUserContext userContext) {
        this.workOrderBase = workOrderBase;

        if (eventBases != null && eventBases.size() > 0) {
            this.eventDate = eventBases.get(0).getEventTimestamp().toDateTime(userContext.getJodaTimeZone());
        }
    }

    public WorkOrderBase getWorkOrderBase() {
        return workOrderBase;
    }
    public void setWorkOrderBase(WorkOrderBase workOrderBase) {
        this.workOrderBase = workOrderBase;
    }
    
    public DateTime getEventDate() {
        return eventDate;
    }
    public void setEventDate(DateTime eventDate) {
        this.eventDate = eventDate;
    }
}