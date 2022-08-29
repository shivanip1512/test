package com.cannontech.web.api.der.edge;

import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties
public class EdgeMultipointRequest {
    private String groupId;
    private String payload;
    private EdgeUnicastPriority queuePriority = EdgeUnicastPriority.HIGH;
    private EdgeUnicastPriority networkPriority = EdgeUnicastPriority.HIGH;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
