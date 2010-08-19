package com.cannontech.common.events.service;

import java.lang.reflect.Method;
import java.util.List;

import org.joda.time.ReadableInstant;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.MappedEventLog;
import com.cannontech.common.events.service.impl.MethodLogDetail;
import com.cannontech.common.exception.BadConfigurationException;
import com.google.common.collect.ListMultimap;

public interface EventLogService {
    
    /**
     * Produces a MethodLogDetail object that can be cached for the particular method.
     * 
     * @throws BadConfigurationException
     */
    public void setupLoggerForMethod(Method method) throws BadConfigurationException;

    public MethodLogDetail getDetailForMethod(Method method);

    public MethodLogDetail getDetailForEventType(String eventType);
    
    public List<MappedEventLog> findAllByCategories(Iterable<EventCategory> eventCategory, 
                                                    ReadableInstant startDate,
                                                    ReadableInstant stopDate);

    /**
     * This method builds up a multimap that contains all the event categories
     * in the system, with all the event log types for that given event
     * category as the map values.
     */
    public ListMultimap<EventCategory, String> getEventLogTypeMultiMap();

    
}
