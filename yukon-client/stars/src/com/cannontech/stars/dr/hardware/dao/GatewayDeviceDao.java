package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.common.model.DigiGateway;
import com.cannontech.stars.dr.hardware.model.ZigbeeDeviceAssignment;

public interface GatewayDeviceDao {
    
    public DigiGateway getDigiGateway(int deviceId);
    
    public void createDigiGateway(DigiGateway digiGateway);
    public void updateDigiGateway(DigiGateway digiGateway);
    public void deleteDigiGateway(DigiGateway digiGateway);
    
    public List<ZigbeeDeviceAssignment> getAssignedDevices(int gatewayId);
    public void assignDeviceToGateway(int deviceId, int gatewayId);
    public void unassignDeviceFromGateway(int deviceId);
    
}
