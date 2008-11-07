package com.cannontech.stars.dr.event.dao;

import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;

public interface LMHardwareEventDao {

    LiteLMHardwareEvent getById(int id) throws DataRetrievalFailureException;
    
    /**
     * Method to get all hardware events by inventory id (sorted newest to oldest)
     * @param inventoryId - Id of inventory to get events for 
     * @return Sorted events
     */
    List<LiteLMHardwareEvent> getByInventoryId(int inventoryId);

    /**
     * Method to delete hardware to meter mappings
     * @param inventoryIds
     */
    public void deleteHardwareToMeterMapping(Integer inventoryId);
    
}
