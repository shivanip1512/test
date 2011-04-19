package com.cannontech.thirdparty.digi.dao;

import java.util.List;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.model.ZigbeeDeviceAssignment;
import com.cannontech.thirdparty.model.ZigbeeGateway;

public interface GatewayDeviceDao {
    
    public ZigbeeGateway getZigbeeGateway(int gatewayId);
    public DigiGateway getDigiGateway(int deviceId);
    
    public void createDigiGateway(DigiGateway digiGateway);
    public void updateDigiGateway(DigiGateway digiGateway);
    public void deleteDigiGateway(DigiGateway digiGateway);
    
    /**
     * Updates the device to gateway assignment. If gatewayId is null, any device to gateway mapping
     * for this device will be removed. 
     * @param deviceId
     * @param gatewayId
     */
    public void updateDeviceToGatewayAssignment(int deviceId, Integer gatewayId);
    
    public void unassignDeviceFromGateway(int deviceId);
    
    public void removeDevicesFromGateway(int gatewayId);

    public List<ZigbeeDeviceAssignment> getZigbeeDevicesForAccount(int accountId, List<Integer> hardwareTypeIds);

    /**
     * Returns the InventoryIdentifier for the gateway this device is assigned to or null if not assigned to a gateway
     */
    public InventoryIdentifier findGatewayByDeviceMapping(int deviceId);

    /**
     * Returns the gateway id for the given device id or null if the device is not assigned to a gateway
     * 
     * @param deviceId
     * @return
     */
    public Integer findGatewayIdForDeviceId(int deviceId);

    public List<DigiGateway> getGatewaysForAccount(int accountId);
    
    /**
     * Returns a list of gateways for devices assigned the the LM Group with groupId.
     * 
     * @param groupId
     * @return
     */
    public List<ZigbeeGateway> getZigbeeGatewaysForGroupId(int groupId);
}