package com.cannontech.common.rfn.simulation.util;

import java.util.Iterator;

import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.util.tree.Node.TreeDebugStatistics;

public class NetworkDebugHelper {    
    /**
     * Returns node count statistics
     */
    public static TreeDebugStatistics count(RfnVertex vertex) {
        TreeDebugStatistics statistics = new TreeDebugStatistics();
        //gateway
        statistics.TOTAL = 1;
        count(vertex, statistics);
        return statistics;
        
    }
    
    /**
     * Creates statistics for the node
     */
    private static void count(RfnVertex vertex, TreeDebugStatistics statistics) {
        if(vertex.getChildren() == null) {
            return;
        }
        for (Iterator<RfnVertex> it = vertex.getChildren().iterator(); it.hasNext();) {
            RfnVertex node = it.next();
            statistics.TOTAL++;
            if (node.getRfnIdentifier() == null) {
                statistics.NULL++;
            } else if (node.getRfnIdentifier().is_Empty_()) {
                statistics._EMPTY_++;
            }
            count(node, statistics);
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
