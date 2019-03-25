package com.cannontech.web.tools.mapping.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.NodeData;
import com.cannontech.common.rfn.message.metadatamulti.PrimaryGatewayComm;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReply;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReplyType;
import com.cannontech.common.rfn.message.network.RfnNeighborDataRequest;
import com.cannontech.common.rfn.message.network.RfnParentReply;
import com.cannontech.common.rfn.message.network.RfnParentReplyType;
import com.cannontech.common.rfn.message.network.RfnParentRequest;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReply;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReplyType;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataRequest;
import com.cannontech.common.rfn.message.network.RouteData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.tools.mapping.model.MappingInfo;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NmNetworkServiceImpl implements NmNetworkService {

    private static final Logger log = YukonLogManager.getLogger(NmNetworkServiceImpl.class);
    private static final String routeRequest = "NM_NETWORK_ROUTE_REQUEST";
    private static final String parentRequest = "NM_NETWORK_PARENT_REQUEST";
    private static final String neighborRequest = "NM_NETWORK_NEIGHBOR_REQUEST";

    private static final String commsError =
        "Unable to send request due to a communication error between Yukon and Network Manager.";
    private static final String nmError = "Received error from Network Manager.";
    private static final String noRoute = "One or more devices within the route could not be located.";
    private static final String noParent = "No location in Yukon was found for this parent device.";

    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.network.data.request";
 
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private RfnGatewayDataCache gatewayDataCache;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private MeterDao meterDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    private RequestReplyTemplate<RfnPrimaryRouteDataReply> routeReplyTemplate;
    private RequestReplyTemplate<RfnNeighborDataReply> neighborReplyTemplate;
    private RequestReplyTemplate<RfnParentReply> parentReplyTemplate;

    @PostConstruct
    public void init() {
        routeReplyTemplate =
            new RequestReplyTemplateImpl<>(routeRequest, configSource, connectionFactory, requestQueue, false);
        neighborReplyTemplate =
            new RequestReplyTemplateImpl<>(neighborRequest, configSource, connectionFactory, requestQueue, false);
        parentReplyTemplate =
            new RequestReplyTemplateImpl<>(parentRequest, configSource, connectionFactory, requestQueue, false);
    }
    
    public class Neighbors {
        private List<Neighbor> neighbors = new ArrayList<>();
        private List<RfnDevice> neighborsWithoutLocation = new ArrayList<>();

        public Neighbors(List<Neighbor> neighbors, List<RfnDevice> neighborsWithoutLocation) {
            this.neighbors = neighbors;
            this.neighborsWithoutLocation = neighborsWithoutLocation;
        }

        public List<Neighbor> getNeighbors() {
            return neighbors;
        }

        public List<RfnDevice> getNeighborsWithoutLocation() {
            return neighborsWithoutLocation;
        }
    }
    
    public class Route {
        private List<RouteInfo> route = new ArrayList<>();
        private RfnDevice deviceWithoutLocation = null;
        
        public Route(List<RouteInfo> route, RfnDevice deviceWithoutLocation) {
            this.route = route;
            this.deviceWithoutLocation = deviceWithoutLocation;
        }

        public RfnDevice getDeviceWithoutLocation() {
            return deviceWithoutLocation;
        }

        public List<RouteInfo> getRoute() {
            return route;
        }
    }
    
    @Override
    public Parent getParent(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        PaoLocation deviceLocation = paoLocationDao.getLocation(deviceId);
        BlockingJmsReplyHandler<RfnParentReply> reply = new BlockingJmsReplyHandler<>(RfnParentReply.class);
        RfnParentRequest request = new RfnParentRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());

        log.debug("Sending get parent request to Network Manager: " + request);

        RfnParentReply response;
        try {
            parentReplyTemplate.send(request, reply);
            response = reply.waitForCompletion();
        } catch (ExecutionException e) {
            log.error(commsError, e);
            throw new NmNetworkException(commsError, e, "commsError");
        }
        if (response.getReplyType() != RfnParentReplyType.OK) {
            log.error(nmError + " (" + response.getReplyType() + ")");
            throw new NmNetworkException(nmError, response.getReplyType().name());
        }

        log.debug("response: " + response);

        ParentData data = response.getParentData();
        try {
            RfnDevice parentDevice = rfnDeviceDao.getDeviceForExactIdentifier(data.getRfnIdentifier());
            PaoLocation parentLocation = paoLocationDao.getLocation(parentDevice.getPaoIdentifier().getPaoId());
            if (parentLocation == null) {
                log.error("No parent found for device=" + device);
                throw new NmNetworkException(noParent, "noParentLocation");
            }
            FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(parentLocation));
            Parent parent = new Parent(parentDevice, location, data, accessor);
            parent.setDeviceDetailUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(parentDevice));
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = getMetaData(Lists.newArrayList(parentDevice));
            addMappingInfo(metaData, parent);
            addDistance(parent, deviceLocation, parentLocation);
            log.debug(parent);
            log.debug("-----" + deviceLocation + " <<>> " + parentLocation);
            return parent;
        } catch (NotFoundException e) {
            // create new device if it doesn't exist
            rfnDeviceCreationService.create(data.getRfnIdentifier());
            log.info(data.getRfnIdentifier() + " is not found. Creating device.");
            log.error("No parent found for device=" + device);
            throw new NmNetworkException(noParent, "noParentLocation");
        }
    }
    
    @Override
    public Route getRoute(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        RfnPrimaryRouteDataRequest request = new RfnPrimaryRouteDataRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());

        log.debug("Sending get route request to Network Manager: " + request);

        BlockingJmsReplyHandler<RfnPrimaryRouteDataReply> reply =
            new BlockingJmsReplyHandler<>(RfnPrimaryRouteDataReply.class);
        RfnPrimaryRouteDataReply response;
        try {
            routeReplyTemplate.send(request, reply);
            response = reply.waitForCompletion();
            if (response.getReplyType() != RfnPrimaryRouteDataReplyType.OK) {
                log.error(nmError + " (" + response.getReplyType() + ")");
                throw new NmNetworkException(nmError, response.getReplyType().name());
            }

        } catch (ExecutionException e) {
            log.error(commsError, e);
            throw new NmNetworkException(commsError, e, "commsError");
        }

        log.debug("response: " + response);

        Map<RouteData, RfnDevice>  dataToDevice = response.getRouteData().stream()
                .collect(Collectors.toMap(data -> data, data -> findDevice(data.getRfnIdentifier())));
            
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = getMetaData(dataToDevice.values());

        Set<PaoLocation> allLocations = paoLocationDao.getLocations(dataToDevice.values());
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());
        PaoLocation nextHopLocation = null;
        List<RouteInfo> routes = new ArrayList<>();
        RfnDevice hopWithoutLocation = null;
        
        for (int i = 0; i < response.getRouteData().size(); i++) {
            RouteData data = response.getRouteData().get(i);
            RfnDevice routeDevice = dataToDevice.get(data);
            PaoLocation paoLocation = locations.get(routeDevice.getPaoIdentifier());
            if (paoLocation != null) {
                FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
                RouteInfo routeInfo = new RouteInfo(routeDevice, data, location, accessor);
                routeInfo.setDeviceDetailUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(routeDevice));
                // the first element shows the distance from the first element to the 2nd element
                // only the last element has no distance, because it has no "next hop"
                if (i < response.getRouteData().size() - 1) {
                    RouteData nextHop = response.getRouteData().get(i + 1);
                    RfnDevice nextHopDevice = dataToDevice.get(nextHop);
                    PaoIdentifier nextHopPaoIdentifier = nextHopDevice.getPaoIdentifier();
                    nextHopLocation = locations.get(nextHopPaoIdentifier);
                    if(nextHopLocation == null){
                        hopWithoutLocation = nextHopDevice;
                    }
                    addDistance(routeInfo, paoLocation, nextHopLocation);
                }
                addMappingInfo(metaData, routeInfo);
                routes.add(routeInfo);
                log.debug(routeInfo);
                if(nextHopLocation == null){
                    break;
                }
            } else {
                log.error("Location is not found for " + routeDevice);
                // one of the devices has no location, can't display a route
                throw new NmNetworkException(noRoute, "noRoute");
            }
        }
        if(routes.isEmpty()){
            // one of the devices has no location, can't display a route
            throw new NmNetworkException(noRoute, "noRoute"); 
        }
        return new Route(routes, hopWithoutLocation);
    }

    @Override
    public Neighbors getNeighbors(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        //the main entity (in which all neighbors are related to) shows no distance
        PaoLocation deviceLocation = paoLocationDao.getLocation(deviceId);
        RfnNeighborDataRequest request = new RfnNeighborDataRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());

        log.debug("Sending get neighbors request to Network Manager: " + request);

        BlockingJmsReplyHandler<RfnNeighborDataReply> reply = new BlockingJmsReplyHandler<>(RfnNeighborDataReply.class);

        RfnNeighborDataReply response = null;
        try {
            neighborReplyTemplate.send(request, reply);
            response = reply.waitForCompletion();
            if (response.getReplyType() != RfnNeighborDataReplyType.OK) {
                log.error(nmError + " (" + response.getReplyType() + ")");
                throw new NmNetworkException(nmError, response.getReplyType().name());
            }
        } catch (ExecutionException e) {
            log.error(commsError, e);
            throw new NmNetworkException(commsError, e, "commsError");
        }

        Map<NeighborData, RfnDevice>  dataToDevice = response.getNeighborData().stream()
            .collect(Collectors.toMap(data -> data, data -> findDevice(data.getRfnIdentifier())));
        
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = getMetaData(dataToDevice.values());
        
        Set<PaoLocation> allLocations = paoLocationDao.getLocations(dataToDevice.values());
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());
        List<RfnDevice> neighborsWithoutLocation = new ArrayList<>();
        List<Neighbor> neighbors = new ArrayList<>();        
        for (NeighborData data : response.getNeighborData()) {
            RfnDevice neighborDevice = dataToDevice.get(data);
            if (neighborDevice != null) {
                PaoLocation neighborLocation = locations.get(neighborDevice.getPaoIdentifier());
                if (neighborLocation != null) {
                    FeatureCollection location =
                        paoLocationService.getFeatureCollection(Lists.newArrayList(neighborLocation));
                    Neighbor neighbor = new Neighbor(neighborDevice, location, data, accessor);
                    neighbor.setDeviceDetailUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(neighborDevice));
                    addMappingInfo(metaData, neighbor);
                    // distance is from device to each neighbor
                    addDistance(neighbor, deviceLocation, neighborLocation);
                    log.debug(neighbor);
                    log.debug("-----" + deviceLocation + "-" + neighborLocation);
                    neighbors.add(neighbor);
                } else {
                    neighborsWithoutLocation.add(neighborDevice);
                    log.error("Location is not found for " + neighborDevice);
                }
            }
        }
        return new Neighbors(neighbors, neighborsWithoutLocation);
    }

    /**
     * Sends meta data request to NM for devices other then gateways
     */
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetaData(Collection<RfnDevice> devices)
            throws NmNetworkException {
        Set<RfnIdentifier> devicesOtherThenGatways = devices.stream()
                .filter(rfnDevice -> !rfnDevice.getPaoIdentifier().getPaoType().isRfGateway())
                .map(rfnDevice -> rfnDevice.getRfnIdentifier())
                .collect(Collectors.toSet());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = null;
        try {
            metaData = metadataMultiService.getMetadata(devicesOtherThenGatways, Set.of(RfnMetadataMulti.PRIMARY_GATEWAY_COMM, RfnMetadataMulti.NODE_DATA));
        } catch (NmCommunicationException e) {
            //Carrie
            throw new NmNetworkException(commsError, e, "commsError");
        }
        return metaData;
    }

    private void addMappingInfo(Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MappingInfo info) {
        PaoIdentifier pao = info.getDevice().getPaoIdentifier();
        if(pao.getPaoType().isRfGateway()) {
            addGatewayInfo(info);
        } else {
            addMetadata(info, metaData);
        }
        if(pao.getPaoType().isMeter()) {
            YukonMeter meter = meterDao.getForId(pao.getPaoId());
            info.setMeterNumber(meter.getMeterNumber());
        }
    }
    
    private void addGatewayInfo(MappingInfo info) {
        try {
            RfnGatewayData gateway = gatewayDataCache.get(info.getDevice().getPaoIdentifier());
            info.setConnectionStatus(gateway.getConnectionStatus());
            info.setIpAddress(gateway.getIpAddress());
            info.setMacAddress(gateway.getMacAddress());
        } catch (NmCommunicationException e) {
            // ignore, status will be set to "UNKNOWN"
            log.error("Failed to get gateway data for " + info.getDevice(), e);
        }
    }
    
    /**
     * Adds metadata received from NM to mapping info
     */
    private void addMetadata(MappingInfo info, Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData) {
        RfnMetadataMultiQueryResult metadata = metaData.get(info.getDevice().getRfnIdentifier());
        if (metadata.getResultType() != RfnMetadataMultiQueryResultType.OK) {
            log.error("NM returned query result:" + metadata.getResultType() + " message:" + metadata.getResultMessage()
                + " for device:" + info.getDevice());
            return;
        }
        PrimaryGatewayComm comm = (PrimaryGatewayComm) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_COMM);
        if (comm != null) {
            info.setStatus(comm.getCommStatusType());
        } else {
            // ignore, status will be set to "UNKNOWN"
            log.error("NM didn't return communication status for " + info.getDevice());
        }
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(findDevice(comm.getRfnIdentifier()).getPaoIdentifier().getPaoId());
        info.setPrimaryGateway(gateway.getNameWithIPAddress());
        info.setPrimaryGatewayUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(gateway));
        NodeData nodeData = (NodeData) metadata.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
        if (nodeData != null) {
            info.setMacAddress(nodeData.getMacAddress());
        } else {
            log.error("NM didn't return node data for " + info.getDevice());
        }        
    }

    /**
     * Calculates distance between 2 locations and adds it to MappingInfo.
     */
    private void addDistance(MappingInfo info, PaoLocation from, PaoLocation to) {
        if (from == null || to == null) {
            return;
        }
        Distance distance = getDistance(from, to);
        info.setDistanceInKm(distance.distanceInKm);
        info.setDistanceInMiles(distance.distanceInMiles);
    }

    /**
     * Attempts to lookup a device by identifier or creates a new device if it was not
     * found.
     */
    private RfnDevice findDevice(RfnIdentifier identifier) {
        RfnDevice rfnDevice = null;
        if (identifier != null) {
            try {
                rfnDevice = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
            } catch (NotFoundException e) {
                try {
                    rfnDevice = rfnDeviceCreationService.create(identifier);
                    log.info(identifier + " is not found. Creating device.");
                } catch (DeviceCreationException e1) {
                    log.error("Device creation failed for " + identifier, e1);
                }
            }
        }
        return rfnDevice;
    }
    
    /**
     * "Distance to Next Hop" is calculated from the location (lat/lon) of the current node and the location of the next hop node/gateway
     * 
     * (Copy from NM - EntityMap.java)
     * Based on the Haversine formula
     * (http://en.wikipedia.org/wiki/Haversine_formula) Source code adapted
     * from: http://www.movable-type.co.uk/scripts/latlong.html
     * 
     * @param loc1
     * @param loc2
     * @return distance in miles and km
     */
    public Distance getDistance(PaoLocation loc1, PaoLocation loc2) {
        double earthRadius = 6371; // in km

        double lati1 = loc1.getLatitude();
        double long1 = loc1.getLongitude();
        double lati2 = loc2.getLatitude();
        double long2 = loc2.getLongitude();

        double dLat = Math.toRadians(lati2 - lati1);
        double dLon = Math.toRadians(long2 - long1);
        double lat1 = Math.toRadians(lati1);
        double lat2 = Math.toRadians(lati2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInKm = earthRadius * c;

        // 1 km = 0.621371 miles
        double distanceInMiles = distanceInKm * 0.621371;
        return new Distance(distanceInKm, distanceInMiles);
    }
    
    private class Distance{
        double distanceInKm;
        double distanceInMiles; 
        public Distance(double distanceInKm, double distanceInMiles) {
            this.distanceInKm = distanceInKm;
            this.distanceInMiles = distanceInMiles;
        }
    }
}
