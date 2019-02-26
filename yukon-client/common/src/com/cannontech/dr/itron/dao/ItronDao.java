package com.cannontech.dr.itron.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.NotFoundException;

public interface ItronDao {
    
    /**
     * Get the ID of the Itron group that is mapped to the specified Yukon LM group.
     */
    int getItronGroupId(int yukonLmGroupId) throws NotFoundException;
    
    /**
     * Get the ID of the Itron program that is mapped to the specified Yukon LM program.
     */
    int getItronProgramId(int yukonLmProgramId) throws NotFoundException;
    
    /**
     * Adds mapping with Yukon LM Group and virtualRelayId
     */
    void addGroupMapping(int yukonLmGroupId, int virtualRelayId);
    
    /**
     * Add a mapping for an Itron program ID and a Yukon LM Program
     */
    void addProgramMapping(long itronProgramId, int yukonLmProgramId);

    /**
     * Get all Itron program IDs mapped to the specified Yukon LM programs.
     */
    Map<Integer, Long> getItronProgramIds(Collection<Integer> lmProgramIds);

    /**
     * Get all Itron group IDs mapped to the specified Yukon LM groups.
     */
    Map<Integer, Long> getItronGroupIds(Collection<Integer> lmGroupIds);

    /**
     * Returns virtual relay Id for a group
     */
    int getVirtualRelayId(int yukonLmGroupId) throws NotFoundException;

    /**
     * Updates group mapping with Itron group id
     */
    void updateGroupMapping(int yukonLmGroupId, long itronGroupId);

    /**
     * Returns pao ids of groups that do not have corresponding Itron group id
     */
    List<Integer> getLmGroupsWithoutItronGroup(Collection<Integer> lmGroupIds);
}
