package com.cannontech.common.rfn.message.route;

import java.util.LinkedList;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;

/**
 * <pre>
 *                  Gateway2
 *                 /        \
 *                /          \
 *               N2          N3
 *              /  \ 
 *             /    \
 *            N1    N4
 *           /  \
 *          /    \
 *         N5    N6
 * </pre>
 */
public class RfnRoute extends LinkedList<RfnIdentifier> {

    private static final long serialVersionUID = 1L;
    
    // The following shows examples to query PRIMARY_FORWARD_ROUTE using RfnMetadataMultiRequest
    public static void main(String[] args) {

        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        request.setRfnMetadatas(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE);
        
        // You can query primary-route-to-gateway for any device.
        // NM will respond with the primary forward route (i.e., the linked list) to gateway of the device
        // For example, on the above tree,
        //     the route of N6 will be [N1, N2, Gateway2];
        //     the route of N1 will be [N2, Gateway2];
        //     the route of N2 will be [Gateway2].
        request.setRfnIdentifiers(new RfnIdentifier("N6", "ITRN", "C2SX"));
        
        // Note: although you can use setPrimaryForwardNodesForGatewayRfnIdentifiers()
        //     to get primary routes for all devices where this gateway is their primary route path,
        //     you will get a ton of path duplication.
        // Instead, we suggest to use PRIMARY_FORWARD_TREE by setRfnIdentifiers() for
        //     that primary route gateway rfnIdentifier where NM will respond with the root/gateway RfnVertex 
        //     which will have all devices in it with their tree relationship to each other.
    }
}
