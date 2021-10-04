package com.cannontech.dr.ecobee.service;

import java.util.List;

public interface EcobeeZeusGroupService {

    int DEFAULT_PROGRAM_ID = -1;

    /**
     * Return a list of Zeus group ID for a given Yukon LM group ID and Yukon Program ID.
     */
    List<String> getZeusGroupIdsForLmGroup(int yukonGroupId, int programId);

    /**
     * Return Zeus group ID for a given Yukon LM group ID , inventoryId and Yukon program ID.
     */
    String getZeusGroupId(int yukonGroupId, int inventoryId, int programId);

    /**
     * Insert a mapping for Yukon group to Zeus group. Return true if mapping is successful.
     */
    boolean mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName, int programId);

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
    boolean deleteZeusGroupMappingForInventory(int inventoryId, String zeusGroupId);

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
    String getNextGroupName(int yukonGroupId, int programId);

    /**
     * Return the group to which Enrollment will happen.
     */
    boolean shouldEnrollToGroup(int inventoryId, int programId);

    /**
     * Update the Yukon programId for the specified zeusGroupId
     */
    void updateProgramId(String zeusGroupId, int programId);

    /**
     * Return the program id to which inventory will be enrolled.
     */
    int getProgramIdToEnroll(int inventoryId, int lmGroupId);

    /**
     * Return true if Yukon program ID is not mapped to the zeus group.
     */
    boolean shouldUpdateProgramId(String zeusGroupId);

    /**
     * Return the program id from which inventory will be unenrolled.
     */
    int getProgramIdToUnenroll(int inventoryId, int lmGroupId);
    
    /**
     * Return the list active enrollment program Id for specified inventoryId & lmGroupId.
     */
    List<Integer> getActiveEnrolmentProgramIds(int inventoryId, int lmGroupId);

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

}