package com.cannontech.dr.edgeDr;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class EdgeDrUnicastRequest {
    private final List<Integer> paoIds;
    private final String messageGuid;
    private final byte[] payload;
    private final EdgeUnicastPriority queuePriority;
    private final EdgeUnicastPriority networkPriority;
    
    public EdgeDrUnicastRequest(Integer paoId, String messageGuid, byte[] payload, EdgeUnicastPriority queuePriority, EdgeUnicastPriority networkPriority) {
        if (paoId == null) {
            throw new IllegalArgumentException("PAO ID cannot be null.");
        }
        paoIds = Lists.newArrayList(paoId);
        this.messageGuid = messageGuid;
        this.payload = payload;
        this.queuePriority = queuePriority;
        this.networkPriority = networkPriority;
    }
    
    public EdgeDrUnicastRequest(List<Integer> paoIds, String messageGuid, byte[] payload, EdgeUnicastPriority queuePriority, EdgeUnicastPriority networkPriority) {
        paoIds = new ArrayList<>(paoIds); //Defensive copy
        this.paoIds = paoIds;
        this.messageGuid = messageGuid;
        this.payload = payload;
        this.queuePriority = queuePriority;
        this.networkPriority = networkPriority;
    }

    public List<Integer> getPaoIds() {
        return paoIds;
    }

    public String getMessageGuid() {
        return messageGuid;
    }

    public byte[] getPayload() {
        return payload;
    }

    public EdgeUnicastPriority getQueuePriority() {
        return queuePriority;
    }

    public EdgeUnicastPriority getNetworkPriority() {
        return networkPriority;
    }
    
}
