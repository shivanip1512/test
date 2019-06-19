package com.cannontech.amr.rfn.message.status;

import java.io.Serializable;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnStatusArchiveResponse
 */
public class RfnStatusArchiveResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long statusPointId;

    public long getStatusPointId() {
        return statusPointId;
    }

    public void setStatusPointId(long statusPointId) {
        this.statusPointId = statusPointId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (statusPointId ^ (statusPointId >>> 32));
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
        RfnStatusArchiveResponse other = (RfnStatusArchiveResponse) obj;
        if (statusPointId != other.statusPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnStatusArchiveResponse [statusPointId=%s]", statusPointId);
    }
}
