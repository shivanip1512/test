package com.cannontech.stars.dr.event.dao;

import java.util.List;

public interface EventBaseDao {

    /**
     * Delete Inventory events, given the list of eventIds for the Inventory.
     * @param eventIds
     */
    void deleteEvents(List<Integer> eventIds);

}
