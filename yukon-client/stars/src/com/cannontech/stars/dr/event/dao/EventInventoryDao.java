package com.cannontech.stars.dr.event.dao;

public interface EventInventoryDao {

    /**
     * Delete events for the Inventory.
     * @param inventoryId
     */
    public void deleteInventoryEvents(Integer inventoryId);

}
