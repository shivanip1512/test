package com.cannontech.stars.dr.event.model;

import java.util.Date;


public class LMCustomerEventBase {
    private int eventId;
    private int eventTypeId;
    private int actionId;
    private Date eventDateTime;
    private String notes;
    private String authorizedBy;
    
    public int getActionId() {
        return actionId;
    }
    
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }
    
    public String getAuthorizedBy() {
        return authorizedBy;
    }
    
    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }
    
    public Date getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public int getEventTypeId() {
        return eventTypeId;
    }
    
    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + actionId;
        result = PRIME * result + ((authorizedBy == null) ? 0 : authorizedBy.hashCode());
        result = PRIME * result + ((eventDateTime == null) ? 0 : eventDateTime.hashCode());
        result = PRIME * result + eventId;
        result = PRIME * result + eventTypeId;
        result = PRIME * result + ((notes == null) ? 0 : notes.hashCode());
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
        final LMCustomerEventBase other = (LMCustomerEventBase) obj;
        if (actionId != other.actionId)
            return false;
        if (authorizedBy == null) {
            if (other.authorizedBy != null)
                return false;
        } else if (!authorizedBy.equals(other.authorizedBy))
            return false;
        if (eventDateTime == null) {
            if (other.eventDateTime != null)
                return false;
        } else if (!eventDateTime.equals(other.eventDateTime))
            return false;
        if (eventId != other.eventId)
            return false;
        if (eventTypeId != other.eventTypeId)
            return false;
        if (notes == null) {
            if (other.notes != null)
                return false;
        } else if (!notes.equals(other.notes))
            return false;
        return true;
    }

}
