package com.cannontech.amr.rfn.message.status;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnStatusArchiveRequest
 */
public class RfnStatusArchiveRequest implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;

    private long statusPointId;
    private RfnStatus status;

    public long getStatusPointId() {
        return statusPointId;
    }

    public void setStatusPointId(long statusPointId) {
        this.statusPointId = statusPointId;
    }

    public RfnStatus getStatus() {
        return status;
    }

    public void setStatus(RfnStatus status) {
        this.status = status;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return status.getRfnIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        RfnStatusArchiveRequest other = (RfnStatusArchiveRequest) obj;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (statusPointId != other.statusPointId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnStatusArchiveRequest [statusPointId=%s, status=%s]", statusPointId, status);
    }
}
