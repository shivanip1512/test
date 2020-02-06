package com.cannontech.web.tools.mapping.service.impl;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA;

import java.util.ArrayList;
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
import com.cannontech.common.rfn.message.node.NodeCommStatus;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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
                if (!metaData.isEmpty()) {
                    RfnMetadataMultiQueryResult deviceMetadata = metaData.get(data.getRfnIdentifier());
                    if (deviceMetadata != null) {
                        if (deviceMetadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT)) {
                            Integer descendantCount = (Integer) deviceMetadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT);
                            routeInfo.setDescendantCount(descendantCount);
                        }
                    }
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
                    Set.of(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM, RfnMetadataMulti.NODE_DATA,
                            RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY, RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT));
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
            setPrimaryForwardGatewayInfo(info, metadata);
            setMacAddress(info, metadata);
        }
    }

    private void setPrimaryForwardGatewayInfo(MappingInfo info, RfnMetadataMultiQueryResult metadata) {
        RfnDevice device = getPrimaryForwardGatewayFromMultiQueryResult(info.getDevice(), metadata);
        if (device != null) {
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(device.getPaoIdentifier().getPaoId());
            info.setPrimaryGateway(gateway.getNameWithIPAddress());
            info.setPrimaryGatewayUrl(paoDetailUrlHelper.getUrlForPaoDetailPage(gateway));
        }
    }

    @Override
    public RfnDevice getPrimaryForwardGatewayFromMultiQueryResult(RfnDevice rfnDevice, RfnMetadataMultiQueryResult metadata) {
        if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY)) {
            RfnIdentifier gatewayIdentifier = (RfnIdentifier) metadata.getMetadatas()
                    .get(RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY);
            RfnDevice gateway = rfnDeviceCreationService.createIfNotFound(gatewayIdentifier);
            return gateway;
        } else {
            log.error("NM didn't return PRIMARY_FORWARD_GATEWAY for {} ", rfnDevice);
        }
        return null;
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
        NodeCommStatus status = getNodeCommStatusFromMultiQueryResult(info.getDevice(), metadata);
        info.setStatus(status);
    }

    @Override
    public NodeCommStatus getNodeCommStatusFromMultiQueryResult(RfnDevice rfnDevice, RfnMetadataMultiQueryResult metadata) {
        if (metadata.isValidResultForMulti(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM)
                && metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY)) {
            NodeComm comm = (NodeComm) metadata.getMetadatas().get(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM);
            RfnIdentifier primaryForwardGateway = (RfnIdentifier) metadata.getMetadatas()
                    .get(RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY);
            if (comm.getGatewayRfnIdentifier() == primaryForwardGateway) {
                return comm.getNodeCommStatus();
            } else {
                log.info("NM didn't return comm gateway {}, primary forward gateway {} for {}, unable to determaine comm status",
                        comm.getGatewayRfnIdentifier(), primaryForwardGateway, rfnDevice);
            }
        } else {
            log.error(
                    "NM didn't return PRIMARY_GATEWAY_NODE_COMM or PRIMARY_FORWARD_GATEWAY for {}, unable to determaine comm status",
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
        log.debug("Getting network map by filter: {}", filter);

        NetworkMap map = new NetworkMap();

        Set<RfnMetadataMulti> multi = Set.of(PRIMARY_FORWARD_GATEWAY, PRIMARY_FORWARD_NEIGHBOR_DATA,
                PRIMARY_FORWARD_DESCENDANT_COUNT, PRIMARY_FORWARD_ROUTE_DATA);

        Map<RfnIdentifier, RfnGateway> gateways = rfnGatewayService.getGatewaysByPaoIds(filter.getSelectedGatewayIds())
                .stream().collect(Collectors.toMap(gateway -> gateway.getRfnIdentifier(), gateway -> gateway));

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = metadataMultiService
                .getMetadataForGatewayRfnIdentifiers(new HashSet<>(gateways.keySet()), multi);

        Set<RfnIdentifier> gatewaysWithoutDevices = new HashSet<>(gateways.keySet());
        Set<RfnIdentifier> filteredDevices = new HashSet<>(metaData.keySet());
        removeDevicesThatDoNotMatchSelectedCriteria(filter, metaData, gatewaysWithoutDevices, filteredDevices);
        metaData.entrySet().removeIf(data -> !filteredDevices.contains(data.getKey()));

        addDevicesToMap(map, "#ffffff", gatewaysWithoutDevices);

        if (filter.getColorCodeBy() == ColorCodeBy.DESCENDANT_COUNT) {
            colorCodeByDescendantCountAndAddToMap(map, metaData, accessor, filter);
        } else if (filter.getColorCodeBy() == ColorCodeBy.GATEWAY) {
            colorCodeByGatewayAndAddToMap(map, metaData);
        } else if (filter.getColorCodeBy() == ColorCodeBy.HOP_COUNT) {
            colorCodeByHopCountAndAddToMap(map, metaData, accessor, filter);
        } else if (filter.getColorCodeBy() == ColorCodeBy.LINK_QUALITY) {
            colorCodeByLinkQualityAndAddToMap(map, metaData, accessor, filter);
        }
        log.debug("Map {} ", map);
        return map;
    }

    /**
     * Adding devices received from NM to map, hop count information displayed in a legend
     */
    private void colorCodeByHopCountAndAddToMap(NetworkMap map,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MessageSourceAccessor accessor, NetworkMapFilter filter) {
        log.debug("Loading map filtered by hop count");
        if (metaData.isEmpty()) {
            return;
        }
        HashMultimap<HopCountColors, RfnIdentifier> identifiers = HashMultimap.create();

        for (Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> result : metaData.entrySet()) {
            com.cannontech.common.rfn.message.route.RouteData routeData = (com.cannontech.common.rfn.message.route.RouteData) result
                    .getValue().getMetadatas().get(PRIMARY_FORWARD_ROUTE_DATA);
            int hopCount = (int) routeData.getHopCount();
            identifiers.put(HopCountColors.getHopCountColor(hopCount), result.getKey());
        }
        
        HopCountColors maxCountColors = HopCountColors.getHopCountColorsWithMaxNumber();
        List<HopCountColors> colors = identifiers.keySet().stream().sorted(Comparator.comparingInt(HopCountColors::getNumber))
            .collect(Collectors.toList());
        map.keepTheLegendOrder();
        for (HopCountColors color : colors) {   
            String legend = color == maxCountColors? "> "+ (maxCountColors.getNumber() - 1) : String.valueOf(color.getNumber());
            addDevicesAndLegendToMap(map, color.getColor(), legend, identifiers.get(color));
        }
    }

    /**
     * Adding devices received from NM to map, gateway information displayed in a legend
     */
    private void colorCodeByDescendantCountAndAddToMap(NetworkMap map,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MessageSourceAccessor accessor, NetworkMapFilter filter) {
        log.debug("Loading map filtered by descendant count");
        if (metaData.isEmpty()) {
            return;
        }
        HashMultimap<DescendantCount, RfnIdentifier> identifiers = HashMultimap.create();
        metaData.entrySet().stream()
                .filter(result -> result.getValue().isValidResultForMulti(PRIMARY_FORWARD_DESCENDANT_COUNT))
                .forEach(result -> {
                    Integer count = (Integer) result.getValue().getMetadatas()
                            .get(PRIMARY_FORWARD_DESCENDANT_COUNT);
                    DescendantCount dc = DescendantCount.getDescendantCount(count);
                    identifiers.put(dc, result.getKey());
                });

        for (DescendantCount descendantCount : identifiers.keySet()) {
            String legendText = accessor.getMessage(descendantCount.getFormatKey());
            addDevicesAndLegendToMap(map, descendantCount.getColor(), legendText, identifiers.get(descendantCount));
        }
    }

    /**
     * Adding devices received from NM to map, gateway information displayed in a legend
     */
    private void colorCodeByLinkQualityAndAddToMap(NetworkMap map,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData, MessageSourceAccessor accessor, NetworkMapFilter filter) {
        log.debug("Loading map filtered by link quality");
        if (metaData.isEmpty()) {
            return;
        }
        HashMultimap<LinkQuality, RfnIdentifier> identifiers = HashMultimap.create();
        metaData.entrySet().stream()
                .filter(result -> result.getValue().isValidResultForMulti(PRIMARY_FORWARD_NEIGHBOR_DATA))
                .forEach(result -> {
                    NeighborData neighborData = (NeighborData) result.getValue().getMetadatas()
                            .get(PRIMARY_FORWARD_NEIGHBOR_DATA);
                    LinkQuality lq = LinkQuality.getLinkQuality(neighborData);
                    identifiers.put(lq, result.getKey());
                });

        for (LinkQuality linkQuality : identifiers.keySet()) {
            String legendText = accessor.getMessage(linkQuality.getFormatKey());
            addDevicesAndLegendToMap(map, linkQuality.getColor(), legendText, identifiers.get(linkQuality));
        }
    }

    /**
     * Adding devices received from NM to map, gateway information displayed in a legend
     */
    private void colorCodeByGatewayAndAddToMap(NetworkMap map,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData) {

        if (metaData.isEmpty()) {
            return;
        }
        log.debug("Loading map filtered by gateway");
        Map<RfnIdentifier, Collection<RfnIdentifier>> gatewayToDeviceMap = getGatewayToDeviceMap(metaData);
        AtomicInteger i = new AtomicInteger(0);
        for (RfnIdentifier gatewayIdentifier : gatewayToDeviceMap.keySet()) {
            Color color = Color.values()[i.getAndIncrement()];
            RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(gatewayIdentifier);
            Set<RfnIdentifier> devices = Sets.newHashSet(gatewayToDeviceMap.get(gatewayIdentifier));
            log.debug("Gateway {} devices {}", gateway.getName(), devices.size());
            devices.add(gatewayIdentifier);
            addDevicesAndLegendToMap(map, color, gateway.getName(), devices);
        }
    }

    private Map<RfnIdentifier, Collection<RfnIdentifier>> getGatewayToDeviceMap(
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData) {
        Multimap<RfnIdentifier, RfnIdentifier> gatewaysToDevices = ArrayListMultimap.create();
        metaData.forEach((deviceRfnIdentifier, queryResult) -> {
            if (queryResult.isValidResultForMulti(PRIMARY_FORWARD_GATEWAY)) {
                RfnIdentifier gatewayRfnIdentifier = (RfnIdentifier) queryResult.getMetadatas().get(PRIMARY_FORWARD_GATEWAY);
                gatewaysToDevices.put(gatewayRfnIdentifier, deviceRfnIdentifier);
            }
        });
        return gatewaysToDevices.asMap();
    }

    /**
     * selectedGateways - removes gateway that has devices
     * filteredDevices - removes devices that do not match user selected criteria (filter)
     */
    private void removeDevicesThatDoNotMatchSelectedCriteria(NetworkMapFilter filter,
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData,
            Set<RfnIdentifier> selectedGateways, Set<RfnIdentifier> filteredDevices) {

        for (Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data : metaData.entrySet()) {
            filterGatewaysWithoutDevices(selectedGateways, data);
            filterByDescendantCount(filter, filteredDevices, data);
            filterByLinkQuality(filter, filteredDevices, data);
            filterByHopCount(filter, filteredDevices, data);
        }
    }

    /**
     * Removes devices that do not match user selected criteria (filter) from filteredDevices
     */
    private void filterByHopCount(NetworkMapFilter filter, Set<RfnIdentifier> filteredDevices,
            Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data) {
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

    /**
     * Removes devices that do not match user selected criteria (filter) from filteredDevices
     */
    private void filterByLinkQuality(NetworkMapFilter filter, Set<RfnIdentifier> filteredDevices,
            Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data) {
        if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_NEIGHBOR_DATA)) {
            NeighborData neighborData = (NeighborData) data.getValue().getMetadatas().get(PRIMARY_FORWARD_NEIGHBOR_DATA);
            if (!filter.getLinkQuality().contains(LinkQuality.getLinkQuality(neighborData))) {
                filteredDevices.remove(data.getKey());
            }
        } else {
            filteredDevices.remove(data.getKey());
        }
    }

    /**
     * Removes devices that do not match user selected criteria (filter) from filteredDevices
     */
    private void filterByDescendantCount(NetworkMapFilter filter, Set<RfnIdentifier> filteredDevices,
            Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data) {
        if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_DESCENDANT_COUNT)) {
            Integer count = (Integer) data.getValue().getMetadatas().get(PRIMARY_FORWARD_DESCENDANT_COUNT);
            if (!filter.getDescendantCount().contains(DescendantCount.getDescendantCount(count))) {
                filteredDevices.remove(data.getKey());
            }
        } else {
            filteredDevices.remove(data.getKey());
        }
    }

    /**
     * Removes gateways that has devices
     */
    private void filterGatewaysWithoutDevices(Set<RfnIdentifier> selectedGateways,
            Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data) {
        if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_GATEWAY)) {
            RfnIdentifier gateway = (RfnIdentifier) data.getValue().getMetadatas().get(PRIMARY_FORWARD_GATEWAY);
            selectedGateways.remove(gateway);
        }
    }

    /**
     * Add legend and device location to a map
     */
    private void addDevicesAndLegendToMap(NetworkMap map, Color color, String legend, Set<RfnIdentifier> devices) {
        map.addLegend(new Legend(color, legend));
        addDevicesToMap(map, color.getHexColor(), devices);
    }

    /**
     * Add device location to a map
     */
    private void addDevicesToMap(NetworkMap map, String hexColor, Set<RfnIdentifier> devices) {
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        Set<Integer> paoIds = rfnDeviceDao.getDeviceIdsForRfnIdentifiers(devices);
        if (CollectionUtils.isEmpty(paoIds)) {
            return;
        }

        Map<Integer, PaoLocation> locations = Maps.uniqueIndex(paoLocationDao.getLocations(paoIds),
                l -> l.getPaoIdentifier().getPaoId());
        map.setDevicesWithoutLocation(paoIds.stream().filter(paoId -> !locations.containsKey(paoId))
                .map(paoId -> new SimpleDevice(dbCache.getAllPaosMap().get(paoId).getPaoIdentifier()))
                .collect(Collectors.toList()));

        if (locations.isEmpty()) {
            log.debug("Failed to add devices {} to map, locations empty", devices.size());
            return;
        }
        log.debug("Attempting to add devices {} to map locations found {}. Only devices, with locations will be added.",
               devices.size(), locations.size());
        FeatureCollection features = paoLocationService.getFeatureCollection(locations.values());
        map.getMappedDevices().put(hexColor, features);
    }
}
