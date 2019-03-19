package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * RfnMetadataMultiRequest provides two new features to retrieve a better performance.
 * 1. You can specify 1 or more devices for one request instead of sending multiple requests.
 * 2. You can select 1 or more metadata only you are interested in to query. 
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.MetadataMultiRequest
 */
public class RfnMetadataMultiRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Set<RfnIdentifier> rfnIdentifiers;
    
    private Set<RfnMetadataMulti> rfnMetadatas;

    public Set<RfnIdentifier> getRfnIdentifiers() {
        return rfnIdentifiers;
    }
    
    public void setRfnIdentifiers(RfnIdentifier... rfnIdentifiers) {
        setRfnIdentifiers(Set.of(rfnIdentifiers));
    }
    
    public void setRfnMetadatas(RfnMetadataMulti... rfnMetadatas) {
        setRfnMetadatas(Set.of(rfnMetadatas));
    }

    public Set<RfnMetadataMulti> getRfnMetadatas() {
        return rfnMetadatas;
    }

    public void setRfnMetadatas(Set<RfnMetadataMulti> rfnMetadatas) {
        this.rfnMetadatas = rfnMetadatas;
    }

    public void setRfnIdentifiers(Set<RfnIdentifier> rfnIdentifiers) {
        this.rfnIdentifiers = rfnIdentifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifiers == null) ? 0 : rfnIdentifiers.hashCode());
        result = prime * result + ((rfnMetadatas == null) ? 0 : rfnMetadatas.hashCode());
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
        RfnMetadataMultiRequest other = (RfnMetadataMultiRequest) obj;
        if (rfnIdentifiers == null) {
            if (other.rfnIdentifiers != null)
                return false;
        } else if (!rfnIdentifiers.equals(other.rfnIdentifiers))
            return false;
        if (rfnMetadatas == null) {
            if (other.rfnMetadatas != null)
                return false;
        } else if (!rfnMetadatas.equals(other.rfnMetadatas))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnMetadataMultiRequest [rfnIdentifiers=%s, rfnMetadatas=%s]",
                             rfnIdentifiers,
                             rfnMetadatas);
    }
}