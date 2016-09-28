package com.cannontech.common.rfn.message.archive;

import java.io.Serializable;

public class RfRelayArchiveResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long nodeId;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RfRelayArchiveResponse other = (RfRelayArchiveResponse) obj;
        if (nodeId != other.nodeId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfRelayArchiveResponse [nodeId=%s]", nodeId);
    }

}
