package com.cannontech.common.rfn.model;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * RfnDeviceDeleteRequest is sent from Yukon to NM when user selects
 * the "Delete XXX" Action in a Device Detail Page.
 * 
 * JMS queue name:
 * com.eaton.eas.yukon.networkmanager.RfnDeviceDeleteRequest
 */
public class RfnDeviceDeleteRequest implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        RfnDeviceDeleteRequest other = (RfnDeviceDeleteRequest) obj;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnDeviceDeleteRequest [rfnIdentifier=%s]", rfnIdentifier);
    }

}
