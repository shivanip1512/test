package com.cannontech.common.device.port;


import org.apache.commons.lang3.StringUtils;

import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.data.port.UdpPort;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.port.PortTerminalServer.EncodingType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonPropertyOrder({ "id", "name", "type", "ipAddress", "portNumber", "baudRate", "enable", "keyInHex",
        "carrierDetectWaitInMilliseconds", "protocolWrap", "timing", "sharing" })

@JsonIgnoreProperties(value={"ipAddress"}, allowGetters= true, ignoreUnknown = true)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class UdpPortDetail extends TerminalServerPortDetailBase<UdpPort> {

    private String keyInHex;
    private String ipAddress = "UDP";

    public String getKeyInHex() {
        return keyInHex;
    }

    public void setKeyInHex(String keyInHex) {
        this.keyInHex = keyInHex;
    }

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
        if (ipAddress != null) {
            portTerminalServer.setIpAddress(ipAddress);
        }
        
        if (getKeyInHex() != null) {
            portTerminalServer.setEncodingKey(getKeyInHex());
            if (StringUtils.isNotBlank(getKeyInHex())) {
                portTerminalServer.setEncodingType(EncodingType.AES);
            } else {
                portTerminalServer.setEncodingType(EncodingType.NONE);
            }
        }
    }

    @Override
    public void buildModel(TerminalServerSharedPortBase port) {
        super.buildModel(port);
        PortTerminalServer portTerminalServer = port.getPortTerminalServer();
        setKeyInHex(portTerminalServer.getEncodingKey());
        setIpAddress(ipAddress);
    }
}
