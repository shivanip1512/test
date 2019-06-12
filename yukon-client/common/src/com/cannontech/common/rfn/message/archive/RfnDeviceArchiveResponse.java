package com.cannontech.common.rfn.message.archive;

import java.io.Serializable;
import java.util.Set;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnDeviceArchiveResponse
 */
public class RfnDeviceArchiveResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Long> referenceIds;

    public Set<Long> getReferenceIds() {
        return referenceIds;
    }

    public void setReferenceIds(Set<Long> referenceIds) {
        this.referenceIds = referenceIds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((referenceIds == null) ? 0 : referenceIds.hashCode());
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
        RfnDeviceArchiveResponse other = (RfnDeviceArchiveResponse) obj;
        if (referenceIds == null) {
            if (other.referenceIds != null)
                return false;
        } else if (!referenceIds.equals(other.referenceIds))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnDeviceArchiveResponse [referenceIds=" + referenceIds + "]";
    }
    
}