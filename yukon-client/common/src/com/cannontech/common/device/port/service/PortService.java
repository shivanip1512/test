package com.cannontech.common.device.port.service;

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
}
