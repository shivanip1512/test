package com.cannontech.openadr.dao;

import java.util.Date;

public interface OpenAdrEventDao {
    
    /**
     * Insert a new OpenADR event to the database and remove any events whose end date
     * has passed.
     * @param eventId the id of the event being written
     * @param eventXml the event's xml representation
     * @param endDate the date this event will expire
     */
    public void insertEventAndPurgeExpired(String eventId, String eventXml, Date endDate);
    
    /**
     * Update a previous OpenADR event and remove any events whose end date has passed.
     * @param eventId the id of the event being updated
     * @param eventXml the event's xml representation
     * @param endDate the date this event will expire
     */
    public void updateEventAndPurgeExpired(String eventId, String eventXml, Date endDate);
    
    /**
     * Delete a specific event from the database. Mainly used when an event has been cancelled.
     * @param eventId the eventId of the event being deleted.
     */
    public void deleteEvent(String eventId);
    
    /**
     * Retrieve the XML representation of an event.
     * @param eventId the eventId of the event whose XML is being retrieved.
     * @return the XML string of the event.
     */
    public String retrieveEventXml(String eventId);
}
