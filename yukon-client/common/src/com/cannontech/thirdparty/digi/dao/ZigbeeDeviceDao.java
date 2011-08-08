package com.cannontech.thirdparty.digi.dao;

import java.util.List;

import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndPoint;


public interface ZigbeeDeviceDao {

    public ZigbeeDevice getZigbeeDevice(int deviceId);
    
    public List<ZigbeeDevice> getZigbeeDevicesForGroupId(int groupId);
    
    public ZigbeeEndPoint getZigbeeEndPointByInventoryId(int inventoryId);
    
    public ZigbeeEndPoint getZigbeeEndPoint(int deviceId);
    
    public ZigbeeEndPoint getZigbeeEndPointByMACAddress(String macAddress);
    
    public int getDeviceIdForMACAddress(String macAddress);
    
    public List<Integer> getDeviceIdsForMACAddresses(List<String> macAddresses);
    
    public void createZigbeeEndPoint(ZigbeeEndPoint zigbeeEndPoint);
    
    public void updateZigbeeEndPoint(ZigbeeEndPoint zigbeeEndPoint);
    
    public void deleteZigbeeEndPoint(int deviceId);
    
    public Integer findGatewayIdForInventory(int inventoryId);
    
}