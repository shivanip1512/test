package com.cannontech.common.rfn.message.node;

import java.io.Serializable;
import java.util.Set;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnNodeCommArchiveResponse
 */
public class RfnNodeCommArchiveResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Long> referenceIDs;

    public Set<Long> getReferenceIDs() {
        return referenceIDs;
    }

    public void setReferenceIDs(Set<Long> referenceIDs) {
        this.referenceIDs = referenceIDs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((referenceIDs == null) ? 0 : referenceIDs.hashCode());
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
        RfnNodeCommArchiveResponse other = (RfnNodeCommArchiveResponse) obj;
        if (referenceIDs == null) {
            if (other.referenceIDs != null)
                return false;
        } else if (!referenceIDs.equals(other.referenceIDs))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnNodeCommArchiveResponse [referenceIDs=%s]", referenceIDs);
    }
}