package com.cannontech.common.device.port;

import com.cannontech.database.data.port.TerminalServerSharedPort;
import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.port.PortTerminalServer.EncodingType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "type", "ipAddress", "portNumber", "baudRate", "enable", "carrierDetectWaitInMilliseconds",
        "protocolWrap", "timing", "sharing" })
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

        portTerminalServer.setEncodingType(EncodingType.NONE);
        portTerminalServer.setEncodingKey("");
    }

    @Override
    public void buildModel(TerminalServerSharedPortBase port) {
        super.buildModel(port);
        PortTerminalServer portTerminalServer = port.getPortTerminalServer();
        setIpAddress(portTerminalServer.getIpAddress());
    }

}
