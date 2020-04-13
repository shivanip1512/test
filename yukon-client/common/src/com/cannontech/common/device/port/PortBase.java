package com.cannontech.common.device.port;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.port.DirectPort;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = TcpPortDetail.class, name = "TCPPORT") })
@JsonIgnoreProperties(value={"id"}, allowGetters= true, ignoreUnknown = true)
public class PortBase<T extends DirectPort> implements DBPersistentConverter<T> {

    private Integer id;
    private String name;
    private Boolean disable;
    private BaudRate baudRate;
    private PaoType type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
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
    public void buildModel(T port) {
        setId(port.getPAObjectID());
        setName(port.getPortName());
        setDisable(port.getPAODisableFlag() == 'N' ? false : true );
        setBaudRate(BaudRate.getForRate(port.getPortSettings().getBaudRate()));
        setType(port.getPaoType());
    }

    @Override
    public void buildDBPersistent(T port) {
        // will be null during creation
        if (getId() != null) {
            port.setPortID(getId());
        }
        if (getBaudRate() != null) {
            port.getPortSettings().setBaudRate(getBaudRate().getBaudRateValue());
        }
        if (getName() != null) {
            port.setPAOName(getName());
        }
        if (getDisable() != null) {
            port.setDisableFlag(BooleanUtils.isTrue(getDisable()) ? 'Y' : 'N');
        }
    }

    @Override
    public void buildModel(LiteYukonPAObject liteYukonPAObject) {
        setId(liteYukonPAObject.getLiteID());
        setName(liteYukonPAObject.getPaoName());
        setDisable(liteYukonPAObject.getDisableFlag().equals("Y") ? true : false);
        setType(liteYukonPAObject.getPaoType());
    }
}
