package com.cannontech.common.device.port;

import com.cannontech.database.data.port.TcpPort;

public class TcpPortDetail extends PortBase<TcpPort> {

    private PortTiming timing;

    public PortTiming getTiming() {
        if (timing == null) {
            timing = new PortTiming();
        }
        return timing;
    }

    public void setTiming(PortTiming timing) {
        this.timing = timing;
    }

    @Override
    public void buildModel(TcpPort port) {
        super.buildModel(port);
        getTiming().buildModel(port.getPortTiming());
    }

    @Override
    public void buildDBPersistent(TcpPort port) {
        super.buildDBPersistent(port);
        getTiming().buildDBPersistent(port.getPortTiming());
    }
}