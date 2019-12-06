package com.cannontech.common.rfn.message.route;

import java.util.HashSet;

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
public class RfnChildren extends HashSet<RfnIdentifier> {

    private static final long serialVersionUID = 1L;
    
    // The following shows an example to query PRIMARY_FORWARD_CHILDREN using RfnMetadataMultiRequest
    public static void main(String[] args) {

        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        
        // You want to query primary forward children
        request.setRfnMetadatas(RfnMetadataMulti.PRIMARY_FORWARD_CHILDREN);

        // for each device for a Gateway.
        request.setPrimaryForwardNodesForGatewayRfnIdentifiers(
                                  new RfnIdentifier("7800000005", "CPS", "RFGateway2"));
        // Or for an individual device.
        request.setRfnIdentifiers(new RfnIdentifier("88638059", "ITRN", "C2SX"));
        
        // In response, NM will assign a RfnChildren object (a set of RfnIdentifiers) for each device.
        // Let's assume the following rfnChildren is for N2.
        RfnIdentifier rfnIdentifier = (RfnIdentifier) new Object(); // map.entry.getKey();
        RfnChildren   rfnChildren   = (RfnChildren)   new Object(); // map.entry.getValue(()
        
        // The following displays the children of N2 are N1, N4.
        // Note: N5 and N6 is descendants of N2 but not children.
        System.out.println("the children of ");
        System.out.println(rfnIdentifier); // N2
        System.out.println(" are ");
        if (rfnChildren != null) {
            for (RfnIdentifier parent: rfnChildren) {
                System.out.println(", ");
                System.out.println(parent);
            }
        }
    }
}
