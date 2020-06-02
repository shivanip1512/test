package com.cannontech.common.device.port;

import com.cannontech.database.data.port.LocalSharedPortBase;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.database.db.port.PortSettings;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonPropertyOrder({ "id", "name", "type", "baudRate", "enable", "physicalPort", "carrierDetectWaitInMilliseconds",
        "protocolWrap", "timing", "sharing" })
@JsonDeserialize(using = JsonDeserializer.None.class)
public class LocalSharedPortDetail extends PortBase<LocalSharedPortBase> {

    private Integer carrierDetectWaitInMilliseconds;
    private ProtocolWrap protocolWrap;

    private PortTiming timing;
    private PortSharing sharing;
    private String physicalPort;

    public String getPhysicalPort() {
        return physicalPort;
    }

    public void setPhysicalPort(String physicalPort) {
        this.physicalPort = physicalPort;
    }

    public PortTiming getTiming() {
        if (timing == null) {
            timing = new PortTiming();
        }
        return timing;
    }

    public void setTiming(PortTiming timing) {
        this.timing = timing;
    }

    public PortSharing getSharing() {
        if (sharing == null) {
            sharing = new PortSharing();
        }
        return sharing;
    }

    public void setSharing(PortSharing sharing) {
        this.sharing = sharing;
    }

    public Integer getCarrierDetectWaitInMilliseconds() {
        return carrierDetectWaitInMilliseconds;
    }

    public void setCarrierDetectWaitInMilliseconds(Integer carrierDetectWaitInMilliseconds) {
        this.carrierDetectWaitInMilliseconds = carrierDetectWaitInMilliseconds;
    }

    public ProtocolWrap getProtocolWrap() {
        return protocolWrap;
    }

    public void setProtocolWrap(ProtocolWrap protocolWrap) {
        this.protocolWrap = protocolWrap;
    }

    @Override
    public void buildDBPersistent(LocalSharedPortBase port) {
        super.buildDBPersistent(port);

        PortSettings portSettings = port.getPortSettings();

        if (getCarrierDetectWaitInMilliseconds() != null) {
            portSettings.setCdWait(getCarrierDetectWaitInMilliseconds());
        }

        CommPort commPort = port.getCommPort();
        if (getProtocolWrap() != null) {
            commPort.setCommonProtocol(getProtocolWrap().getProtocolWrapString());
        }

        if (getPhysicalPort() != null) {
            port.getPortLocalSerial().setPhysicalPort(getPhysicalPort());
        }

        getTiming().buildDBPersistent(port.getPortTiming());
        getSharing().buildDBPersistent(commPort);
    }

    @Override
    public void buildModel(LocalSharedPortBase port) {
        super.buildModel(port);

        setPhysicalPort(port.getPortLocalSerial().getPhysicalPort());
        CommPort commPort = port.getCommPort();
        setProtocolWrap(ProtocolWrap.valueOf(commPort.getCommonProtocol()));

        PortSettings portSettings = port.getPortSettings();

        setCarrierDetectWaitInMilliseconds(portSettings.getCdWait());

        getTiming().buildModel(port.getPortTiming());
        getSharing().buildModel(port.getCommPort());
    }
}
