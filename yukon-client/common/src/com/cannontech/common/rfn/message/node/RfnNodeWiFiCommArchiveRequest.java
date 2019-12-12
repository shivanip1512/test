package com.cannontech.common.rfn.message.node;

import java.io.Serializable;
import java.util.Map;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnNodeWiFiCommArchiveRequest
 */
public class RfnNodeWiFiCommArchiveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // Map referenceID to nodeComm
    private Map<Long, NodeWiFiComm> nodeWiFiComms;

    public Map<Long, NodeWiFiComm> getNodeWiFiComms() {
        return nodeWiFiComms;
    }

    public void setNodeComms(Map<Long, NodeWiFiComm> nodeWiFiComms) {
        this.nodeWiFiComms = nodeWiFiComms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeWiFiComms == null) ? 0 : nodeWiFiComms.hashCode());
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
        RfnNodeWiFiCommArchiveRequest other = (RfnNodeWiFiCommArchiveRequest) obj;
        if (nodeWiFiComms == null) {
            if (other.nodeWiFiComms != null)
                return false;
        } else if (!nodeWiFiComms.equals(other.nodeWiFiComms))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnNodeCommArchiveRequest [nodeComms=%s]", nodeWiFiComms);
    }
}