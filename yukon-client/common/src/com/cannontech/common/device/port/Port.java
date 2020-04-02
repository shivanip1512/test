package com.cannontech.common.device.port;

import com.cannontech.database.data.port.DirectPort;

public interface Port {

    /**
     * Build model object from the passed db persistent port.
     */
    public void buildModel(DirectPort port);

    /**
     * Builds db persistent object for a port.
     */
    public void buildDBPersistent(DirectPort port);
}
