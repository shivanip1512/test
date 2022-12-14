package com.cannontech.web.tools.mapping.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.web.tools.mapping.model.NetworkMap;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter;
import com.cannontech.web.tools.mapping.model.NmNetworkException;

public interface NmNetworkService {

    /**
     * Asks NM for the device route.
     * 
     * If one of the devices in the route returned by MN have no location information in Yukon, this method will return
     * empty list.
     * Devices that do not exist in Yukon will be created.
     * 
     * NM can return NO_PARENT if primary route information is requested for battery node(water meter).
     * NM searches for the battery node's parent first, then finds the parent's primary route and returns that as the
     * battery node's primary route.
     * 
     * @throws NmCommunicationException 
     */
    List<Pair<RfnDevice, FeatureCollection>> getRoute(int deviceId, MessageSourceAccessor accessor) throws NmCommunicationException;

    /**
     * Asks NM for the device neighbors.
     * 
     * If NM returns a neighbor device that doesn't exist in Yukon, the device will be created, this neighbor device will
     * NOT be returned by this method since there is no location in Yukon for this device.
     * 
     * If neighbor device has no location information in Yukon the neighbor will not be returned.
     * If all the neighbor devices received from NM have no location information in Yukon, empty list is returned.
     * 
     * @return Neighbors - neighbor list and list of neighbor devices without location
     * @throws NmCommunicationException 
     */
    List<Pair<RfnDevice, FeatureCollection>> getNeighbors(int deviceId, MessageSourceAccessor accessor) throws NmCommunicationException;

    /**
     * Asks NM for the parent information.
     * 
     * If NM returns a parent device that Yukon doesn't have created, the device will be created.
     * 
     * @throws NmCommunicationException 
     */
    Pair<RfnDevice, FeatureCollection> getParent(int deviceId, MessageSourceAccessor accessor) throws NmCommunicationException;
    
    /**
     * Returns a network map representation (legend and a list of devices by color).
     * @throws NmNetworkException, nmCommunicationException  
     */
    NetworkMap getNetworkMap(NetworkMapFilter filter, MessageSourceAccessor accessor) throws NmNetworkException, NmCommunicationException;

    /**
     * Parses the result we received from NM to get Node Comm Status
     */
    NodeComm getNodeCommStatusFromMultiQueryResult(RfnDevice rfnDevice, RfnMetadataMultiQueryResult metadata);
}
