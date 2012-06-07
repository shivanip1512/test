package com.cannontech.stars.dr.workOrder.model;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.stars.dr.event.model.EventBase;

public class WorkOrderDto {
    private WorkOrderBase workOrderBase;
    private Instant eventDate = new Instant();
    
    public WorkOrderDto(){
        this.workOrderBase = new WorkOrderBase();
    }
    public WorkOrderDto(WorkOrderBase workOrderBase, 
                        List<EventBase> eventBases) {
        this.workOrderBase = workOrderBase;

        if (eventBases != null && eventBases.size() > 0) {
            this.eventDate = new Instant(eventBases.get(0).getEventTimestamp());
        }
    }

    public WorkOrderBase getWorkOrderBase() {
        return workOrderBase;
    }
    public void setWorkOrderBase(WorkOrderBase workOrderBase) {
        this.workOrderBase = workOrderBase;
    }
    
    public Instant getEventDate() {
        return eventDate;
    }
    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }
}