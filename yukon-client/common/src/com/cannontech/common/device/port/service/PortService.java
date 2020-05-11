package com.cannontech.common.device.port.service;

import java.util.List;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.database.data.port.DirectPort;

public interface PortService {

    /**
     * Create the Port.
     */
    PortBase<? extends DirectPort> create(PortBase<? extends DirectPort> port);

    /**
     * Retrieve Port for passed portId.
     */
    PortBase<? extends DirectPort> retrieve(int portId);

    /**
     * Update the Port.
     */
    PortBase<? extends DirectPort> update(int portId, PortBase<? extends DirectPort> port);
    
    /**
     * Delete the Port.
     */
    int delete(int portId);

    /**
     * Retrieve List of all Comm channels.
     */
    List<PortBase> getAllPorts();

    /**
     * Retrieve List of all devices assigned to a Comm channel.
     */
    List<DeviceBaseModel> getAssignedDevicesForPort(int portId);

}
