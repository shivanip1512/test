package com.cannontech.web.tools.mapping.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.geojson.FeatureCollection;
import org.joda.time.Instant;

import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.util.tree.Node;
import com.cannontech.web.tools.mapping.model.NmNetworkException;

public interface NetworkTreeService {
    
    /**
     * Returns root elements of the tree representing routes to gateways.
     */
    List<Node<Pair<Integer, FeatureCollection>>> getNetworkTree(List<Integer> gatewayIds)
            throws NmNetworkException, NmCommunicationException;
    
    /**
     * Sends request to NM to update Network Tree. Returns true if the request was sent.
     */
    boolean requestNetworkTreeUpdate();

    /**
     * Returns true if it is possible to send request to NM to update Network Tree.
     */
    boolean isNetworkTreeUpdatePossible();

    /**
     * Returns Network Tree update time
     */
    Instant getNetworkTreeUpdateTime();

    /**
     * Returns true if Network Tree was updated after lastUpdateTime.
     */
    boolean isNetworkTreeUpdated(Instant lastUpdateTime);
}
