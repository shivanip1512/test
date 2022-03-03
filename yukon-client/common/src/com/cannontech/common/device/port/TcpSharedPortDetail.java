package com.cannontech.common.device.port;

import com.cannontech.database.data.port.TerminalServerSharedPort;
import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.db.port.PortTerminalServer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonPropertyOrder({ "id", "name", "type", "ipAddress", "portNumber", "baudRate", "enable", "carrierDetectWaitInMilliseconds",
        "protocolWrap", "timing", "sharing" })
@JsonDeserialize(using = JsonDeserializer.None.class)
public class TcpSharedPortDetail extends TerminalServerPortDetailBase<TerminalServerSharedPort> {

    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void buildDBPersistent(TerminalServerSharedPortBase port) {
        super.buildDBPersistent(port);

        PortTerminalServer portTerminalServer = port.getPortTerminalServer();
        if (getIpAddress() != null) {
            portTerminalServer.setIpAddress(getIpAddress());
        }
    }

    @Override
    public void buildModel(TerminalServerSharedPortBase port) {
        super.buildModel(port);
        PortTerminalServer portTerminalServer = port.getPortTerminalServer();
        setIpAddress(portTerminalServer.getIpAddress());
    }

}
