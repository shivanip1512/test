package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * RfnMetadataMultiRequest provides some new features for a better performance.
 * <ul>
 * <li> You can specify 1 or more queries in metadata.</li>
 * <li> You can specify 1 or more rfnIdentifiers including gateway, node, relay, etc.</li>
 * <li> You can specify 1 or more primaryForwardNodesForGatewayRfnIdentifiers(i.e. gateways)
 *      as "group" names of rfnIdentifiers. All nodes under the gateway
 *      (but not the gateway itself) will be added to rfnIdentifiers before query.
 *      Yes, you can specify a combination of rfnIdentifiers and primaryForwardNodesForGatewayRfnIdentifiers.
 *      Note: the query will apply to the final combined rfnIdentifiers;
 *            the query won't apply to primaryForwardNodesForGatewayRfnIdentifiers themselves.</li>
 * <li> Response rfnIdentifiers match to request rfnIdentifiers.
 *      For example, if your request finally has 10 rfnIdentifiers,
 *      you will get the same 10 rfnIdentifiers back, each with its own result.
 *      rfnIdentifier as a key in either request or response can't be null but can be "_EMPTY_".
 *      Note: NM treats "_EMPTY_" as a normal non-null rfnIdentifier
 *            but Yukon treats "_EMPTY_" same as a null rfnIdentifier.</li>
 * </ul>
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.metadatamulti.request
 */
public class RfnMetadataMultiRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String requestID; // to correlate response to request
    
    // NM first retrieves nodes from primaryForwardNodesForGatewayRfnIdentifiers
    //     and added to rfnIdentifiers.
    // You can think primaryForwardNodesForGatewayRfnIdentifiers are just "group" names for rfnIdentifiers.
    //
    // Usually you will specify either groups or rfnIdentifiers.
    // However you can also combine them as you want.
    // NM will add the rfnIdentifiers retrieved from the "groups" (PRIMARY_GATEWAY_NODES)
    //     to the given set of rfnIdentifiers before the query.
    // Note: MetadataMulti query will always be run on the final set of rfnIdentifiers.
    // The response, the keySet() of the map, also matches the final set of rfnIdentifiers.

    private Set<RfnMetadataMulti> rfnMetadatas;
    
    private Set<RfnIdentifier> rfnIdentifiers;
    
    private Set<RfnIdentifier> primaryForwardNodesForGatewayRfnIdentifiers;
    
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