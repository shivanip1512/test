package com.cannontech.common.device.port;

import com.cannontech.common.typeResolver.NestedTypeResolver;
import com.cannontech.database.data.port.DirectPort;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "info.type")
@JsonSubTypes({ @JsonSubTypes.Type(value = TcpPortDetail.class, name = "TCPPORT") })
@JsonTypeResolver(NestedTypeResolver.class)
public class PortDetailBase {

    private PortBase info;
    private PortTiming timing;

    public PortBase getInfo() {
        return info;
    }

    public void setInfo(PortBase info) {
        this.info = info;
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

    public void buildModel(DirectPort port) {
        getInfo().buildModel(port);
    }
}
