package com.cannontech.common.device.port;

import com.cannontech.database.data.port.TcpPort;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
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