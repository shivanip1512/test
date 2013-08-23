package com.cannontech.dr.assetavailability.dao;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;

/**
 * Dao for determining hierarchy of dr control areas, scenarios, programs and load groups, and
 * enrolled inventory/devices.
 */
public interface DRGroupDeviceMappingDao {

    /**
     * @return The inventory ids / device ids associated with the specified load groups.
     */
    public Map<Integer, Integer> getInventoryAndDeviceIdsForLoadGroups(Iterable<Integer> loadGroupIds);
    
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
