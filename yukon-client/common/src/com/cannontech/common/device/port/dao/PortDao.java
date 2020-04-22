package com.cannontech.common.device.port.dao;

import com.cannontech.database.db.port.PortTerminalServer;

public interface PortDao {

    /**
     * Find PortTerminalServer object based on IP Address and Port number.
     */
    public PortTerminalServer findPortTerminalServer(String ipAddress, Integer port);
}
