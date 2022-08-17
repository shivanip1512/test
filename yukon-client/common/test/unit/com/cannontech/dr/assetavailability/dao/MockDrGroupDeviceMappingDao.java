package com.cannontech.dr.assetavailability.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.util.MethodNotImplementedException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MockDrGroupDeviceMappingDao implements DRGroupDeviceMappingDao {
    private Multimap<Integer, Integer> groupToInventoryMap = ArrayListMultimap.create();
    private Map<Integer, Integer> inventoryToDeviceMap = Maps.newHashMap();
    private Multimap<PaoIdentifier, Integer> drGroupToLoadGroupMap = ArrayListMultimap.create();
    
    private final String gatewayName = "Test Gateway";
    private final PaoIdentifier gatewayPaoId = new PaoIdentifier(100, PaoType.RFN_GATEWAY);
    private static final RfnIdentifier gatewayRfnId = new RfnIdentifier("10000", "CPS", "RFGateway");
    private final static String gateway2Name = "Test Gateway 2";
    
    private static RfnGatewayData createEmptyRfnGatewayData(RfnIdentifier rfnIdentifier) {
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(rfnIdentifier);
        return new RfnGatewayData(gatewayDataResponse, gateway2Name);
    }
    
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

    @Override
    public Map<Integer, SimpleDevice> getInventoryPaoMapForGrouping(YukonPao yukonPao) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<String> getSerialNumbersForLoadGroups(Iterable<Integer> loadGroupIds) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public List<RfnGateway> getRfnGatewayList(Iterable<Integer> loadGroupIds) {
        List<RfnGateway> resultList = new ArrayList<RfnGateway>();
        RfnGateway rfnGateway = new RfnGateway(gatewayName, gatewayPaoId, gatewayRfnId, createEmptyRfnGatewayData(gatewayRfnId));
        resultList.add(rfnGateway);
        return resultList;
    }
}
