package com.cannontech.dr.assetavailability.dao;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.MethodNotImplementedException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MockDrGroupDeviceMappingDao implements DRGroupDeviceMappingDao {
    private Multimap<Integer, Integer> groupToInventoryMap = ArrayListMultimap.create();
    private Map<Integer, Integer> inventoryToDeviceMap = Maps.newHashMap();
    private Multimap<PaoIdentifier, Integer> drGroupToLoadGroupMap = ArrayListMultimap.create();
    
    public MockDrGroupDeviceMappingDao(Multimap<Integer, Integer> groupToInventoryMap,
                                       Map<Integer, Integer> inventoryToDeviceMap,
                                       Multimap<PaoIdentifier, Integer> drGroupToLoadGroupMap) {
        this.groupToInventoryMap = groupToInventoryMap;
        this.inventoryToDeviceMap = inventoryToDeviceMap;
        this.drGroupToLoadGroupMap = drGroupToLoadGroupMap;
    }
    
    @Override
    public Map<Integer, Integer> getInventoryAndDeviceIdsForLoadGroups(Iterable<Integer> loadGroupIds) {
        Set<Integer> inventoryIds = Sets.newHashSet();
        for(Integer loadGroupId : loadGroupIds) {
            inventoryIds.addAll(groupToInventoryMap.get(loadGroupId));
        }
        Map<Integer, Integer> filteredInventoryToDeviceMap = Maps.newHashMap();
        for(Integer inventoryId : inventoryIds) {
            filteredInventoryToDeviceMap.put(inventoryId, inventoryToDeviceMap.get(inventoryId));
        }
        return filteredInventoryToDeviceMap;
    }
    
    @Override
    public Set<Integer> getLoadGroupIdsForDrGroup(PaoIdentifier paoIdentifier) {
        return Sets.newHashSet(drGroupToLoadGroupMap.get(paoIdentifier));
    }
    
    /*
     * Unimplemented methods:
     */
    @Override
    public Set<Integer> getDeviceIdsForGrouping(PaoIdentifier paoIdentifier) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<YukonPao> getDevicesForGrouping(PaoIdentifier paoIdentifier) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, SimpleDevice> getInventoryPaoMapForGrouping(YukonPao yukonPao) {
        throw new MethodNotImplementedException();
    }
}
