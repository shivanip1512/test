package com.cannontech.common.device.port.service;

import com.cannontech.common.device.port.PortBase;

public interface PortService {

    /**
     * Create the Port.
     */
    Integer create(PortBase request);

    /**
     * Retrieve Port for passed portId.
     */
    PortBase retrieve(int portId);
}
