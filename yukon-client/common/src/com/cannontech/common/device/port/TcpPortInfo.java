package com.cannontech.common.device.port;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TCPPORT")
@JsonSubTypes({ @JsonSubTypes.Type(value = TcpPortDetail.class) })
public class TcpPortInfo extends PortBase {

}
