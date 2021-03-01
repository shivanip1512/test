package com.cannontech.common.rfn.message.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * <pre>
 *                  Gateway2
 *                 /        \
 *                /          \
 *               N1          N2
 *              /  \ 
 *             /    \
 *            N3    N4
 *           /  \
 *          /    \
 *         N5    N6
 * </pre>
 * 
 * @author lizhu2
 */
public class RfnVertex implements Serializable  {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    
    private RfnVertex parent;
    
    private final Set<RfnVertex> children = new LinkedHashSet<>();
    
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

    public RfnVertex getRoot() {
        RfnVertex root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }
    
    public void addChild(RfnVertex child) {
        children.add(child);
    }
    
    public void addChildren(Collection<RfnVertex> children) {
        children.addAll(children);
    }

    @Override
    public String toString() {
        return String.format("%s%s", rfnIdentifier, children);
    }

    public static void main(String[] args) {

        buildTheSampleTree();
        
        /**
        // The following shows examples to query PRIMARY_FORWARD_TREE using RfnMetadataMultiRequest
         
        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        request.setRfnMetadatas(RfnMetadataMulti.PRIMARY_FORWARD_TREE);
        
        // You can query PRIMARY_FORWARD_TREE for a gateway.
        // NM will respond with only the root/gateway RfnVertex of the network tree.  
        request.setRfnIdentifiers(new RfnIdentifier("Gateway2", "EATON", "GWY800"));
        
        // You can also query PRIMARY_FORWARD_TREE for a device.
        // NM will respond with only the device RfnVertex.
        // Note: whenever you get any vertex in a tree, you actually get the whole tree structure.
        // request.setRfnIdentifiers(new RfnIdentifier("N1", "ITRN", "C2SX"));
        */
    }
    
    public static void buildTheSampleTree() { // As Marina requested
        
        RfnVertex gateway2Vertex = new RfnVertex();
        gateway2Vertex.setRfnIdentifier(new RfnIdentifier("Gateway2", "EATON", "GWY800"));
        
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

        gateway2Vertex.addChild(n1Vertex);
        gateway2Vertex.addChild(n2Vertex);
        
        n1Vertex.setParent(gateway2Vertex);
        n1Vertex.addChild(n3Vertex);
        n1Vertex.addChild(n4Vertex);
        
        n2Vertex.setParent(gateway2Vertex);

        n3Vertex.setParent(n1Vertex);
        n3Vertex.addChild(n5Vertex);
        n3Vertex.addChild(n6Vertex);
        
        n4Vertex.setParent(n1Vertex);
        
        n5Vertex.setParent(n3Vertex);
        
        n6Vertex.setParent(n3Vertex);
        
        System.out.println(gateway2Vertex);
    }
}
