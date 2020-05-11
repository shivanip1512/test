package com.cannontech.common.device.port.dao;

import java.util.List;

import com.cannontech.common.device.model.DeviceBaseModel;

public interface PortDao {

    /**
     * Find Port Id based on IP Address and Port number.
     */
    public Integer findUniquePortTerminalServer(String ipAddress, Integer port);
    
    /**
     * Retrieves the list of devices for a port.
     */
    public List<DeviceBaseModel> getAllAssignedDevicesForPort(Integer portId);
}
