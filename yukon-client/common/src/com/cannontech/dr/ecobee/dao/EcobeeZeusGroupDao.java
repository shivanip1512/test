package com.cannontech.dr.ecobee.dao;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;

public interface EcobeeZeusGroupDao {
    /**
     * Retrieve List of Zeus group ID for a given Yukon LM group ID and yukon Program ID.
     */
    List<String> getZeusGroupIdsForLmGroup(int yukonGroupId, int programId);

    /**
     * Retrieve Zeus group ID for a given Yukon LM group ID, inventoryId and Yukon program ID.
     */
    String getZeusGroupId(int yukonGroupId, int inventoryId, int programId);

    /**
     * Insert a mapping for Yukon group to Zeus group details.
     */
    void mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName, int programId);

    /**
     * Remove a mapping for Yukon group to Zeus group ID.
     */
    void removeGroupIdForZeusGroupId(int yukonGroupId, String zeusGroupId);

    /**
     * Insert a mapping for inventory ID to Zeus group ID.
     */
    void mapInventoryToZeusGroupId(int inventoryId, String zeusGroupId);

    /**
     * Delete Zeus group mapping for an inventory ID and corresponding zeusGroupId.
     */
    void deleteZeusGroupMappingForInventory(int inventoryId, String zeusGroupId);

    /**
     * Insert an event ID for a Zeus group ID (overwriting any existing value).
     */
    void updateEventId(String eventId, String zeusGroupId);

    /**
     * Retrieve Demand Response event IDs for a Yukon group ID.
     */
    List<String> getEventIds(int yukonGroupId);

    /**
     * Returns thermostat count fort the specified Zeus group ID.
     */
    int getDeviceCount(String zeusGroupId);

    /**
     * Retrieve all the inventory IDs for the specified Zeus group ID
     */
    List<Integer> getInventoryIdsForZeusGroupID(String zeusGroupId);

    /**
     * Retrieve list of inventory id for a given Yukon group.
     */
    List<Integer> getInventoryIdsForYukonGroupID(String lmGroup);

    /**
     * Return name of the specified Zeus group ID.
     */
    String getZeusGroupName(String zeusGroupId);

    /**
     * Get count of zeus group in yukon
     */
    int getGroupCount();

    /**
     * Get count of ecobee thermostats in yukon
     */
    int getAllThermostatCount();

    /**
     * Remove the specified the zeusEventId from LMGroupZeusMapping table.
     */
    void removeEventId(String zeusEventId);

    /**
     * Return the distinct Zeus group names which are mapped to specified Yukon group.
     */
    List<String> getZeusGroupNames(int yukonGroupId);

    /**
     * Return the Yukon group associated with the inventory and Yukon programId.
     */
    int getLmGroupForInventory(int inventoryId, int programId);

    /**
     * Update the Yukon programId for the specified zeusGroupId
     */
    void updateProgramId(String zeusGroupId, int programId);

    /**
     * Get Yukon program id for the specified zeusGroupId
     */
    int getProgramIdForZeusGroup(String zeusGroupId);

    /**
     * Retrieve List of enrolled yukon programId for the specified inventoryId and lmGroupId
     */
    List<Integer> getProgramIdsEnrolled(int inventoryId, int lmGroupId);

    /**
     * Return list of Zeus group id for corrosponding yukon group ids.
     */
    List<String> getGroupMapping(Set<Integer> lmGroupId);

    /**
     * Return mapping of all ecobee group and serial numbers.
     */
    Multimap<String, String> getAllEcobeeGroupToSerialNumberMapping();

    /**
     * Return all ecobee groups mapped with the specified inventoryId
     */
    List<String> getZeusGroupIdsForInventoryId(int inventoryId);

    /**
     * Delete zeus group mapping.
     */
    void deleteZeusGroupMapping(String zeusGroupId);

    /**
     * Update mappings with new zeus group Id.
     */
    void updateZeusGroupId(String oldZeusGroupId, String newZeusGroupId);

    /**
     * Return all programs mapped with the specified lmGroupId
     */
    List<Integer> getProgramIdsByGroupId(Integer lmGroupId);
}