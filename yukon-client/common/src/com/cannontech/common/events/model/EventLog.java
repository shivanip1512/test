package com.cannontech.common.events.model;

import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class EventLog {
    private Integer eventLogId;
    private EventCategory eventCategory;
    private String eventType;
    private Date dateTime;
    
    private Object[] arguments;

    public Integer getEventLogId() {
        return eventLogId;
    }

    public void setEventLogId(Integer eventLogId) {
        this.eventLogId = eventLogId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
        this.eventCategory = createCategoryForFullType(eventType);
    }

    public static EventCategory createCategoryForFullType(String eventType) {
        String categoryName = eventType.substring(0, eventType.lastIndexOf("."));
        return EventCategory.createCategory(categoryName);
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
    public MessageSourceResolvable getMessageSourceResolvable() {
        // put eventLogId in first argument slot (mostly to make array 1-based)
        Object[] newArguments = new Object[arguments.length + 1];
        newArguments[0] = eventLogId;
        System.arraycopy(arguments, 0, newArguments, 1, arguments.length);
        MessageSourceResolvable result = YukonMessageSourceResolvable.createDefaultWithArguments("yukon.common.events." + getEventType(), Arrays.toString(newArguments), newArguments);
        return result;
    }
    
    @Override
    public String toString() {
        return eventType + " @ " + new DateTime(dateTime) + " " + Arrays.toString(arguments);
    }

}
