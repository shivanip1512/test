package com.cannontech.dr.ecobee.dao;

import java.util.List;

public interface EcobeeZeusGroupDao {
    /**
     * Retrieve list of Zeus group IDs for a given Yukon LM group ID.
     */
    List<String> getZeusGroupIdsForLmGroup(String lmGroupId);

    /**
     * Retrieve list of Zeus group IDs for a given inventory ID.
     */
    List<String> getZeusGroupIdsForInventoryId(String inventoryId);

    /**
     * Retrieve a single Zeus group ID for a given Yukon inventory ID and LM group ID.
     */
    String getZeusGroupId(String lmGroupId, String inventoryId);

    /**
     * Insert a mapping for Yukon group to Zeus group ID.
     */
    void mapGroupIdToZeusGroupId(String lmGroupId, String zeusGroupId);

    /**
     * Insert a mapping for inventory ID to Zeus group ID.
     */
    void mapInventoryToZeusGroupId(String inventoryId, String zeusGroupId);

    /**
     * Delete a mapping for inventory ID to Zeus group ID.
     */
    void deleteInventoryToZeusGroupId(String inventoryId);

    /**
     * Insert an event ID for a Zeus group ID (overwriting any existing value).
     */
    void updateEventId(String eventId, String zeusGroupId);

    /**
     * Retrieve a list of event IDs for a Yukon group ID.
     */
    List<String> getEventIds(String lmGroupId);
}
