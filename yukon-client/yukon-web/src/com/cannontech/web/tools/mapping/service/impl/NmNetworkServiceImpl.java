package com.cannontech.web.tools.mapping.service.impl;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
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
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
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
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.tools.mapping.model.MappingInfo;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.NetworkMap;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.Color;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.ColorCodeBy;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.DescendantCount;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.HopCount;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.HopCountColors;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.Legend;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.LinkQuality;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NmNetworkServiceImpl implements NmNetworkService {

    private static final Logger log = YukonLogManager.getLogger(NmNetworkServiceImpl.class);
    private static final String routeRequest = "NM_NETWORK_ROUTE_REQUEST";
    private static final String parentRequest = "NM_NETWORK_PARENT_REQUEST";
    private static final String neighborRequest = "NM_NETWORK_NEIGHBOR_REQUEST";

    private static final String commsError = "Unable to send request due to a communication error between Yukon and Network Manager.";
    private static final String nmError = "Received error from Network Manager.";
    private static final String noRoute = "One or more devices within the route could not be located.";
    private static final String noParent = "No location in Yukon was found for this parent device.";
    private static final String metadataErrorKey = "yukon.web.modules.operator.mapNetwork.exception.metadataError";

    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.network.data.request";

    @Autowired private RfnGatewayService rfnGatewayService;
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
    @Autowired private ServerDatabaseCache dbCache;
    private RequestReplyTemplate<RfnPrimaryRouteDataReply> routeReplyTemplate;
    private RequestReplyTemplate<RfnNeighborDataReply> neighborReplyTemplate;
    private RequestReplyTemplate<RfnParentReply> parentReplyTemplate;

    @PostConstruct
    public void init() {
        routeReplyTemplate = new RequestReplyTemplateImpl<>(routeRequest, configSource, connectionFactory, requestQueue, false);
        neighborReplyTemplate = new RequestReplyTemplateImpl<>(neighborRequest, configSource, connectionFactory, requestQueue,
                false);
        parentReplyTemplate = new RequestReplyTemplateImpl<>(parentRequest, configSource, connectionFactory, requestQueue, false);
    }

    public class Neighbors {
        private List<Neighbor> neighbors = new ArrayList<>();
        private List<RfnDevice> neighborsWithoutLocation = new ArrayList<>();
        private String errorMsg;

        public Neighbors(List<Neighbor> neighbors, List<RfnDevice> neighborsWithoutLocation, String errorMsg) {
            this.neighbors = neighbors;
            this.neighborsWithoutLocation = neighborsWithoutLocation;
            this.errorMsg = errorMsg;
        }

        public List<Neighbor> getNeighbors() {
            return neighbors;
        }

        public List<RfnDevice> getNeighborsWithoutLocation() {
            return neighborsWithoutLocation;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    public class Route {
        private List<RouteInfo> route = new ArrayList<>();
        private RfnDevice deviceWithoutLocation = null;
        private String errorMsg;

        public Route(List<RouteInfo> route, RfnDevice deviceWithoutLocation, String errorMsg) {
            this.route = route;
            this.deviceWithoutLocation = deviceWithoutLocation;
            this.errorMsg = errorMsg;
        }

        public RfnDevice getDeviceWithoutLocation() {
            return deviceWithoutLocation;
        }

        public List<RouteInfo> getRoute() {
            return route;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
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

            try {
                Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = getMetaData(Lists.newArrayList(parentDevice));
                addMappingInfo(metaData, parent);
            } catch (NmNetworkException e) {
                parent.setErrorMsg(accessor.getMessage(metadataErrorKey));
            }

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
        String errorMsg = null;

        log.debug("Sending get route request to Network Manager: " + request);

        BlockingJmsReplyHandler<RfnPrimaryRouteDataReply> reply = new BlockingJmsReplyHandler<>(RfnPrimaryRouteDataReply.class);
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

        Map<RfnIdentifier, RfnDevice> devices = response.getRouteData().stream()
                .map(data -> rfnDeviceCreationService.createIfNotFound(data.getRfnIdentifier()))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(data -> data.getRfnIdentifier(), data -> data));

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = new HashMap<>();
        try {
            metaData = getMetaData(devices.values());
        } catch (NmNetworkException e) {
            errorMsg = accessor.getMessage(metadataErrorKey);
        }

        Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());
        PaoLocation nextHopLocation = null;
        List<RouteInfo> routes = new ArrayList<>();
        RfnDevice hopWithoutLocation = null;

        for (int i = 0; i < response.getRouteData().size(); i++) {
            RouteData data = response.getRouteData().get(i);
            RfnDevice routeDevice = devices.get(data.getRfnIdentifier());
            if (routeDevice == null) {
                log.error("Device {} was not found", data.getRfnIdentifier());
                continue;
            }
            PaoLocation paoLocation = locations.get(routeDevice.getPaoIdentifier());
            if (paoLocation != null) {
                FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
                RouteInfo routeInfo = new RouteInfo(routeDevice, data, location, accessor);
                DynamicRfnDeviceData deviceData = rfnDeviceDao.findDynamicRfnDeviceData(deviceId);
                if(deviceData != null) {
                    routeInfo.setDescendantCount(deviceData.getDescendantCount());
                }
                routeInfo.setDeviceDetailUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(routeDevice));
                // the first element shows the distance from the first element to the 2nd element
                // only the last element has no distance, because it has no "next hop"
                if (i < response.getRouteData().size() - 1) {
                    RouteData nextHop = response.getRouteData().get(i + 1);
                    RfnDevice nextHopDevice = devices.get(nextHop.getRfnIdentifier());
                    if (nextHopDevice == null) {
                        log.error("Device {} was not found", nextHop.getRfnIdentifier());
                        break;
                    }
                    PaoIdentifier nextHopPaoIdentifier = nextHopDevice.getPaoIdentifier();
                    nextHopLocation = locations.get(nextHopPaoIdentifier);
                    if (nextHopLocation == null) {
                        hopWithoutLocation = nextHopDevice;
                    }
                    addDistance(routeInfo, paoLocation, nextHopLocation);
                }
                addMappingInfo(metaData, routeInfo);
                routes.add(routeInfo);
                log.debug(routeInfo);
                if (nextHopLocation == null) {
                    break;
                }
            } else {
                log.error("Location is not found for " + routeDevice);
                // one of the devices has no location, can't display a route
                throw new NmNetworkException(noRoute, "noRoute");
            }
        }
        if (routes.isEmpty()) {
            // one of the devices has no location, can't display a route
            throw new NmNetworkException(noRoute, "noRoute");
        }
        return new Route(routes, hopWithoutLocation, errorMsg);
    }

    @Override
    public Neighbors getNeighbors(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        // the main entity (in which all neighbors are related to) shows no distance
        PaoLocation deviceLocation = paoLocationDao.getLocation(deviceId);
        RfnNeighborDataRequest request = new RfnNeighborDataRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());
        String errorMsg = null;

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

        Map<RfnIdentifier, RfnDevice> devices = response.getNeighborData().stream()
                .map(data -> rfnDeviceCreationService.createIfNotFound(data.getRfnIdentifier()))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(data -> data.getRfnIdentifier(), data -> data));

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = new HashMap<>();
        try {
            metaData = getMetaData(devices.values());
        } catch (NmNetworkException e) {
            errorMsg = accessor.getMessage(metadataErrorKey);
        }

        Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());
        List<RfnDevice> neighborsWithoutLocation = new ArrayList<>();
        List<Neighbor> neighbors = new ArrayList<>();

        for (com.cannontech.common.rfn.message.network.NeighborData data : response.getNeighborData()) {
            RfnDevice neighborDevice = devices.get(data.getRfnIdentifier());
            if (neighborDevice == null) {
                log.error("Device {} wan not found", data.getRfnIdentifier());
                continue;
            }
            PaoLocation neighborLocation = locations.get(neighborDevice.getPaoIdentifier());
            if (neighborLocation != null) {
                FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(neighborLocation));
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
        return new Neighbors(neighbors, neighborsWithoutLocation, errorMsg);
    }

    /**
     * Sends meta data request to NM for devices other then gateways
     */
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetaData(Collection<RfnDevice> devices)
            throws NmNetworkException {
        Set<RfnIdentifier> devicesOtherThenGatways = devices.stream()
                .filter(rfnDevice -> !rfnDevice.getPaoIdentifier().getPaoType().isRfGateway())
                .map(RfnDevice::getRfnIdentifier)
                .collect(Collectors.toSet());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = null;
        try {
            metaData = metadataMultiService.getMetadataForDeviceRfnIdentifiers(devicesOtherThenGatways,
                    Set.of(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM, RfnMetadataMulti.NODE_DATA));
        } catch (NmCommunicationException e) {
            throw new NmNetworkException(commsError, e, "commsError");
        }
        return metaData;
    }

    private void addMappingInfo(Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MappingInfo info) {
        PaoIdentifier pao = info.getDevice().getPaoIdentifier();
        if (pao.getPaoType().isRfGateway()) {
            addGatewayInfo(info);
        } else {
            addMetadata(info, metaData);
        }
        if (pao.getPaoType().isMeter()) {
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
        if (!metaData.isEmpty()) {
            RfnMetadataMultiQueryResult metadata = metaData.get(info.getDevice().getRfnIdentifier());
            if (metadata.getResultType() != RfnMetadataMultiQueryResultType.OK) {
                log.error("NM returned query result:" + metadata.getResultType() + " message:" + metadata.getResultMessage()
                        + " for device:" + info.getDevice());
                info.setErrorMsg(info.getAccessor().getMessage(metadataErrorKey));
                return;
            }
            setNodeCommStatus(info, metadata);
            setPrimaryForwardGatewayInfo(info);
            setMacAddress(info, metadata);
        }
    }

    private void setPrimaryForwardGatewayInfo(MappingInfo info) {
        //gateway
        DynamicRfnDeviceData deviceData =  rfnDeviceDao.findDynamicRfnDeviceData(info.getDevice().getPaoIdentifier().getPaoId());
        if (deviceData != null) {
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(deviceData.getGateway().getPaoIdentifier().getPaoId());
            info.setPrimaryGateway(gateway.getNameWithIPAddress());
            info.setPrimaryGatewayUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(gateway));
        }
    }

    private void setMacAddress(MappingInfo info, RfnMetadataMultiQueryResult metadata) {
        if (metadata.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
            NodeData nodeData = (NodeData) metadata.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
            info.setMacAddress(nodeData.getMacAddress());
        } else {
            log.error("NM didn't return NODE_DATA for {}", info.getDevice());
        }
    }

    /**
     * To get the NodeCommStatus (Ready/Not-Ready) we compare PRIMARY_GATEWAY_NODE_COMM and with PRIMARY_FORWARD_GATEWAY.
     * If they match, we can use the NodeCommStatus from PRIMARY_GATEWAY_NODE_COMM. If they don't match we will have to use
     * Unknown.
     */
    private void setNodeCommStatus(MappingInfo info, RfnMetadataMultiQueryResult metadata) {
        NodeComm status = getNodeCommStatusFromMultiQueryResult(info.getDevice(), metadata);
        if (status != null) {
            info.setStatus(status.getNodeCommStatus());
        }
    }

    @Override
    public NodeComm getNodeCommStatusFromMultiQueryResult(RfnDevice rfnDevice, RfnMetadataMultiQueryResult metadata) {
        if (metadata.isValidResultForMulti(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM)) {
            DynamicRfnDeviceData deviceData =  rfnDeviceDao.findDynamicRfnDeviceData(rfnDevice.getPaoIdentifier().getPaoId());
            RfnIdentifier primaryForwardGateway =  deviceData != null ?  deviceData.getGateway().getRfnIdentifier() : null;
            NodeComm comm = (NodeComm) metadata.getMetadatas().get(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM);
            RfnIdentifier reverseGateway = comm.getGatewayRfnIdentifier();
            if (reverseGateway != null && primaryForwardGateway != null && reverseGateway.equals(primaryForwardGateway)) {
                return comm;
            } else {
                log.debug("reverse gateway {} primary forward gateway {} for {}", reverseGateway, primaryForwardGateway, rfnDevice);
                log.info(
                        "Comm reverse gateway from DynamicRfnDeviceData {} doesn't match primary forward gateway {} for {}, unable to determine comm status",
                        reverseGateway, primaryForwardGateway, rfnDevice);
            }
        } else {
            log.error(
                    "NM didn't return REVERSE_LOOKUP_NODE_COMM for {}, unable to determine comm status",
                    rfnDevice);
        }
        return null;
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
     * "Distance to Next Hop" is calculated from the location (lat/lon) of the current node and the location of the next hop
     * node/gateway
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

    private class Distance {
        double distanceInKm;
        double distanceInMiles;

        public Distance(double distanceInKm, double distanceInMiles) {
            this.distanceInKm = distanceInKm;
            this.distanceInMiles = distanceInMiles;
        }
    }

    @Override
    public NetworkMap getNetworkMap(NetworkMapFilter filter, MessageSourceAccessor accessor)
            throws NmNetworkException, NmCommunicationException {
        // "link quality1 OR link quality2" AND "gateway1 OR gateway2 OR gateway3" AND "hopcount1 OR hopcount2"
        Set<RfnGateway> gateways = rfnGatewayService.getGatewaysByPaoIds(filter.getSelectedGatewayIds());
        String gatewayNames = gateways.stream()
                .map(gateway -> gateway.getName()).collect(Collectors.joining(" ,"));
        Map<Integer, RfnIdentifier> gatewayIdsToIdentifiers = gateways.stream()
                .filter(g -> filter.getSelectedGatewayIds().contains(g.getId()))
                .collect(Collectors.toMap(g -> g.getId(), g -> g.getRfnIdentifier()));
        NetworkMap map = new NetworkMap();
        Set<RfnIdentifier> filteredDevices = new HashSet<>();
        Set<RfnIdentifier> gatewaysToAddToMap = new HashSet<>(gatewayIdsToIdentifiers.values());
        Set<RfnMetadataMulti> multi = getMulti(filter);
        if (!multi.isEmpty()) {
            Set<RfnIdentifier> devices = rfnDeviceDao.getDeviceRfnIdentifiersByGatewayIds(filter.getSelectedGatewayIds());
            log.debug("Getting network map by filter: {} gateways {} devices {}", filter, gatewayNames, devices.size());
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = metadataMultiService
                    .getMetadataForDeviceRfnIdentifiers(devices, multi);

            filteredDevices.addAll(metaData.keySet());
            log.debug("All devices {}", filteredDevices.size());
            filterByDataRecievedFromNM(filter, metaData, filteredDevices);
            log.debug("After filtered by data recieved from NM devices {}", filteredDevices.size());
            filterByDataInDynamicRfnDeviceData(filter, filteredDevices);
            log.debug("After filtered by data in {} DynamicRfnDeviceData", filteredDevices.size());
            metaData.entrySet().removeIf(data -> !filteredDevices.contains(data.getKey()));
            if (filter.getColorCodeBy() == ColorCodeBy.DESCENDANT_COUNT) {
                List<DynamicRfnDeviceData> data = rfnDeviceDao
                        .getDynamicRfnDeviceData(rfnDeviceDao.getDeviceIdsForRfnIdentifiers(filteredDevices));
                log.debug("Loading map filtered by decendantCount {} devices to display {}", gatewayNames, data.size());
                colorCodeByDescendantCountAndAddToMap(map, data, accessor);
            } else if (filter.getColorCodeBy() == ColorCodeBy.HOP_COUNT) {
                colorCodeByHopCountAndAddToMap(map, metaData, accessor, filter);
            } else if (filter.getColorCodeBy() == ColorCodeBy.LINK_QUALITY) {
                colorCodeByLinkQualityAndAddToMap(map, metaData, accessor, filter);
            } else if (filter.getColorCodeBy() == ColorCodeBy.GATEWAY) {
                Set<Integer> paoIds = rfnDeviceDao.getDeviceIdsForRfnIdentifiers(filteredDevices);
                Map<Integer, List<DynamicRfnDeviceData>> data = rfnDeviceDao.getDynamicRfnDeviceDataByDevices(paoIds);
                gatewaysToAddToMap.removeAll(data.keySet().stream().map(id -> gatewayIdsToIdentifiers.get(id))
                        .collect(Collectors.toList()));
                log.debug("Loading map filtered by gateway {} devices to display {}", gatewayNames, paoIds.size());
                colorCodeByGatewayAndAddToMap(map, data);
            }
        } else {
            Map<Integer, List<DynamicRfnDeviceData>> data = rfnDeviceDao
                    .getDynamicRfnDeviceDataByGateways(filter.getSelectedGatewayIds());
            if (!filter.getDescendantCount().containsAll(Arrays.asList(DescendantCount.values()))) {
                data.values().forEach(datas -> datas.removeIf(value -> !filter.getDescendantCount()
                        .contains(DescendantCount.getDescendantCount(value.getDescendantCount()))));
            }
            if (filter.getColorCodeBy() == ColorCodeBy.GATEWAY) {
                gatewaysToAddToMap.removeAll(data.keySet().stream().map(id -> gatewayIdsToIdentifiers.get(id))
                        .collect(Collectors.toList()));
                log.debug("Loading map filtered by gateway {} ", gatewayNames);
                colorCodeByGatewayAndAddToMap(map, data);
            } else if (filter.getColorCodeBy() == ColorCodeBy.DESCENDANT_COUNT) {
                log.debug("Loading map filtered by decendant count {} for gateways {} ", gatewayNames);
                List<DynamicRfnDeviceData> list = new ArrayList<>();
                data.values().forEach(value -> list.addAll(value));
                colorCodeByDescendantCountAndAddToMap(map, list, accessor);
            }
        }
        addDevicesToMap(map, null, null, gatewaysToAddToMap);
        log.debug("Map {} ", map);
        return map;
    }

    private void filterByDataInDynamicRfnDeviceData(NetworkMapFilter filter, Set<RfnIdentifier> filteredDevices) {
        if (!filter.getDescendantCount().containsAll(Arrays.asList(DescendantCount.values()))) {
            Set<Integer> ids = rfnDeviceDao.getDeviceIdsForRfnIdentifiers(filteredDevices);
            List<DynamicRfnDeviceData> data = rfnDeviceDao.getDynamicRfnDeviceData(ids);
            Map<RfnIdentifier, DynamicRfnDeviceData> map = data.stream()
                    .collect(Collectors.toMap(d -> d.getDevice().getRfnIdentifier(), d -> d));
            filteredDevices.removeIf(filteredDevice -> {
                DynamicRfnDeviceData deviceData = map.get(filteredDevice);
                if (deviceData == null) {
                    log.debug("No entry in DynamicRfnDeviceData for {}", filteredDevice);
                    return false;
                }
                try {
                    return !filter.getDescendantCount()
                            .contains(DescendantCount.getDescendantCount(deviceData.getDescendantCount()));
                } catch (Exception e) {
                    String text = "Filter:" + filter.getDescendantCount() + " deviceData.getDescendantCount():"
                            + deviceData.getDescendantCount();
                    log.error(text, e);
                    return false;
                }
            });
        }
    }

    /**
     * Returns set of multis to send to NM
     */
    private Set<RfnMetadataMulti> getMulti(NetworkMapFilter filter) {
        Set<RfnMetadataMulti> multi = new HashSet<>();
        if (!filter.getLinkQuality().containsAll(Arrays.asList(LinkQuality.values()))) {
            multi.add(PRIMARY_FORWARD_NEIGHBOR_DATA);
        }
        if (!filter.getHopCount().containsAll(Arrays.asList(HopCount.values()))) {
            multi.add(PRIMARY_FORWARD_ROUTE_DATA);
        }
        if (filter.getColorCodeBy() == ColorCodeBy.HOP_COUNT) {
            multi.add(PRIMARY_FORWARD_ROUTE_DATA);
        } else if (filter.getColorCodeBy() == ColorCodeBy.LINK_QUALITY) {
            multi.add(PRIMARY_FORWARD_NEIGHBOR_DATA);
        }
        return multi;
    }

    /**
     * Adding devices received from NM to map, hop count information displayed in a legend
     */
    private void colorCodeByHopCountAndAddToMap(NetworkMap map,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MessageSourceAccessor accessor, NetworkMapFilter filter) {
        log.debug("Loading map filtered by hop count, total devices in result {}", metaData.size());
        if (metaData.isEmpty()) {
            return;
        }
        RfnMetadataMulti multi = PRIMARY_FORWARD_ROUTE_DATA;
        Set<RfnIdentifier> unknownDevices = new HashSet<>();
        HashMultimap<HopCountColors, RfnIdentifier> identifiers = HashMultimap.create();

        for (Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> result : metaData.entrySet()) {
            if (result.getValue().isValidResultForMulti(multi)) {
                com.cannontech.common.rfn.message.route.RouteData routeData = (com.cannontech.common.rfn.message.route.RouteData) result
                        .getValue().getMetadatas().get(multi);
                int hopCount = (int) routeData.getHopCount();
                identifiers.put(HopCountColors.getHopCountColor(hopCount), result.getKey());
            } else {
                unknownDevices.add(result.getKey());
            }
        }
        log.debug("Filtered identifiers {}", identifiers.size());
        HopCountColors maxCountColors = HopCountColors.getHopCountColorsWithMaxNumber();
        List<HopCountColors> colors = identifiers.keySet().stream().sorted(Comparator.comparingInt(HopCountColors::getNumber))
                .collect(Collectors.toList());
        map.keepTheLegendOrder();
        for (HopCountColors color : colors) {
            String legend = color == maxCountColors ? "> " + (maxCountColors.getNumber() - 1) : String.valueOf(color.getNumber());
            addDevicesToMap(map, color.getColor(), legend, identifiers.get(color));
        }
        addUnknownDevicesToMap(map, accessor, unknownDevices);
    }

    /**
     * Adding devices to map color coded by descendant count
     */
    private void colorCodeByDescendantCountAndAddToMap(NetworkMap map, List<DynamicRfnDeviceData> data,
            MessageSourceAccessor accessor) {
        HashMultimap<DescendantCount, RfnIdentifier> counts = HashMultimap.create();
        for (DynamicRfnDeviceData deviceData : data) {
            DescendantCount dc = DescendantCount.getDescendantCount(deviceData.getDescendantCount());
            counts.put(dc, deviceData.getDevice().getRfnIdentifier());
        }
        for (DescendantCount descendantCount : counts.keySet()) {
            String legendText = accessor.getMessage(descendantCount.getFormatKey());
            addDevicesToMap(map, descendantCount.getColor(), legendText, counts.get(descendantCount));
        }
    }

    /**
     * If there was no data to determine the color is returned for device and the user chose to color code by that option, it will
     * be added to a map the device will be marked as "Unknown".
     */
    private void addUnknownDevicesToMap(NetworkMap map, MessageSourceAccessor accessor, Set<RfnIdentifier> unknownDevices) {
        if (!unknownDevices.isEmpty()) {
            String legendText = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.unknown");
            addDevicesToMap(map, Color.GREY, legendText, unknownDevices);
        }
    }

    /**
     * Adding devices received from NM to map, gateway information displayed in a legend
     */
    private void colorCodeByLinkQualityAndAddToMap(NetworkMap map,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MessageSourceAccessor accessor, NetworkMapFilter filter) {
        log.debug("Loading map filtered by link quality, total devices in result {}", metaData.size());
        if (metaData.isEmpty()) {
            return;
        }
        RfnMetadataMulti multi = PRIMARY_FORWARD_NEIGHBOR_DATA;
        Set<RfnIdentifier> unknownDevices = new HashSet<>();
        HashMultimap<LinkQuality, RfnIdentifier> identifiers = HashMultimap.create();
        metaData.entrySet()
                .forEach(result -> {
                    if (result.getValue().isValidResultForMulti(multi)) {
                        NeighborData neighborData = (NeighborData) result.getValue().getMetadatas().get(multi);
                        LinkQuality lq = LinkQuality.getLinkQuality(neighborData);
                        identifiers.put(lq, result.getKey());
                    } else {
                        unknownDevices.add(result.getKey());
                    }
                });

        for (LinkQuality linkQuality : identifiers.keySet()) {
            String legendText = accessor.getMessage(linkQuality.getFormatKey());
            addDevicesToMap(map, linkQuality.getColor(), legendText, identifiers.get(linkQuality));
        }
        addUnknownDevicesToMap(map, accessor, unknownDevices);
    }

    /**
     * Adding devices and gateways to map
     */
    private void colorCodeByGatewayAndAddToMap(NetworkMap map,  Map<Integer, List<DynamicRfnDeviceData>> data) {
        AtomicInteger i = new AtomicInteger(0);
        for (Integer gatewayId : data.keySet()) {
            Color color = Color.values()[i.getAndIncrement()];
            RfnDevice gateway = data.get(gatewayId).iterator().next().getGateway();
            Set<RfnIdentifier> devices = data.get(gatewayId).stream()
                    .map(d -> d.getDevice().getRfnIdentifier())
                    .collect(Collectors.toSet());
            log.debug("Color code by gateway {} devices {}", gateway.getName(), devices.size());
            devices.add(gateway.getRfnIdentifier());
            addDevicesToMap(map, color, gateway.getName(), devices);
        }
    }

    /**
     * filteredDevices - removes devices that do not match user selected criteria (filter)
     */
    private void filterByDataRecievedFromNM(NetworkMapFilter filter,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, Set<RfnIdentifier> filteredDevices) {
        for (Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data : metaData.entrySet()) {
            filterByLinkQuality(filter, filteredDevices, data);
            filterByHopCount(filter, filteredDevices, data);
        }
    }

    /**
     * Removes devices that do not match user selected criteria (filter) from filteredDevices
     */
    private void filterByHopCount(NetworkMapFilter filter, Set<RfnIdentifier> filteredDevices,
            Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data) {
        if (!filter.getHopCount().containsAll(Arrays.asList(HopCount.values()))) {
            if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_ROUTE_DATA)) {
                com.cannontech.common.rfn.message.route.RouteData routeData = (com.cannontech.common.rfn.message.route.RouteData) data
                        .getValue().getMetadatas().get(PRIMARY_FORWARD_ROUTE_DATA);
                if (!filter.getHopCount().contains(HopCount.getHopCount(routeData.getHopCount()))) {
                    filteredDevices.remove(data.getKey());
                }
            } else {
                filteredDevices.remove(data.getKey());
            }
        }
    }

    /**
     * Removes devices that do not match user selected criteria (filter) from filteredDevices
     */
    private void filterByLinkQuality(NetworkMapFilter filter, Set<RfnIdentifier> filteredDevices,
            Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data) {
        if (!filter.getLinkQuality().containsAll(Arrays.asList(LinkQuality.values()))) {
            if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_NEIGHBOR_DATA)) {
                NeighborData neighborData = (NeighborData) data.getValue().getMetadatas().get(PRIMARY_FORWARD_NEIGHBOR_DATA);
                if (!filter.getLinkQuality().contains(LinkQuality.getLinkQuality(neighborData))) {
                    filteredDevices.remove(data.getKey());
                }
            } else {
                filteredDevices.remove(data.getKey());
            }
        }
    }

    /**
     * Adds device location and legend to a map
     */
    private void addDevicesToMap(NetworkMap map, Color color, String legend, Set<RfnIdentifier> devices) {
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        Set<Integer> paoIds = rfnDeviceDao.getDeviceIdsForRfnIdentifiers(devices);
        if (CollectionUtils.isEmpty(paoIds)) {
            return;
        }

        Map<Integer, PaoLocation> locations = Maps.uniqueIndex(paoLocationDao.getLocations(paoIds),
                l -> l.getPaoIdentifier().getPaoId());
        map.getDevicesWithoutLocation().addAll(paoIds.stream().filter(paoId -> !locations.containsKey(paoId))
                .map(paoId -> new SimpleDevice(dbCache.getAllPaosMap().get(paoId).getPaoIdentifier()))
                .collect(Collectors.toList()));

        if (locations.isEmpty()) {
            log.debug("Failed to add devices {} to map, locations empty", paoIds.size());
            return;
        }
        String hexColor = "#ffffff";
        if (color != null) {
            hexColor = color.getHexColor();
        }
        FeatureCollection features = paoLocationService.getFeatureCollection(locations.values());
        log.debug(
                "Color {} attempting to add devices {} to map locations found {} features created {}. Not added devices {} - no location.",
                hexColor, paoIds.size(), locations.size(), features.getFeatures().size(), map.getDevicesWithoutLocation().size());
        if (map.getMappedDevices().containsKey(hexColor)) {
            // filtering by hop count contain duplicate colors, see legend for a visual example
            map.getMappedDevices().get(hexColor).getFeatures().addAll(features.getFeatures());
        } else {
            map.getMappedDevices().put(hexColor, features);
        }
        if (legend != null && color != null) {
            map.addLegend(new Legend(color, legend));
        }
        return;
    }
}