package com.cannontech.common.device.port;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.data.port.UdpPort;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.port.PortTerminalServer.EncodingType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "type", "ipAddress", "portNumber", "baudRate", "enable", "enableEncryption", "keyInHex", "carrierDetectWait",
        "carrierDetectWaitInMilliseconds", "protocolWrap", "timing", "sharing" })
@JsonIgnoreProperties(value={"ipAddress"}, allowGetters= true, ignoreUnknown = true)
public class UdpPortDetail extends TerminalServerPortDetailBase<UdpPort> {

    Boolean enableEncryption;
    private String keyInHex;
    private String ipAddress = "UDP";

    public Boolean getEnableEncryption() {
        return enableEncryption;
    }

    public void setEnableEncryption(Boolean enableEncryption) {
        this.enableEncryption = enableEncryption;
    }

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

        if (BooleanUtils.isTrue(getEnableEncryption())) {
            portTerminalServer.setEncodingType(EncodingType.AES);
            portTerminalServer.setEncodingKey(getKeyInHex());
        } 
    }

    @Override
    public void buildModel(TerminalServerSharedPortBase port) {
        super.buildModel(port);
        PortTerminalServer portTerminalServer = port.getPortTerminalServer();
        if (StringUtils.isNotBlank(portTerminalServer.getEncodingKey())) {
            setEnableEncryption(true);
        } else {
            setEnableEncryption(false);
        }
        setKeyInHex(portTerminalServer.getEncodingKey());
        setIpAddress(ipAddress);
    }
}
