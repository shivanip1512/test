package com.cannontech.dr.ecobee.service;

import java.util.List;

public interface EcobeeZeusGroupService {
    /**
     * Return a list of Zeus group IDs for a given Yukon LM group ID.
     */
    List<String> getZeusGroupIdsForLmGroup(String yukonGroupId);

    /**
     * Return a list of Zeus group IDs for a given inventory ID.
     */
    List<String> getZeusGroupIdsForInventoryId(String inventoryId);

    /**
     * Return a single Zeus group ID for a given Yukon inventory ID and LM group ID.
     */
    String getZeusGroupId(String yukonGroupId, String inventoryId);

    /**
     * Insert a mapping for Yukon group to Zeus group ID. Return true if mapping is successful.
     */
    boolean mapGroupIdToZeusGroupId(String yukonGroupId, String zeusGroupId);

    /**
     * Remove a mapping for Yukon group to Zeus group ID.Return true if removed successfully.
     */
    boolean removeGroupIdForZeusGroupId(String yukonGroupId, String zeusGroupId);

    /**
     * Insert a mapping for inventory ID to Zeus group ID. Return true if mapping is successful.
     */
    boolean mapInventoryToZeusGroupId(String inventoryId, String zeusGroupId);

    /**
     * Delete a mapping for inventory ID to Zeus group ID. Return true if deletion is successful.
     */
    boolean deleteInventoryToZeusGroupId(String inventoryId);

    /**
     * Insert an event ID for a Zeus group ID (overwriting any existing value). Return true if update is successful.
     */
    boolean updateEventId(String eventId, String zeusGroupId);

    /**
     * Return a list of event IDs for a Yukon group ID.
     */
    List<String> getEventIds(String yukonGroupId);
}