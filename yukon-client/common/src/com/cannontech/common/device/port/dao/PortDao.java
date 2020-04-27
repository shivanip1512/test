package com.cannontech.common.device.port.dao;

public interface PortDao {

    /**
     * Find Port Id based on IP Address and Port number.
     */
    public Integer findUniquePortTerminalServer(String ipAddress, Integer port);
}
