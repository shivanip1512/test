package com.cannontech.web.tools.mapping.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.geojson.FeatureCollection;

import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.util.tree.Node;
import com.cannontech.web.tools.mapping.model.NmNetworkException;

public interface NetworkTreeService {
    
    /**
     * Returns root elements of the tree representing routes to gateways.
     */
    List<Node<Pair<Integer, FeatureCollection>>> getNetworkTree(List<Integer> gatewayIds)
            throws NmNetworkException, NmCommunicationException;
}
