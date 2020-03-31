package com.cannontech.common.device.port;

import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.TcpPort;

public class TcpPortDetail extends TcpPortInfo {

    private PortTiming timing;
    
    public PortTiming getTiming() {
        return timing;
    }

    public void setTiming(PortTiming timing) {
        this.timing = timing;
    }

    @Override
    public void buildModel(DirectPort port) {
        super.buildModel(port);
        TcpPort tctPort = (TcpPort) port;
        PortTiming timing = new PortTiming();
        timing.buildModel(tctPort.getPortTiming());
        this.setTiming(timing);
    }
}
