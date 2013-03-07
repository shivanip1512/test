package com.cannontech.openadr.dao;

import java.util.Date;

import com.cannontech.openadr.model.OffsetEvent;

public interface OpenAdrEventDao {
    
    /**
     * Insert a new OpenADR event to the database and remove any events whose end date
     * has passed.
     * @param eventId the id of the event being written
     * @param eventXml the event's xml representation
     * @param endDate the date this event will expire
     * @param requestId the requestId the event corresponds to
     */
    public void insertEventAndPurgeExpired(String eventId, String eventXml, Date endDate, String requestId);
    
    /**
     * Update a previous OpenADR event and remove any events whose end date has passed.
     * @param eventId the id of the event being updated
     * @param eventXml the event's xml representation
     * @param endDate the date this event will expire
     * @param requestId the requestId the event corresponds to
     */
    public void updateEventAndPurgeExpired(String eventId, String eventXml, Date endDate, String requestId);
    
    /**
     * Delete a specific event from the database. Mainly used when an event has been cancelled.
     * @param eventId the eventId of the event being deleted.
     */
    public void deleteEvent(String eventId);
    
    /**
     * Retrieve the database representation of an event.
     * @param eventId the eventId of the event whose representation is being retrieved.
     * @return the OffsetEvent representing the xml and startoffset for the event.
     */
    public OffsetEvent retrieveOffsetEvent(String eventId);
}
