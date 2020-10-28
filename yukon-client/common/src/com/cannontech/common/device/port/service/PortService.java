package com.cannontech.common.device.port.service;

import java.util.List;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.port.DirectPort;

public interface PortService {

    /**
     * Create the Port.
     */
    PortBase<? extends DirectPort> create(PortBase<? extends DirectPort> port, LiteYukonUser liteYukonUser);

    /**
     * Retrieve Port for passed portId.
     */
    PortBase<? extends DirectPort> retrieve(int portId);

    /**
     * Update the Port.
     */
    PortBase<? extends DirectPort> update(int portId, PortBase<? extends DirectPort> port, LiteYukonUser liteYukonUser);
    
    /**
     * Delete the Port.
     */
    int delete(int portId, LiteYukonUser liteYukonUser);

    /**
     * Retrieve List of all Comm channels.
     */
    List<PortBase> getAllPorts();

    /**
     * Retrieves list of all devices using portId. Returns empty list when no devices found.
     */
    List<DeviceBaseModel> getDevicesAssignedPort(int portId);

}
