package com.cannontech.web.dr.cc.service;

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
    
}
