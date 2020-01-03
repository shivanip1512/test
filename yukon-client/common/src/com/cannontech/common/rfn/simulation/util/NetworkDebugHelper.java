package com.cannontech.common.rfn.simulation.util;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.util.tree.Node;

public class NetworkDebugHelper {

    /**
     * Returns node count
     */
    public static int count(RfnVertex vertex) {
        AtomicInteger atomicInt = new AtomicInteger(1);
        count(vertex, atomicInt);
        return atomicInt.get();
        
    }
    
    /**
     * Returns node count
     */
    public static int count(Node<RfnIdentifier> node) {
        AtomicInteger atomicInt = new AtomicInteger(1);
        count(node, atomicInt);
        return atomicInt.get();
        
    }
    
    private static void count(Node<RfnIdentifier> node, AtomicInteger atomicInt) {
        for (Iterator<Node<RfnIdentifier>> it = node.getChildren().iterator(); it.hasNext();) {
            atomicInt.incrementAndGet();
            count(it.next(), atomicInt);
        }
    }

    private static void count(RfnVertex vertex, AtomicInteger atomicInt) {
        if(vertex.getChildren() == null) {
            return;
        }
        for (Iterator<RfnVertex> it = vertex.getChildren().iterator(); it.hasNext();) {
            atomicInt.incrementAndGet();
            count(it.next(), atomicInt);
        }
    }
    
    /**
     * Returns a string representation of vertex
     */
    public static String print(RfnVertex root) {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "", root);
        return buffer.toString();
    }

    private static String print(StringBuilder buffer, String prefix, String childrenPrefix, RfnVertex vertex) {
        buffer.append(prefix);
        buffer.append(vertex.getRfnIdentifier());
        buffer.append('\n');
        if (vertex.getChildren() != null) {
            for (Iterator<RfnVertex> it = vertex.getChildren().iterator(); it.hasNext();) {
                print(buffer, childrenPrefix + "*", childrenPrefix + "I   ", it.next());
            }
        }
        return buffer.toString();
    }
}
