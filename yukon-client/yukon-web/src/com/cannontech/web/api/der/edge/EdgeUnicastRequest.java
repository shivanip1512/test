package com.cannontech.web.api.der.edge;

import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeUnicastRequest {
    private String name;
    private PaoType type;
    private String payload;
    private EdgeUnicastPriority queuePriority = EdgeUnicastPriority.HIGH;
    private EdgeUnicastPriority networkPriority = EdgeUnicastPriority.HIGH;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public EdgeUnicastPriority getQueuePriority() {
        return queuePriority;
    }

    public void setQueuePriority(EdgeUnicastPriority queuePriority) {
        this.queuePriority = queuePriority;
    }

    public EdgeUnicastPriority getNetworkPriority() {
        return networkPriority;
    }

    public void setNetworkPriority(EdgeUnicastPriority networkPriority) {
        this.networkPriority = networkPriority;
    }
    
}
