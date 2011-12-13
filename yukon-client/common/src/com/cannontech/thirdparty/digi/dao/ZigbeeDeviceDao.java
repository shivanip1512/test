package com.cannontech.thirdparty.digi.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;


public interface ZigbeeDeviceDao {

    public ZigbeeDevice getZigbeeDevice(int deviceId);
    
    public List<ZigbeeDevice> getZigbeeDevicesForGroupId(int groupId);
    
    public List<ZigbeeDevice> getAllEndPoints();
    
    public ZigbeeEndpoint getZigbeeEndPointByInventoryId(int inventoryId);
    
    public ZigbeeEndpoint getZigbeeEndPoint(int deviceId);
    
    public ZigbeeEndpoint getZigbeeEndPointByMACAddress(String macAddress);
    
    public int getDeviceIdForMACAddress(String macAddress);
    
    public List<Integer> getDeviceIdsForMACAddresses(List<String> macAddresses);
    
    public Integer findGatewayIdForInventory(int inventoryId);
    
    public void updateNodeId(PaoIdentifier paoIdentifier, int nodeId);
}