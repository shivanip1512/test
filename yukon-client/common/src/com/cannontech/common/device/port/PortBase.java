package com.cannontech.common.device.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.port.DirectPort;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = TcpPortInfo.class, name = "TCPPORT") })
public class PortBase implements Port {

    private String name;
    private boolean enable;
    private BaudRate baudRate;
    @JsonTypeId
    private PaoType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public BaudRate getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(BaudRate baudRate) {
        this.baudRate = baudRate;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    @Override
    public void buildModel(DirectPort port) {
        setName(port.getPortName());
        setEnable(port.getPAODisableFlag() == 'N' ? true : false );
        setBaudRate(BaudRate.getForRate(port.getPortSettings().getBaudRate()));
        setType(port.getPaoType());
    }

    @Override
    public void buildDBPersistent(DirectPort port) {
        port.getPortSettings().setBaudRate(getBaudRate().getBaudRateValue());
        port.setPAOName(getName());
        port.setDisableFlag(isEnable() == true ? 'N' : 'Y');
    }
}
