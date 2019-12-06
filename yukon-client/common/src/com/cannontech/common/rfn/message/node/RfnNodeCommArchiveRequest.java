package com.cannontech.common.rfn.message.node;

import java.io.Serializable;
import java.util.Map;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnNodeCommArchiveRequest
 */
public class RfnNodeCommArchiveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // Map referenceID to nodeComm
    private Map<Long, NodeComm> nodeComms;

    public Map<Long, NodeComm> getNodeComms() {
        return nodeComms;
    }

    public void setNodeComms(Map<Long, NodeComm> nodeComms) {
        this.nodeComms = nodeComms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeComms == null) ? 0 : nodeComms.hashCode());
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
        RfnNodeCommArchiveRequest other = (RfnNodeCommArchiveRequest) obj;
        if (nodeComms == null) {
            if (other.nodeComms != null)
                return false;
        } else if (!nodeComms.equals(other.nodeComms))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnNodeCommArchiveRequest [nodeComms=%s]", nodeComms);
    }
}