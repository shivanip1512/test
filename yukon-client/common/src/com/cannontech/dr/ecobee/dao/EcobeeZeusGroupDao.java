package com.cannontech.dr.ecobee.dao;

import java.util.List;

public interface EcobeeZeusGroupDao {
    /**
     * Retrieve Zeus group ID for a given Yukon LM group ID.
     */
    String getZeusGroupIdForLmGroup(int yukonGroupId);

    /**
     * Retrieve list of Zeus group IDs for a given inventory ID.
     */
    List<String> getZeusGroupIdsForInventoryId(int inventoryId);

    /**
     * Insert a mapping for Yukon group to Zeus group details.
     */
    void mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName);

    /**
     * Remove a mapping for Yukon group to Zeus group ID.
     */
    void removeGroupIdForZeusGroupId(String zeusGroupId);

    /**
     * Insert a mapping for inventory ID to Zeus group ID.
     */
    void mapInventoryToZeusGroupId(int inventoryId, String zeusGroupId);

    /**
     * Delete Zeus group mapping for an inventory ID.
     */
    void deleteZeusGroupMappingForInventoryId(int inventoryId);

    /**
     * Insert an event ID for a Zeus group ID (overwriting any existing value).
     */
    void updateEventId(String eventId, String zeusGroupId);

    /**
     * Retrieve a list of event IDs for a Yukon group ID.
     */
    List<String> getEventIds(int yukonGroupId);

    /**
     * Returns thermostat count fort the specified Zeus group ID.
     */
    int getDeviceCount(String zeusGroupId);

    /**
     * Retrieve all the inventory IDs for the specified Zeus group ID
     */
    List<Integer> getInventoryIdsForZeusGrouID(String zeusGroupId);

    /**
     * Return name of the specified Zeus group ID.
     */
    String getZeusGroupName(String zeusGroupId);
}