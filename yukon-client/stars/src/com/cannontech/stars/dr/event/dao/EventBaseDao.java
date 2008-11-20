package com.cannontech.stars.dr.event.dao;

import java.util.List;

public interface EventBaseDao {

    /**
     * Method to delete events from EventBase table
     * @param eventIds
     */
    void deleteEvents(List<Integer> eventIds);

}
