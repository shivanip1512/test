package com.cannontech.dr.ecobee.service;

import java.util.List;

public interface EcobeeZeusGroupService {
    /**
     * Return a list of Zeus group ID for a given Yukon LM group ID.
     */
    List<String> getZeusGroupIdsForLmGroup(int yukonGroupId);

    /**
     * Return a list of Zeus group IDs for a given inventory ID.
     */
    List<String> getZeusGroupIdsForInventoryId(int inventoryId);

    /**
     * Return Zeus group ID for a given Yukon LM group ID and inventoryId.
     */
    String getZeusGroupId(int yukonGroupId, int inventoryId);

    /**
     * Insert a mapping for Yukon group to Zeus group. Return true if mapping is successful.
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
     * Delete Zeus group mapping for a inventory ID. Return true if deletion is successful.
     */
    boolean deleteZeusGroupMappingForInventoryId(int inventoryId);

    /**
     * Insert an event ID for a Zeus group ID (overwriting any existing value). Return true if update is successful.
     */
    boolean updateEventId(String eventId, String zeusGroupId);

    /**
     * Return list of Demand Response event IDs for a Yukon group ID.
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
    String zeusGroupName(String zeusGroupId);
    
    /**
     * Returns count of zeus group in yukon
     */
    int getGroupCount();
    
    /**
     * Returns count of ecobee thermostats in yukon 
     */
    int getAllThermostatCount();

    /**
     * Remove the specified the zeusEventId from LMGroupZeusMapping table.
     */
    void removeEventId(String zeusEventId);

    /**
     * Generate and return next name for Zeus group.
     */
    String getNextGroupName(int yukonGroupId);
}