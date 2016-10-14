package com.cannontech.web.tools.mapping.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
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
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.core.dao.NotFoundException;
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
    private static final String nmError = "Recieved error from Network Manager.";
    private static final String noRoute = "One or more devices within the route could not be located.";
    private static final String noParent = "No location in Yukon was found for this parent device.";

    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.network.data.request";

    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private ConnectionFactory connectionFactory;
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

    @Override
    public Parent getParent(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        BlockingJmsReplyHandler<RfnParentReply> reply = new BlockingJmsReplyHandler<>(RfnParentReply.class);
        RfnParentRequest request = new RfnParentRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());

        log.debug("Sending get parent request to Network Manager: " + request);

        RfnParentReply response;
        try {
            parentReplyTemplate.send(request, reply);
            response = reply.waitForCompletion();
        } catch (ExecutionException e) {
            throw new NmNetworkException(commsError, e, "commsError");
        }
        if (response.getReplyType() != RfnParentReplyType.OK) {
            throw new NmNetworkException(nmError, response.getReplyType().name());
        }

        log.debug("response: " + response);

        ParentData data = response.getParentData();
        try {
            RfnDevice parentDevice = rfnDeviceDao.getDeviceForExactIdentifier(data.getRfnIdentifier());
            PaoLocation paoLocation = paoLocationDao.getLocation(parentDevice.getPaoIdentifier().getPaoId());
            if (paoLocation == null) {
                throw new NmNetworkException(noParent, "noParent");
            }
            FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
            Parent parent = new Parent(device, location, data);
            return parent;
        } catch (NotFoundException e) {
            // create new device if it doesn't exist
            rfnDeviceCreationService.create(data.getRfnIdentifier());
            log.info(data.getRfnIdentifier() + " is not found. Creating device.");
            throw new NmNetworkException(noParent, "noParent");
        }
    }
    
    @Override
    public List<RouteInfo> getRoute(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {

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
        } catch (ExecutionException e) {
            throw new NmNetworkException(commsError, e, "commsError");
        }

        log.debug("response: " + response);

        if (response.getReplyType() != RfnPrimaryRouteDataReplyType.OK) {
            throw new NmNetworkException(nmError, response.getReplyType().name());
        }

        Map<RfnIdentifier, RfnDevice> devices = new HashMap<>();
        boolean hasMissingLocationOrDevice = false;
        for (RouteData data : response.getRouteData()) {
            if(data.getRfnIdentifier() == null){
                hasMissingLocationOrDevice  = true;
                log.error(data + " has no RfnIdentifier");
                continue;
            }
            boolean isFound = findDevice(data.getRfnIdentifier(), devices);
            if(!isFound){
                hasMissingLocationOrDevice = true;  
            }
        }

        if (hasMissingLocationOrDevice) {
            // one of the devices doesn't exist in yukon or we got a response without valid rfnIdentifier
            // since we will not be able to find location and route can't be generated, exception is thrown
            throw new NmNetworkException(noRoute, "noRoute");
        }

        Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());

        List<RouteInfo> routes = new ArrayList<>();
        for (RouteData data : response.getRouteData()) {
            RfnDevice routeDevice = devices.get(data.getRfnIdentifier());
            PaoLocation paoLocation = locations.get(routeDevice.getPaoIdentifier());
            if (paoLocation != null) {
                FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
                routes.add(new RouteInfo(device, data, location, accessor));
            } else {
                log.error("Location is not found for " + routeDevice);
                // one of the devices has no location, can't display a route
                throw new NmNetworkException(noRoute, "noRoute");
            }
        }
        return routes;
    }

    @Override
    public List<Neighbor> getNeighbors(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        RfnNeighborDataRequest request = new RfnNeighborDataRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());

        log.debug("Sending get neighbors request to Network Manager: " + request);

        BlockingJmsReplyHandler<RfnNeighborDataReply> reply = new BlockingJmsReplyHandler<>(RfnNeighborDataReply.class);

        RfnNeighborDataReply response;
        try {
            neighborReplyTemplate.send(request, reply);
            response = reply.waitForCompletion();
        } catch (ExecutionException e) {
            throw new NmNetworkException(commsError, e, "commsError");
        }

        if (response.getReplyType() != RfnNeighborDataReplyType.OK) {
            throw new NmNetworkException(nmError, response.getReplyType().name());
        }
        log.debug("response: " + response);

        Map<RfnIdentifier, RfnDevice> devices = new HashMap<>();
        for (NeighborData data : response.getNeighborData()) {
            findDevice(data.getRfnIdentifier(), devices);
        }

        Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());

        List<Neighbor> neighbors = new ArrayList<>();
        for (NeighborData data : response.getNeighborData()) {
            RfnDevice neighborDevice = devices.get(data.getRfnIdentifier());
            
            if(neighborDevice == null){
                continue;
            }
            PaoLocation paoLocation = locations.get(neighborDevice.getPaoIdentifier());
            if (paoLocation != null) {
                FeatureCollection location = paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
                neighbors.add(new Neighbor(device, location, data, accessor));
            } else {
                log.error("Location is not found for " + neighborDevice);
            }
        }
        return neighbors;
    }
    
    /**
     * Attempts to lookup a device by identifier, return true if the device is found, creates a new device if it was not
     * found. Populates device map with the device information;
     */
    private boolean findDevice(RfnIdentifier identifier, Map<RfnIdentifier, RfnDevice> devices) {
        boolean isFound = false;
        if (identifier != null) {
            RfnDevice rfnDevice = null;
            try {
                rfnDevice = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
                devices.put(identifier, rfnDevice);
                isFound = true;
            } catch (NotFoundException e) {
                // create new device if it doesn't exist
                rfnDeviceCreationService.create(identifier);
                log.info(identifier + " is not found. Creating device.");
            }
        }
        return isFound;
    }
}
