package com.cannontech.stars.dr.event.model;

public class EventWorkOrder {
    private int eventId;
    private int workOrderId;

    public EventWorkOrder(){}
    public EventWorkOrder(int eventId, int workOrderId) {
        this.eventId = eventId;
        this.workOrderId = workOrderId;
    }
    
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getWorkOrderId() {
        return workOrderId;
    }
    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }
    
}