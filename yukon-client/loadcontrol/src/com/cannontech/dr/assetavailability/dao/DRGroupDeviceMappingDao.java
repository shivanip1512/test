package com.cannontech.dr.assetavailability.dao;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;

/**
 * Retrieves a set of all devices "in" a load group, program, scenario or area. To be "in" a load 
 * group, devices must have associated inventory enrolled in that group.
 */
public interface DRGroupDeviceMappingDao {

    /**
     * @return The device ids / inventory ids associated with the specified load groups.
     */
    public Map<Integer, Integer> getDeviceAndInventoryIdsForLoadGroups(Collection<Integer> loadGroupIds);
    
    /**
     * @return The set of device ids associated with the specified load group, program, scenario or
     * control area.
     */
    public Set<Integer> getDeviceIdsForGrouping(PaoIdentifier paoIdentifier);
    
    /**
     * @return A set of ids for the load groups in control area, scenario, load program, or load group
     * specified by the paoIdentifier.
     * @throws IllegalArgumentException If the paoIdentifier is not a DR grouping.
     */
    public Set<Integer> getLoadGroupIdsForDrGroup(PaoIdentifier paoIdentifier);
}
