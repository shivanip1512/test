package com.cannontech.common.device.port.service;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.PortDetailBase;

public interface PortService {

    /**
     * Create the Port.
     */
    Integer create(PortBase request);

    /**
     * Retrieve Port for passed portId.
     */
    PortDetailBase retrieve(int portId);
}
