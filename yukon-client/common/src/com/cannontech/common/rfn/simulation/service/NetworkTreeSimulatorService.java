package com.cannontech.common.rfn.simulation.service;

import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.rfn.model.RfnDevice;

public interface NetworkTreeSimulatorService {

    /**
     * Creates RfnVertex from the data in Yukon database
     */
    RfnVertex buildVertex(RfnDevice gateway);
}
