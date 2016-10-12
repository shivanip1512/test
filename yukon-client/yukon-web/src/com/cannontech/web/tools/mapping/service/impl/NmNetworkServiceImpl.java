package com.cannontech.web.tools.mapping.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
        "Unable to send request due to a communication error between Yukon and Network Manager";
    private static final String nmError = "Recieved error from Network Manager";
    private static final String noLocation = "No location in Yukon was found for this device.";

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

        try {
            parentReplyTemplate.send(request, reply);
            RfnParentReply response = reply.waitForCompletion();
            log.debug("response: " + response);
            if (response.getReplyType() == RfnParentReplyType.OK) {
                ParentData data = response.getParentData();
                RfnDevice parentDevice = rfnDeviceDao.getDeviceForExactIdentifier(data.getRfnIdentifier());
                if (parentDevice == null) {
                    rfnDeviceCreationService.create(data.getRfnIdentifier());
                    log.info(data.getRfnIdentifier()+" is not found. Creating device.");
                    throw new NmNetworkException(noLocation, "noLocation");
                } else {
                    FeatureCollection location =
                        paoLocationService.getLocationsAsGeoJson(Lists.newArrayList(parentDevice));
                    if (location == null) {
                        throw new NmNetworkException(noLocation, "noLocation");
                    }
                    Parent parent = new Parent(device, location, data);
                    return parent;
                }
            } else {
                throw new NmNetworkException(nmError, response.getReplyType().name());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new NmNetworkException(commsError, e, "commsError");
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

        try {
            routeReplyTemplate.send(request, reply);
            RfnPrimaryRouteDataReply response = reply.waitForCompletion();
            log.debug("response: " + response);
            if (response.getReplyType() == RfnPrimaryRouteDataReplyType.OK) {
                Map<RfnIdentifier, RfnDevice> devices =
                    response.getRouteData().stream().collect(Collectors.toMap(x -> x.getRfnIdentifier(),
                        x -> rfnDeviceDao.getDeviceForExactIdentifier(x.getRfnIdentifier())));
                Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
                Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());

                List<RouteInfo> routes = new ArrayList<>();
                boolean hasMissingLocation = false;
                for (RouteData data : response.getRouteData()) {
                    RfnDevice routeDevice = devices.get(data.getRfnIdentifier());
                    if(routeDevice == null){
                        rfnDeviceCreationService.create(data.getRfnIdentifier());
                        log.info(data.getRfnIdentifier()+" is not found. Creating device.");
                        hasMissingLocation = true;
                    } else {
                        PaoLocation paoLocation = locations.get(routeDevice.getPaoIdentifier());
                        if (paoLocation != null && !hasMissingLocation) {
                            FeatureCollection location =
                                paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
                            routes.add(new RouteInfo(device, data, location, accessor));
                        }else{
                            hasMissingLocation = true;
                            log.info("Location is not found for "+ routeDevice);
                        }
                    }
                }
                if(hasMissingLocation){
                    log.info("Location is missing for one of the devices that are part of the route for "+ device);
                }
                return routes;
            } else {
                throw new NmNetworkException(nmError, response.getReplyType().name());
            }
        } catch (ExecutionException e) {
            throw new NmNetworkException(commsError, e, "commsError");
        }
    }

    @Override
    public List<Neighbor> getNeighbors(int deviceId, MessageSourceAccessor accessor) throws NmNetworkException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        RfnNeighborDataRequest request = new RfnNeighborDataRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());

        log.debug("Sending get neighbors request to Network Manager: " + request);

        BlockingJmsReplyHandler<RfnNeighborDataReply> reply = new BlockingJmsReplyHandler<>(RfnNeighborDataReply.class);

        try {
            neighborReplyTemplate.send(request, reply);
            RfnNeighborDataReply response = reply.waitForCompletion();
            log.debug("response: " + response);
            if (response.getReplyType() == RfnNeighborDataReplyType.OK) {
                Map<RfnIdentifier, RfnDevice> devices =
                    response.getNeighborData().stream().collect(Collectors.toMap(x -> x.getRfnIdentifier(),
                        x -> rfnDeviceDao.getDeviceForExactIdentifier(x.getRfnIdentifier())));
                Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
                Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());

                List<Neighbor> neighbors = new ArrayList<>();
                for (NeighborData data : response.getNeighborData()) {
                    RfnDevice neighborDevice = devices.get(data.getRfnIdentifier());
                    if (neighborDevice == null) {
                        rfnDeviceCreationService.create(data.getRfnIdentifier());
                        log.info(data.getRfnIdentifier()+" is not found. Creating device.");
                    } else {
                        PaoLocation paoLocation = locations.get(neighborDevice.getPaoIdentifier());
                        if (paoLocation != null) {
                            FeatureCollection location =
                                paoLocationService.getFeatureCollection(Lists.newArrayList(paoLocation));
                            neighbors.add(new Neighbor(neighborDevice, location, data, accessor));
                        }else{
                            log.info("Location is not found for "+ neighborDevice);
                        }
                    }
                }
                return neighbors;
            } else {
                throw new NmNetworkException(nmError, response.getReplyType().name());
            }
        } catch (ExecutionException e) {
            throw new NmNetworkException(commsError, e, "commsError");
        }
    }
}
