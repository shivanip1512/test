package com.cannontech.development.model;

import com.cannontech.common.pao.PaoType;

public enum DevCommChannel {

    TEST_COMM_1(PaoType.TSERVER_SHARED, "Test Comm Channel", "127.0.0.1", 15000, 9600, 25),
    CART_PORT_1(PaoType.TSERVER_SHARED, "Wireless Cart Port 1", "127.0.0.1", 2101, 1200, 25),
    SIM(PaoType.TSERVER_SHARED, "Sim Comm Channel", "127.0.0.1", 2102, 1200, 25),
    ;

    private final PaoType paoType;
    private final String name;
    private final String ipAddress;
    private final int port;
    private final int baudRate;
    private final int portTimingPreTxWait;
    
    private DevCommChannel(PaoType paoType, String name, String ipAddress, int port, int baudRate, int portTimingPreTxWait) {
        this.paoType = paoType;
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.baudRate = baudRate;
        this.portTimingPreTxWait = portTimingPreTxWait;
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

    public int getPortTimingPreTxWait() {
        return portTimingPreTxWait;
    }
}
