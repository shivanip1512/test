package com.cannontech.stars.dr.event.dao;

import java.util.List;

import com.cannontech.stars.dr.event.model.EventBase;

public interface EventBaseDao {

    public void add(EventBase eventBase);
    
    /**
     * Delete Inventory events, given the list of eventIds for the Inventory.
     */
    public void deleteEvents(List<Integer> eventIds);

}
