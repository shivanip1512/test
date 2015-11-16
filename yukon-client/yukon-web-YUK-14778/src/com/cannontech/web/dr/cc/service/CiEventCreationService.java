package com.cannontech.web.dr.cc.service;

import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.web.dr.cc.model.CiInitEventModel;

/**
 * Service to initate curtailment events.
 */
public interface CiEventCreationService {
    
    /**
     * Create a curtailment event based on the event model object.
     * @return the event id.
     */
    int createEvent(CiInitEventModel event) throws EventCreationException;
    
    /**
     * Split the specified event to remove the specified customers.
     * @return the event id of the newly created "split" event
     */
    int splitEvent(CurtailmentEvent originalEvent, List<CICustomerStub> customersToRemove);
    
    /**
     * Adjust the specified event's parameters.
     */
    void adjustEvent(CurtailmentEvent originalEvent, CiInitEventModel newEventParams);
    
}
