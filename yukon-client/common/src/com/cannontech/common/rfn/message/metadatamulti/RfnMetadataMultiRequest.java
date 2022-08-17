package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * RfnMetadataMultiRequest provides some new features for a better performance.
 * <ul>
 * <li> You can specify 1 or more metadata.
 * <li> You can specify 1 or more rfnIdentifiers including gateway, node, relay, etc.
 * <li> You can specify 1 or more primaryForwardNodesForGatewayRfnIdentifiers as "group" names of rfnIdentifiers.
 *      All nodes under the gateway (but not gateway itself) will be added to rfnIdentifiers.
 * </ul>
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.metadatamulti.request
 */
public class RfnMetadataMultiRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String requestID; // to correlate response to request
    
    // NM first retrieves primary nodes from primaryForwardNodesForGatewayRfnIdentifiers
    //     and added to rfnIdentifiers.
    // You can think primaryForwardNodesForGatewayRfnIdentifiers are just "group" names for rfnIdentifiers.
    //
    // Usually you will specify either groups or rfnIdentifiers.
    // However you can also combine them (not usual) as you want.
    // NM will combine the rfnIdentifiers retrieved from the "groups" (PRIMARY_GATEWAY_NODES)
    //     with the given set of rfnIdentifiers before the query.
    // Note: MetadataMulti query will always be run on the final set of rfnIdentifiers.
    // The response, the keySet() of the map, also matches the final set of rfnIdentifiers.

    private Set<RfnMetadataMulti> rfnMetadatas;
    
    private Set<RfnIdentifier> rfnIdentifiers;
        // Request applies to all devices or gateways in specified rfnIdentifiers.
    
    private Set<RfnIdentifier> primaryForwardNodesForGatewayRfnIdentifiers;
        // Request applies to all devices using primaryForwardNodesForGatewayRfnIdentifiers
        // as their primary forward gateways.
    
    public void setRfnMetadatas(RfnMetadataMulti... rfnMetadatas) {
        setRfnMetadatas(new HashSet<>(Arrays.asList(rfnMetadatas)));
    }

    public void setRfnIdentifiers(RfnIdentifier... rfnIdentifiers) {
        setRfnIdentifiers(new HashSet<>(Arrays.asList(rfnIdentifiers)));
    }
    
    public void setPrimaryForwardNodesForGatewayRfnIdentifiers(RfnIdentifier... rfnIdentifiers) {
        setPrimaryForwardNodesForGatewayRfnIdentifiers(new HashSet<>(Arrays.asList(rfnIdentifiers)));
    }
    
    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Set<RfnMetadataMulti> getRfnMetadatas() {
        return rfnMetadatas;
    }

    public void setRfnMetadatas(Set<RfnMetadataMulti> rfnMetadatas) {
        this.rfnMetadatas = rfnMetadatas;
    }

    public Set<RfnIdentifier> getRfnIdentifiers() {
        return rfnIdentifiers;
    }

    public void setRfnIdentifiers(Set<RfnIdentifier> rfnIdentifiers) {
        this.rfnIdentifiers = rfnIdentifiers;
    }

    public Set<RfnIdentifier> getPrimaryForwardNodesForGatewayRfnIdentifiers() {
        return primaryForwardNodesForGatewayRfnIdentifiers;
    }

    public void setPrimaryForwardNodesForGatewayRfnIdentifiers(Set<RfnIdentifier> primaryForwardNodesForGatewayRfnIdentifiers) {
        this.primaryForwardNodesForGatewayRfnIdentifiers = primaryForwardNodesForGatewayRfnIdentifiers;
    }

    @Override
    public String toString() {
        return String.format(
                "RfnMetadataMultiRequest [requestID=%s, rfnMetadatas=%s, rfnIdentifiers=%s, primaryForwardNodesForGatewayRfnIdentifiers=%s]",
                requestID, rfnMetadatas, rfnIdentifiers, primaryForwardNodesForGatewayRfnIdentifiers);
    }
}