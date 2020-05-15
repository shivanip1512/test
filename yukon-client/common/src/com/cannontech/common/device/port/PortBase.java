package com.cannontech.common.device.port;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.port.DirectPort;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value={"id"}, allowGetters= true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializePaoTypeLookup.class)
public class PortBase<T extends DirectPort> extends DeviceBaseModel implements DBPersistentConverter<T> {

    private BaudRate baudRate;

    public BaudRate getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(BaudRate baudRate) {
        this.baudRate = baudRate;
    }

    @Override
    public void buildModel(T port) {
        setId(port.getPAObjectID());
        setName(port.getPortName());
        setEnable(port.getPAODisableFlag() == 'N' ? true : false );
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
        if (getEnable() != null) {
        port.setDisableFlag(BooleanUtils.isFalse(getEnable()) ? 'Y' : 'N');
        }
    }

    @Override
    public void buildModel(LiteYukonPAObject liteYukonPAObject) {
        setId(liteYukonPAObject.getLiteID());
        setName(liteYukonPAObject.getPaoName());
        setEnable(liteYukonPAObject.getDisableFlag().equals("N") ? true : false);
        setType(liteYukonPAObject.getPaoType());
    }
}
