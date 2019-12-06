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
 * </pre>
 */
public class RfnRoute extends LinkedList<RfnIdentifier> {

    private static final long serialVersionUID = 1L;
    
    // The following shows an example to query PRIMARY_FORWARD_ROUTE using RfnMetadataMultiRequest
    public static void main(String[] args) {

        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        
        // You want to query primary-route-to-gateway
        request.setRfnMetadatas(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE);

        // for each device for a Gateway.
        request.setPrimaryForwardNodesForGatewayRfnIdentifiers(
                                  new RfnIdentifier("7800000005", "CPS", "RFGateway2"));
        // Or for an individual device.
        request.setRfnIdentifiers(new RfnIdentifier("88638059", "ITRN", "C2SX"));
        
        // In response, NM will assign a RfnRoute object (a link-list of RfnIdentifiers) for each device.
        // Let's assume the following rfnRoute is for N4.
        RfnIdentifier rfnIdentifier = (RfnIdentifier) new Object(); // map.entry.getKey();
        RfnRoute      rfnRoute      = (RfnRoute)      new Object(); // map.entry.getValue(()
        
        // To display the primary route to gateway for N4 shown on YUK-20021: 
        //     Primary Route to Gateway: N4 > N2 > Gateway2. 
        System.out.println("Primary Route to Gateway: ");
        System.out.println(rfnIdentifier); // N4
        if (rfnRoute != null) {
            for (RfnIdentifier parent: rfnRoute) {
                System.out.println(" > ");
                System.out.println(parent);
            }
        }
        
        // You can also show a gateway network tree in the Comprehensive Map by displaying routes
        // for all devices under the gateway   
    }
}
