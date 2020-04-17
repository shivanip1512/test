package com.cannontech.common.device.port;

import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TerminalServerPortDetailBase<T extends TerminalServerSharedPortBase> extends PortBase<TerminalServerSharedPortBase> {

    private Integer portNumber;

    private Integer carrierDetectWaitInMilliseconds;
    private ProtocolWrap protocolWrap;

    private PortTiming timing;
    private PortSharing sharing;

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

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
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
    public void buildDBPersistent(TerminalServerSharedPortBase port) {
        super.buildDBPersistent(port);

        PortTerminalServer portTerminalServer = port.getPortTerminalServer();

        if (getPortNumber() != null) {
            portTerminalServer.setSocketPortNumber(getPortNumber());
        }

        PortSettings portSettings = port.getPortSettings();

        if (getCarrierDetectWaitInMilliseconds() != null) {
            portSettings.setCdWait(getCarrierDetectWaitInMilliseconds());
        }

        CommPort commPort = port.getCommPort();
        if (getProtocolWrap() != null) {
            commPort.setCommonProtocol(getProtocolWrap().getProtocolWrapString());
        }

        getTiming().buildDBPersistent(port.getPortTiming());
        getSharing().buildDBPersistent(commPort);
    }

    @Override
    public void buildModel(TerminalServerSharedPortBase port) {
        super.buildModel(port);

        PortTerminalServer portTerminalServer = port.getPortTerminalServer();

        setPortNumber(portTerminalServer.getSocketPortNumber());

        CommPort commPort = port.getCommPort();
        setProtocolWrap(ProtocolWrap.valueOf(commPort.getCommonProtocol()));

        PortSettings portSettings = port.getPortSettings();

        setCarrierDetectWaitInMilliseconds(portSettings.getCdWait());

        getTiming().buildModel(port.getPortTiming());
        getSharing().buildModel(port.getCommPort());

    }

}
