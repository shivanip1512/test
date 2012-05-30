package com.cannontech.dr.rfn.message.archive;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnArchiveRequest;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: yukon.rr.obj.dr.rfn.LcrArchiveRequest
 */
public class RfnLcrArchiveRequest implements RfnArchiveRequest, Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private long sensorId;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public long getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + (int) (sensorId ^ (sensorId >>> 32));
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
        RfnLcrArchiveRequest other = (RfnLcrArchiveRequest) obj;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (sensorId != other.sensorId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnLcrArchiveRequest [rfnIdentifier=%s, sensorId=%s]", rfnIdentifier, sensorId);
    }
    
}