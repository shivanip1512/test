package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * RfnMetadataMultiRequest provides some new features for a better performance.
 * 1. You can specify 1 or more metadata.
 * 2. You can specify 1 or more rfnIdentifiers which can contain any RfnIdentifier (gateway, node, relay, etc.).
 * 3. You can specify 1 or more primaryNodesForGatewayRfnIdentifiers as "group" names of rfnIdentifiers.
 *    Request is for all nodes having this gateway(s) as primary (PRIMARY_GATEWAY_NODES).
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.MetadataMultiRequest
 */
public class RfnMetadataMultiRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String requestID; // to correlate response to request
    
    // NM first retrieves primary nodes (rfnIdentifiers) from primaryNodesForGatewayRfnIdentifiers
    //     and added to rfnIdentifiers.
    // You can think primaryNodesForGatewayRfnIdentifiers are just "group" names for rfnIdentifiers.
    //
    // Usually you will specify either groups or rfnIdentifiers.
    // However you can also combine them (not usual) as you want.
    // NM will combine the rfnIdentifiers retrieved from the "groups" (PRIMARY_GATEWAY_NODES)
    //     with the given set of rfnIdentifiers before the query.
    // Note: MetadataMulti query will always be run on the final set of rfnIdentifiers.
    // The response, the keySet() of the map, also matches the final set of rfnIdentifiers.

    private Set<RfnIdentifier> primaryNodesForGatewayRfnIdentifiers;
        // Again, request is not run directly on these rfnIdentifiers.
        // Instead, request will be run on their primary nodes (PRIMARY_GATEWAY_NODES). 

    private Set<RfnIdentifier> rfnIdentifiers;
        // can contain any RfnIdentifier (gateway, node, relay, etc.)
    
    private Set<RfnMetadataMulti> rfnMetadatas;

    public void setRfnIdentifiers(RfnIdentifier... rfnIdentifiers) {
        setRfnIdentifiers(new HashSet<>(Arrays.asList(rfnIdentifiers)));
    }
    
    public void setRfnMetadatas(RfnMetadataMulti... rfnMetadatas) {
        setRfnMetadatas(new HashSet<>(Arrays.asList(rfnMetadatas)));
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Set<RfnIdentifier> getPrimaryNodesForGatewayRfnIdentifiers() {
        return primaryNodesForGatewayRfnIdentifiers;
    }

    public void setPrimaryNodesForGatewayRfnIdentifiers(Set<RfnIdentifier> primaryNodesForGatewayRfnIdentifiers) {
        this.primaryNodesForGatewayRfnIdentifiers = primaryNodesForGatewayRfnIdentifiers;
    }

    public Set<RfnIdentifier> getRfnIdentifiers() {
        return rfnIdentifiers;
    }

    public void setRfnIdentifiers(Set<RfnIdentifier> rfnIdentifiers) {
        this.rfnIdentifiers = rfnIdentifiers;
    }

    public Set<RfnMetadataMulti> getRfnMetadatas() {
        return rfnMetadatas;
    }

    public void setRfnMetadatas(Set<RfnMetadataMulti> rfnMetadatas) {
        this.rfnMetadatas = rfnMetadatas;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((primaryNodesForGatewayRfnIdentifiers == null) ? 0
                : primaryNodesForGatewayRfnIdentifiers.hashCode());
        result = prime * result + ((requestID == null) ? 0 : requestID.hashCode());
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
        if (primaryNodesForGatewayRfnIdentifiers == null) {
            if (other.primaryNodesForGatewayRfnIdentifiers != null)
                return false;
        } else if (!primaryNodesForGatewayRfnIdentifiers
            .equals(other.primaryNodesForGatewayRfnIdentifiers))
            return false;
        if (requestID == null) {
            if (other.requestID != null)
                return false;
        } else if (!requestID.equals(other.requestID))
            return false;
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
        return String
            .format("RfnMetadataMultiRequest [requestID=%s, primaryNodesForGatewayRfnIdentifiers=%s, rfnIdentifiers=%s, rfnMetadatas=%s]",
                    requestID,
                    primaryNodesForGatewayRfnIdentifiers,
                    rfnIdentifiers,
                    rfnMetadatas);
    }
}