package com.cannontech.web.support.development.database.objects;

import com.cannontech.common.pao.PaoType;

public enum DevCommChannel {
    CART_PORT_1(PaoType.TSERVER_SHARED, "Wireless Cart Port 1", "127.0.0.1", 2101, 1200),
    SIM(PaoType.TSERVER_SHARED, "Sim Comm Channel", "127.0.0.1", 2101, 1200),
    ;

    private final PaoType paoType;
    private final String name;
    private final String ipAddress;
    private final int port;
    private final int baudRate;
    
    private DevCommChannel(PaoType paoType, String name, String ipAddress, int port, int baudRate) {
        this.paoType = paoType;
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.baudRate = baudRate;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public int getBaudRate() {
        return baudRate;
    }
}
