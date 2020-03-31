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
    // Set port timings
    com.cannontech.database.db.port.PortTiming portTiming = tctPort.getPortTiming();
    timing.setExtraTimeOut(portTiming.getExtraTimeOut());
    timing.setPostTxWait(portTiming.getPostTxWait());
    timing.setPreTxWait(portTiming.getPreTxWait());
    timing.setReceiveDataWait(portTiming.getReceiveDataWait());
    timing.setRtsToTxWait(portTiming.getRtsToTxWait());
    this.setTiming(timing);
    }
}
