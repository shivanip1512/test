package com.cannontech.dr.assetavailability.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.model.RfnGateway;

/**
 * Dao for determining hierarchy of dr control areas, scenarios, programs and load groups, and
 * enrolled inventory/devices.
 */
public interface DRGroupDeviceMappingDao {

    /**
     * Returns The inventory ids / device ids associated with the specified load groups.
     */
    Map<Integer, Integer> getInventoryAndDeviceIdsForLoadGroups(Iterable<Integer> loadGroupIds);
    
    /**
     * Returns the manufacturers serial number for all inventory associated with the load groups
     */
    List<String> getSerialNumbersForLoadGroups(Iterable<Integer> loadGroupIds);
    
    /**
     * Returns A set of ids for the load groups in control area, scenario, load program, or load group
     * specified by the paoIdentifier.
     * @throws IllegalArgumentException If the paoIdentifier is not a DR grouping.
     */
    Set<Integer> getLoadGroupIdsForDrGroup(PaoIdentifier paoIdentifier);
    
    /**
     * Returns A map of inventoryId to SimpleDevice for all two-way inventory in the specified load group, program,
     * scenario or control area.
     */
    Map<Integer, SimpleDevice> getInventoryPaoMapForGrouping(YukonPao yukonPao);
    
    /**
     * Creates RfnGatewayList which has all primary gateways of assets
     * @param loadGroupIds -- list of load group ids
     */
     List<RfnGateway> getRfnGatewayList(Iterable<Integer> loadGroupIds);
}
