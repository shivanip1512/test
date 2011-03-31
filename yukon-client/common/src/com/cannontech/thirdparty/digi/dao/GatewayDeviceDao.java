package com.cannontech.thirdparty.digi.dao;

import java.util.List;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.digi.model.ZigbeeDeviceAssignment;

public interface GatewayDeviceDao {
    
    public DigiGateway getDigiGateway(int deviceId);
    
    public void createDigiGateway(DigiGateway digiGateway);
    public void updateDigiGateway(DigiGateway digiGateway);
    public void deleteDigiGateway(DigiGateway digiGateway);
    
    public void assignDeviceToGateway(int deviceId, int gatewayId);
    public void unassignDeviceFromGateway(int deviceId);

    public List<ZigbeeDeviceAssignment> getZigbeeDevicesForAccount(int accountId, List<Integer> hardwareTypeIds);

    /**
     * Returns the InventoryIdentifier for the gateway this device is assigned to or null if not assigned to a gateway
     */
    public InventoryIdentifier findGatewayByDeviceMapping(int deviceId);

    /**
     * Returns the gateway id for the given device id or null if the device is not assigned to a gateway
     */
    public Integer findGatewayIdForDeviceId(int deviceId);
    
}