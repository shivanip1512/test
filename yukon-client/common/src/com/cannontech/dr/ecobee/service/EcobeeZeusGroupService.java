package com.cannontech.dr.ecobee.service;

import java.util.List;

public interface EcobeeZeusGroupService {
    /**
     * Return a list of Zeus group IDs for a given Yukon LM group ID.
     */
    List<String> getZeusGroupIdsForLmGroup(int yukonGroupId);

    /**
     * Return a list of Zeus group IDs for a given inventory ID.
     */
    List<String> getZeusGroupIdsForInventoryId(int inventoryId);

    /**
     * Return a single Zeus group ID for a given Yukon inventory ID and LM group ID.
     */
    String getZeusGroupId(int yukonGroupId, int inventoryId);

    /**
     * Insert a mapping for Yukon group to Zeus group ID. Return true if mapping is successful.
     */
    boolean mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName);

    /**
     * Remove a mapping for Yukon group to Zeus group ID.Return true if removed successfully.
     */
    boolean removeGroupIdForZeusGroupId(int yukonGroupId, String zeusGroupId);

    /**
     * Insert a mapping for inventory ID to Zeus group ID. Return true if mapping is successful.
     */
    boolean mapInventoryToZeusGroupId(int inventoryId, String zeusGroupId);

    /**
     * Delete a mapping for inventory ID to Zeus group ID. Return true if deletion is successful.
     */
    boolean deleteInventoryToZeusGroupId(int inventoryId);

    /**
     * Insert an event ID for a Zeus group ID (overwriting any existing value). Return true if update is successful.
     */
    boolean updateEventId(String eventId, String zeusGroupId);

    /**
     * Return a list of event IDs for a Yukon group ID.
     */
    List<String> getEventIds(int yukonGroupId);

    /**
     * Retrieve a Group name for a Yukon group ID.
     */
    String getNextGroupName(int yukonGroupId);

    /**
     * Retrieve a device count for Zeus group ID.
     */
    int getDeviceCount(String zeusGroupId);

    /**
     *  Retrieve all the nventory for a Zeus group ID
     */
    List<Integer> getInventoryIdsForZeusGrouID(String zeusGroupId);

    String zeusGroupName(String zeusGroupId);
}