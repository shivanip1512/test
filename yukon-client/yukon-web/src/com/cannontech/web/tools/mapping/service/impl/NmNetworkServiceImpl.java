package com.cannontech.web.tools.mapping.service.impl;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.neighbor.Neighbor;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.neighbor.Neighbors;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.route.RfnRoute;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.ExceptionToNullHelper;
import com.cannontech.mbean.ServerDatabaseCache;
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
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NmNetworkServiceImpl implements NmNetworkService {

    private static final Logger log = YukonLogManager.getLogger(NmNetworkServiceImpl.class);

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private ServerDatabaseCache dbCache;
   
    @Override
    public Pair<RfnDevice, FeatureCollection> getParent(int deviceId, MessageSourceAccessor accessor) throws NmCommunicationException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        PaoLocation deviceLocation = paoLocationDao.getLocation(deviceId);

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaDataMultiResult = metadataMultiService
                .getMetadataForDeviceRfnIdentifier(device.getRfnIdentifier(),
                        Set.of(RfnMetadataMulti.BATTERY_NODE_PARENT));
        RfnMetadataMultiQueryResult metadataMulti = metaDataMultiResult.get(device.getRfnIdentifier());
        if (metadataMulti.isValidResultForMulti(RfnMetadataMulti.BATTERY_NODE_PARENT)) {
            RfnIdentifier rfnIdentifier = (RfnIdentifier) metadataMulti.getMetadatas()
                    .get(RfnMetadataMulti.BATTERY_NODE_PARENT);
            if (rfnIdentifier.is_Empty_()) {
                return null;
            }
            RfnDevice parent = ExceptionToNullHelper.nullifyExceptions(() -> rfnDeviceCreationService.getOrCreate(rfnIdentifier));
            if (parent == null) {
                // couldn't find or create parent
                return null;
            }
            PaoLocation parentLocation = paoLocationDao.getLocation(parent.getPaoIdentifier().getPaoId());
            if (parentLocation == null) {
                return Pair.of(parent, null);
            }
            FeatureCollection feature = paoLocationService
                    .getFeatureCollection(Lists.newArrayList(parentLocation));
            if (deviceLocation != null && parentLocation != null) {
                double distanceTo = deviceLocation.distanceTo(parentLocation, DistanceUnit.MILES);
                DecimalFormat df = new DecimalFormat("#.####");
                feature.setProperty("distance", df.format(distanceTo));
            }
            return Pair.of(parent, feature);

        } else {
            // no parent
            return null;
        }
    }

    @Override
    public List<Pair<RfnDevice, FeatureCollection>> getRoute(int deviceId, MessageSourceAccessor accessor)
            throws NmCommunicationException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaDataMultiResult = metadataMultiService
                .getMetadataForDeviceRfnIdentifier(device.getRfnIdentifier(),
                        Set.of(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE));

        RfnMetadataMultiQueryResult metadataMulti = metaDataMultiResult.get(device.getRfnIdentifier());

        if (metadataMulti.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE)) {
            RfnRoute route = (RfnRoute) metadataMulti.getMetadatas().get(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE);

            if (route.isEmpty()) {
                log.error("Route is empty for device {}", deviceId);
                return new ArrayList<>();
            }
            
            Map<RfnIdentifier, RfnDevice> devices = route.stream()
                    // remove nulls returned from NM
                    .filter(Objects::nonNull)
                    .filter(identifier -> !identifier.is_Empty_())
                    .map(rfnIdentifier -> ExceptionToNullHelper.nullifyExceptions(() -> rfnDeviceCreationService.getOrCreate(rfnIdentifier)))
                    // remove devices not created or found
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(data -> data.getRfnIdentifier(), data -> data));

            Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
            Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());

            List<Pair<RfnDevice, FeatureCollection>> result = new ArrayList<>();

            route.forEach(identifier -> {
                if (identifier == null || identifier.is_Empty_()) {
                    result.add(null);
                } else {
                    RfnDevice routeDevice = devices.get(identifier);
                    if (routeDevice == null) {
                        result.add(null);
                    } else {
                        PaoLocation location = locations.get(routeDevice.getPaoIdentifier());
                        if (location == null) {
                            result.add(Pair.of(routeDevice, null));
                        } else {
                            FeatureCollection feature = paoLocationService
                                    .getFeatureCollection(Lists.newArrayList(location));
                            result.add(Pair.of(routeDevice, feature));
                        }
                    }
                }
            });
            return result;
        } else {
            log.error("PRIMARY_FORWARD_ROUTE for device {} is not valid", device);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Pair<RfnDevice, FeatureCollection>> getNeighbors(int deviceId, MessageSourceAccessor accessor)
            throws NmCommunicationException {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        PaoLocation deviceLocation = paoLocationDao.getLocation(deviceId);

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaDataMultiResult = metadataMultiService
                .getMetadataForDeviceRfnIdentifier(device.getRfnIdentifier(),
                        Set.of(RfnMetadataMulti.NEIGHBORS));

        RfnMetadataMultiQueryResult metadataMulti = metaDataMultiResult.get(device.getRfnIdentifier());

        if (metadataMulti.isValidResultForMulti(RfnMetadataMulti.NEIGHBORS)) {
            Neighbors neighbors = (Neighbors) metadataMulti.getMetadatas().get(RfnMetadataMulti.NEIGHBORS);

            if (neighbors.isEmpty()) {
                log.warn("No neighbors found for device {}", deviceId);
                return new ArrayList<>();
            }

            Map<RfnIdentifier, RfnDevice> devices = neighbors.stream()
                    // remove devices that do not have identifier or identifier is not valid
                    .filter(neighbor -> neighbor.getRfnIdentifier() != null && !neighbor.getRfnIdentifier().is_Empty_())
                    .map(neighbor -> ExceptionToNullHelper.nullifyExceptions(() -> rfnDeviceCreationService.getOrCreate(neighbor.getRfnIdentifier())))
                    // remove devices not created or found
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(data -> data.getRfnIdentifier(), data -> data));
            
            Map<RfnIdentifier, Neighbor> deviceToNeighbor = neighbors.stream()
                    // remove devices that do not have identifier or identifier is not valid
                    .filter(neighbor -> neighbor.getRfnIdentifier() != null && !neighbor.getRfnIdentifier().is_Empty_())
                    .collect(Collectors.toMap(neighbor -> neighbor.getRfnIdentifier(), neighbor -> neighbor));

            Set<PaoLocation> allLocations = paoLocationDao.getLocations(devices.values());
            Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(allLocations, c -> c.getPaoIdentifier());

            List<Pair<RfnDevice, FeatureCollection>> result = new ArrayList<>();

            devices.forEach((identifier, rfnDevice) -> {
                PaoLocation location = locations.get(rfnDevice.getPaoIdentifier());
                if (location == null) {
                    result.add(Pair.of(rfnDevice, null));
                } else {
                    FeatureCollection feature = paoLocationService
                            .getFeatureCollection(Lists.newArrayList(location));
                    Neighbor neighbor = deviceToNeighbor.get(identifier);
                    feature.setProperty("neighborData", neighbor);
                    if (deviceLocation != null && location != null) {
                        double distanceTo = deviceLocation.distanceTo(location, DistanceUnit.MILES);
                        DecimalFormat df = new DecimalFormat("#.####");
                        feature.setProperty("distance", df.format(distanceTo));
                    }
                    List<String> flags = new ArrayList<>();
                    neighbor.getNeighborData().getNeighborFlags().forEach(flag -> {
                        flags.add(accessor.getMessage("yukon.web.modules.operator.mapNetwork.neighborFlag." + flag.name()));
                    });
                    feature.setProperty("commaDelimitedNeighborFlags", String.join(", ", flags));
                    result.add(Pair.of(rfnDevice, feature));
                }
            });
            return result;
        } else {
            log.warn("NEIGHBORS for device {} is not valid", device);
            return new ArrayList<>();
        }
    }

    @Override
    public NodeComm getNodeCommStatusFromMultiQueryResult(RfnDevice rfnDevice, RfnMetadataMultiQueryResult metadata) {
        if (metadata.isValidResultForMulti(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM)) {
            NodeComm comm = (NodeComm) metadata.getMetadatas().get(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM);
            RfnIdentifier reverseGateway = comm.getGatewayRfnIdentifier();
            DynamicRfnDeviceData deviceData = rfnDeviceDao.findDynamicRfnDeviceData(rfnDevice.getPaoIdentifier().getPaoId());
            if (deviceData == null) {
                log.info("Device:{} Primary Gateway:None Reverse Gateway:{} using Reverse Gateway", rfnDevice, reverseGateway);
                return comm;
            }
            RfnIdentifier primaryForwardGateway = deviceData.getGateway().getRfnIdentifier();
            if (reverseGateway == null) {
                log.info("Device:{} Primary Gateway:{} Reverse Gateway:None Status is unknown", rfnDevice,
                        primaryForwardGateway);
            } else if (reverseGateway.equals(primaryForwardGateway)) {
                return comm;
            } else {
                log.info("Device:{} Primary Gateway:{} Reverse Gateway:{} do not match, unable to determine comm status",
                        rfnDevice, primaryForwardGateway, reverseGateway);
            }
        } else {
            log.error("NM didn't return REVERSE_LOOKUP_NODE_COMM for {}, unable to determine comm status",
                    rfnDevice);
        }
        return null;
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
                data = data.entrySet().stream()
                                      .filter(e -> !e.getValue().isEmpty())
                                      .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
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
                RouteData routeData = (RouteData) result.getValue().getMetadatas().get(multi);
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
            Color color = Color.values()[i.getAndIncrement() % Color.values().length];
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
				RouteData routeData = (RouteData) data.getValue().getMetadatas().get(PRIMARY_FORWARD_ROUTE_DATA);
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
        String hexColor = YukonColorPalette.WHITE.getHexValue();
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