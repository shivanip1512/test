package com.cannontech.common.rfn.message.tree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

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
 * 
 * "We are trying to keep limit the scope of the RfnVertex API down to just what is needed for conveying the network tree.
 *  Attaching associated data to any of the nodes/vertices is outside that scope."
 *      -- Manny's email to Marina Mon 12/9/2019 7:43 PM
 */
public class RfnVertex implements Serializable  {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    
    private RfnVertex parent;
    
    private Set<RfnVertex> children;
    
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public RfnVertex getParent() {
        return parent;
    }

    public void setParent(RfnVertex parent) {
        this.parent = parent;
    }


    public Set<RfnVertex> getChildren() {
        return children;
    }

    public void setChildren(Set<RfnVertex> children) {
        this.children = children;
    }
    

    public static void main(String[] args) {

        buildTheSampleTree();
        
        /**
        // The following shows examples to query PRIMARY_FORWARD_TREE using RfnMetadataMultiRequest
         
        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        request.setRfnMetadatas(RfnMetadataMulti.PRIMARY_FORWARD_TREE);
        
        // You can query PRIMARY_FORWARD_TREE for a gateway.
        // NM will respond with only the root/gateway RfnVertex of the network tree.  
        request.setRfnIdentifiers(new RfnIdentifier("Gateway2", "CPS", "RFGateway2"));
        
        // You can also query PRIMARY_FORWARD_TREE for a device.
        // NM will respond with only the device RfnVertex.
        // Note: whenever you get any vertex in a tree, you actually get the whole tree structure.
        // request.setRfnIdentifiers(new RfnIdentifier("N1", "ITRN", "C2SX"));
        */
    }
    
    public static void buildTheSampleTree() { // As Marina requested
        
        RfnVertex gateway2Vertex = new RfnVertex();
        gateway2Vertex.setRfnIdentifier(new RfnIdentifier("Gateway2", "CPS", "RFGateway2"));
        
        RfnVertex n1Vertex = new RfnVertex();
        n1Vertex.setRfnIdentifier(new RfnIdentifier("N1", "ITRN", "C2SX"));
        
        RfnVertex n2Vertex = new RfnVertex();
        n2Vertex.setRfnIdentifier(new RfnIdentifier("N2", "ITRN", "C2SX"));

        RfnVertex n3Vertex = new RfnVertex();
        n3Vertex.setRfnIdentifier(new RfnIdentifier("N3", "ITRN", "C2SX"));

        RfnVertex n4Vertex = new RfnVertex();
        n4Vertex.setRfnIdentifier(new RfnIdentifier("N4", "ITRN", "C2SX"));

        RfnVertex n5Vertex = new RfnVertex();
        n5Vertex.setRfnIdentifier(new RfnIdentifier("N5", "ITRN", "C2SX"));

        RfnVertex n6Vertex = new RfnVertex();
        n6Vertex.setRfnIdentifier(new RfnIdentifier("N6", "ITRN", "C2SX"));

        gateway2Vertex.setParent(null);
        Set<RfnVertex> children = new HashSet<>();
        children.add(n2Vertex);
        children.add(n3Vertex);
        gateway2Vertex.setChildren(children);
        
        n1Vertex.setParent(n2Vertex);
        children = new HashSet<>();
        children.add(n5Vertex);
        children.add(n6Vertex);
        n1Vertex.setChildren(children);

        n2Vertex.setParent(gateway2Vertex);
        children = new HashSet<>();
        children.add(n1Vertex);
        children.add(n4Vertex);
        n2Vertex.setChildren(children);
        
        n3Vertex.setParent(gateway2Vertex);
        n3Vertex.setChildren(null);
        
        n4Vertex.setParent(n2Vertex);
        n4Vertex.setChildren(null);

        n5Vertex.setParent(n1Vertex);
        n5Vertex.setChildren(null);

        n6Vertex.setParent(n1Vertex);
        n6Vertex.setChildren(null);
        
        print(gateway2Vertex);
    }
    
    // The following function is just help to display how to build the sample tree.
    // To make this shared messaging class simple, we suggest to move any helper function like the following
    //     which navigates the tree into an utility class outside.
    public static void print(RfnVertex rfnVertex) {
        System.out.println(rfnVertex.getRfnIdentifier());
        Set<RfnVertex> children = rfnVertex.getChildren();
        if (children != null) {
            System.out.println("{");
            for (RfnVertex child : children) {
                print(child);
            }
            System.out.println("}");
        }
    }
}
