package com.cannontech.common.rfn.message.archive;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: com.eaton.eas.yukon.networkmanager.RfnDeviceArchiveRequest
 */
public class RfnDeviceArchiveRequest implements Serializable {

    private static final long serialVersionUID = 2291878594754809450L;
    
    // mapping a rfnIentifier to a Long value -- referenceID
    private Map<Long, RfnIdentifier> rfnIdentifiers;

    public Map<Long, RfnIdentifier> getRfnIdentifiers() {
        return rfnIdentifiers;
    }

    public void setRfnIdentifiers(Map<Long, RfnIdentifier> rfnIdentifiers) {
        this.rfnIdentifiers = rfnIdentifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifiers == null) ? 0 : rfnIdentifiers.hashCode());
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
        RfnDeviceArchiveRequest other = (RfnDeviceArchiveRequest) obj;
        if (rfnIdentifiers == null) {
            if (other.rfnIdentifiers != null)
                return false;
        } else if (!rfnIdentifiers.equals(other.rfnIdentifiers))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnDeviceArchiveRequest [rfnIdentifiers=" + rfnIdentifiers + "]";
    }

}