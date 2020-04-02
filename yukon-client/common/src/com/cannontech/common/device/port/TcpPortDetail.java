package com.cannontech.common.device.port;

import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.TcpPort;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TCPPORT")
public class TcpPortDetail extends PortDetailBase {

    @Override
    public void buildModel(DirectPort port) {
        super.buildModel(port);
        TcpPort tctPort = (TcpPort) port;
        getTiming().buildModel(tctPort.getPortTiming());
        setTiming(getTiming());
    }
}
