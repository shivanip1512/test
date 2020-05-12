package com.cannontech.common.device.port.dao;

import java.util.List;

import com.cannontech.common.device.model.DeviceBaseModel;

public interface PortDao {

    /**
     * Find Port Id based on IP Address and Port number.
     */
    public Integer findUniquePortTerminalServer(String ipAddress, Integer port);

    /**
     * Retrieves list of all devices using portId. Returns empty list when no devices found.
     */
    public List<DeviceBaseModel> getDevicesUsingPort(Integer portId);
}
