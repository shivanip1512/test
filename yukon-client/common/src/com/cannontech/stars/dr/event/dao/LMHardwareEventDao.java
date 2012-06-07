package com.cannontech.stars.dr.event.dao;

import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;

public interface LMHardwareEventDao {

    public LiteLMHardwareEvent getById(int id) throws DataRetrievalFailureException;

    /**
     * Method to get all hardware events by inventory id (sorted newest to
     * oldest)
     * @param inventoryId - Id of inventory to get events for
     * @return Sorted events
     */
    public List<LiteLMHardwareEvent> getByInventoryId(int inventoryId);

    /**
     * Method to get all hardware events by inventory id and actionId (sorted
     * newest to oldest)
     * @param inventoryId - Id of inventory to get events for
     * @param actionId - ex., install actionId etc
     * @return Sorted events
     */
    public List<LiteLMHardwareEvent> getByInventoryAndActionId(int inventoryId,
            int actionId);

    /**
     * Method to delete hardware to meter mappings
     * @param inventoryIds
     */
    public void deleteHardwareToMeterMapping(Integer inventoryId);
    
    public LiteLMHardwareEvent add(LiteLMHardwareEvent lmHwEvent, int energyCompanyId);
    
    public LiteLMHardwareEvent update(LiteLMHardwareEvent lmHwEvent);    
    
    public void deleteAllLMHardwareEvents(Integer inventoryId);    

}
