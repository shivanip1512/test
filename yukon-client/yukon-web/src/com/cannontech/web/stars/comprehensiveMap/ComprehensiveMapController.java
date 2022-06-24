package com.cannontech.web.stars.comprehensiveMap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionActionUrl;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.ExceptionToNullHelper;
import com.cannontech.common.util.tree.Node;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.tools.mapping.model.NetworkMap;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.ColorCodeBy;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.DescendantCount;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.HopCount;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.LinkQuality;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.service.NetworkTreeService;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;

@RequestMapping("/comprehensiveMap/*")
@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class ComprehensiveMapController {

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private NmNetworkService nmNetworkService;
    @Autowired private NetworkTreeService networkTreeService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    private Instant networkTreeUpdateTime = null;

    private static final Logger log = YukonLogManager.getLogger(ComprehensiveMapController.class);

    @GetMapping("home")
    public String home(ModelMap model) {

        NetworkMapFilter filter = new NetworkMapFilter();
        model.addAttribute("filter", filter);

        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);

        model.addAttribute("colorCodeByOptions", ColorCodeBy.values());
        model.addAttribute("linkQualityOptions", LinkQuality.values());
        model.addAttribute("descendantCountOptions", DescendantCount.values());
        model.addAttribute("hopCountOptions", HopCount.values());
        model.addAttribute("gatewayPaoTypes", PaoType.getRfGatewayTypes());
        model.addAttribute("relayPaoTypes", PaoType.getRfRelayTypes());
        model.addAttribute("wifiPaoTypes", PaoType.getWifiTypes());

        return "comprehensiveMap/map.jsp";
    }

    @GetMapping("filter")
    public @ResponseBody Map<String, Object> filter(@ModelAttribute("filter") NetworkMapFilter filter,
            YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        NetworkMap map = null;
        // empty filters mean all
        if (filter.getLinkQuality().isEmpty()) {
            filter.setLinkQuality(Lists.newArrayList(LinkQuality.values()));
        }
        if (filter.getDescendantCount().isEmpty()) {
            filter.setDescendantCount(Lists.newArrayList(DescendantCount.values()));
        }
        if (filter.getHopCount().isEmpty()) {
            filter.setHopCount(Lists.newArrayList(HopCount.values()));
        }
        try {
            map = nmNetworkService.getNetworkMap(filter, accessor);
            String groupName = addDevicesToDeviceGroup(map);
            json.put("collectionActionRedirect",
                    CollectionActionUrl.COLLECTION_ACTIONS.getUrl() + "?collectionType=group&group.name=" + groupName);
            json.put("collectionGroup", groupName);
        } catch (NmNetworkException | NmCommunicationException e) {
            String errorMsg = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.nmError");
            log.error(errorMsg, e);
            json.put("errorMsg", errorMsg);
        }
        json.put("map", map);
        return json;
    }

    @GetMapping("search")
    public @ResponseBody Map<String, Object> searchForNode(String searchText) {
        Map<String, Object> json = new HashMap<>();
        Set<Integer> foundPaoIds = new HashSet<Integer>();
        // search for a Sensor Serial Number with the provided text
        RfnDevice sensor = rfnDeviceDao.findDeviceBySensorSerialNumber(searchText);
        if (sensor != null) {
            foundPaoIds.add(sensor.getPaoIdentifier().getPaoId());
        }
        // search for a Meter Number with the provided text
        try {
            YukonMeter meter = meterDao.getForMeterNumber(searchText);
            if (meter != null) {
                foundPaoIds.add(meter.getDeviceId());
            }
        } catch (NotFoundException e) {
            // continue looking for matches
        }
        // search by partial device name
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(searchText, true);
        if (!paos.isEmpty()) {
            List<Integer> paoIds = paos.stream().map(pao -> pao.getPaoIdentifier().getPaoId()).collect(Collectors.toList());
            foundPaoIds.addAll(paoIds);
        }

        json.put("paoIds", foundPaoIds);
        return json;
    }

    @GetMapping("download")
    public void download(String groupName, YukonUserContext userContext, HttpServletResponse response) throws IOException {
        String[] headerRow = retrieveDownloadHeaderRow(userContext);

        DeviceGroup group = deviceGroupService.findGroupName(groupName);
        DeviceCollection collection = deviceGroupCollectionHelper.buildDeviceCollection(group);

        log.debug("Devices in a group {}", collection.getDeviceCount());

        Set<RfnIdentifier> rfnIdentifiers = collection.getDeviceList().stream()
                .map(device -> rfnDeviceDao.getDeviceForId(device.getDeviceId()).getRfnIdentifier())
                .collect(Collectors.toSet());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = new HashMap<>();
        log.debug("Getting data for download for {} devices", rfnIdentifiers.size());
        try {
            metaData = metadataMultiService.getMetadataForDeviceRfnIdentifiers(rfnIdentifiers,
                    Set.of(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM,
                            RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA,
                            RfnMetadataMulti.NODE_DATA,
                            RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA));
        } catch (NmCommunicationException e1) {
            log.warn("caught exception in download", e1);
        }

        List<String[]> dataRows = retrieveDownloadDataRows(metaData, userContext);

        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "ComprehensiveMapDownload_" + now + ".csv");
    }

    @GetMapping("downloadNetworkInfo")
    public void downloadNetworkInfo(YukonUserContext userContext, HttpServletResponse response) throws IOException {
        String[] headerRow = retrieveDownloadHeaderRow(userContext);

        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());

        Set<RfnIdentifier> gatewayRfnIdentifiers = gateways.stream()
                .map(gateway -> gateway.getRfnIdentifier())
                .collect(Collectors.toSet());

        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = new HashMap<>();
        log.debug("Getting data for download for {} gateways", gatewayRfnIdentifiers.size());

        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = "NetworkInformationDownload_" + now + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(response.getOutputStream())); CSVWriter csvWriter = new CSVWriter(writer);) {
            csvWriter.writeNext(headerRow);

            for (List<RfnIdentifier> splitGateways : Iterables.partition(gatewayRfnIdentifiers, 5)) {
                try {
                    metaData = metadataMultiService.getMetadataForGatewayRfnIdentifiers(new HashSet<RfnIdentifier>(splitGateways),
                            Set.of(RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM,
                                    RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA,
                                    RfnMetadataMulti.NODE_DATA,
                                    RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA));
                    List<String[]> dataRows = retrieveDownloadDataRows(metaData, userContext);
                    csvWriter.writeAll(dataRows);
                } catch (NmCommunicationException e1) {
                    log.warn("caught exception in download", e1);
                }
            }

        }
    }

    /**
     * Adding devices(gateways, meters and LCRs) to a temporary group which will be used for accessing all devices required for
     * download functionality.
     */
    private String addDevicesToDeviceGroup(NetworkMap map) {
        log.debug("Devices in map {}", map.getTotalDevices());
        log.debug("Devices without location {}", map.getDevicesWithoutLocation().size());
        // create collection action group
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        for (FeatureCollection feature : map.getMappedDevices().values()) {
            List<YukonPao> devices = feature.getFeatures().stream()
                    .map(d -> new SimpleDevice(d.getProperty("paoIdentifier")))
                    .collect(Collectors.toList());
            devices.addAll(map.getDevicesWithoutLocation());
            deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        }
        return tempGroup.getFullName();
    }

    private String[] retrieveDownloadHeaderRow(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        // device name, meter number, device type, sensor s/n, lat, long, primary gateway, comm status, mac address, node s/n,
        // link cost, hop count, descendant count, next hop, path cost
        String[] headerRow = new String[15];

        String baseKey = "yukon.web.modules.operator.mapNetwork.";

        headerRow[0] = accessor.getMessage(baseKey + "device");
        headerRow[1] = accessor.getMessage(baseKey + "meterNumber");
        headerRow[2] = accessor.getMessage(baseKey + "type");
        headerRow[3] = accessor.getMessage(baseKey + "serialNumber");
        headerRow[4] = accessor.getMessage(baseKey + "location.latitude");
        headerRow[5] = accessor.getMessage(baseKey + "location.longitude");
        headerRow[6] = accessor.getMessage(baseKey + "primaryGateway");
        headerRow[7] = accessor.getMessage(baseKey + "status");
        headerRow[8] = accessor.getMessage(baseKey + "macAddress");
        headerRow[9] = accessor.getMessage(baseKey + "nodeSN");
        headerRow[10] = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.colorCodeBy.LINK_QUALITY");
        headerRow[11] = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.colorCodeBy.HOP_COUNT");
        headerRow[12] = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.colorCodeBy.DESCENDANT_COUNT");
        headerRow[13] = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.nextHop");
        headerRow[14] = accessor.getMessage(baseKey + "totalCost");

        return headerRow;
    }

    private List<String[]> retrieveDownloadDataRows(Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData,
            YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<RfnDevice> devices = metaData.keySet().stream()
                .map(rfnIdentifier -> rfnDeviceLookupService.findRfnDevices(rfnIdentifier))
                // remove devices not created or found
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Get Pao Locations for all devices
        Map<Integer, PaoLocation> locations = paoLocationDao.getLocations(devices).stream()
                .collect(Collectors.toMap(l -> l.getPaoIdentifier().getPaoId(), l -> l));

        // Get Rfn Device Data for all devices
        List<DynamicRfnDeviceData> data = rfnDeviceDao.getDynamicRfnDeviceData(devices.stream()
                .map(device -> device.getPaoIdentifier().getPaoId()).collect(Collectors.toList()));
        Map<RfnIdentifier, DynamicRfnDeviceData> deviceDataMap = data.stream()
                .collect(Collectors.toMap(d -> d.getDevice().getRfnIdentifier(), d -> d));

        List<String[]> dataRows = Lists.newArrayList();
        log.debug("Got data from NM for {} devices", metaData.keySet().size());
        for (RfnDevice rfnDevice : devices) {
            String[] dataRow = new String[15];
            LiteYukonPAObject pao = cache.getAllPaosMap().get(rfnDevice.getPaoIdentifier().getPaoId());
            dataRow[0] = pao.getPaoName();
            SimpleMeter meter = cache.getAllMeters().get(rfnDevice.getPaoIdentifier().getPaoId());
            if (meter != null) {
                dataRow[1] = meter.getMeterNumber();
            }
            dataRow[2] = pao.getPaoType().getPaoTypeName();
            dataRow[3] = rfnDevice.getRfnIdentifier().getSensorSerialNumber();
            PaoLocation location = locations.get(rfnDevice.getPaoIdentifier().getPaoId());
            if (location != null) {
                dataRow[4] = String.valueOf(location.getLatitude());
                dataRow[5] = String.valueOf(location.getLongitude());
            }
            RfnMetadataMultiQueryResult metadata = metaData.get(rfnDevice.getRfnIdentifier());
            if (metadata != null) {
                String statusString = accessor.getMessage("yukon.web.modules.operator.mapNetwork.status.UNKNOWN");
                NodeComm comm = nmNetworkService.getNodeCommStatusFromMultiQueryResult(rfnDevice, metadata);
                if (comm != null && comm.getNodeCommStatus() != null) {
                    statusString = accessor
                            .getMessage("yukon.web.modules.operator.mapNetwork.status." + comm.getNodeCommStatus());
                }
                dataRow[7] = statusString;

                DynamicRfnDeviceData deviceData = deviceDataMap.get(rfnDevice.getRfnIdentifier());
                if (deviceData != null) {
                    dataRow[6] = deviceData.getGateway().getName();
                    dataRow[12] = String.valueOf(deviceData.getDescendantCount());
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
                    NodeData node = (NodeData) metadata.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
                    dataRow[8] = node.getMacAddress();
                    dataRow[9] = node.getNodeSerialNumber();
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA)) {
                    NeighborData neighbor = (NeighborData) metadata.getMetadatas()
                            .get(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA);
                    LinkQuality linkQuality = LinkQuality.getLinkQuality(neighbor);
                    String linkQualityFormatted = accessor.getMessage(linkQuality.getFormatKey());
                    dataRow[10] = linkQualityFormatted;
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA)) {
                    RouteData routeData = (RouteData) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA);
                    dataRow[11] = String.valueOf(routeData.getHopCount());
                    RfnIdentifier nextHop = routeData.getNextHopRfnIdentifier();
                    if (nextHop != null) {
                        RfnDevice nextHopDevice = ExceptionToNullHelper.nullifyExceptions(() -> rfnDeviceCreationService.getOrCreate(nextHop));
                        if (nextHopDevice != null) {
                            dataRow[13] = nextHopDevice.getName();
                        }
                    }
                    dataRow[14] = String.valueOf(routeData.getTotalCost());
                }
            }
            dataRows.add(dataRow);
        }

        log.debug("Generated {} rows for CSV file", dataRows.size());

        return dataRows;
    }

    @GetMapping("allGateways")
    public @ResponseBody FeatureCollection allGateways() {
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        return paoLocationService.getLocationsAsGeoJson(gateways);
    }

    @GetMapping("allRelays")
    public @ResponseBody FeatureCollection allRelays() {
        List<RfnDevice> relays = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfRelayTypes());
        return paoLocationService.getLocationsAsGeoJson(relays);
    }

    @GetMapping("allPrimaryRoutes")
    public @ResponseBody Map<String, Object> primaryRoutes(Integer[] gatewayIds) {
        return getNetworkTree(Arrays.asList(gatewayIds));
    }
    
    @GetMapping("primaryNeighborDataForDevices")
    public @ResponseBody Map<String, Object> primaryNeighborDataForDevices(Integer[] deviceIds) {
        Map<String, Object> json = new HashMap<>();
        Set<RfnIdentifier> rfnIds = new HashSet<RfnIdentifier>();
        Set<RfnDevice> devices = new HashSet<RfnDevice>();
        Map<Integer, NeighborData> deviceNeighborData = new HashMap<Integer, NeighborData>();
        for (Integer deviceId : deviceIds) {
            RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
            devices.add(device);
            rfnIds.add(device.getRfnIdentifier());
        }
        try {
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> neighborDataResult = metadataMultiService
                    .getMetadataForDeviceRfnIdentifiers(rfnIds, Set.of(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA));
            for (RfnDevice device : devices) {
                RfnMetadataMultiQueryResult neighborResult = neighborDataResult.get(device.getRfnIdentifier());
                if (neighborResult.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA)) {
                    NeighborData neighborData = (NeighborData) neighborResult.getMetadatas().get(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA);
                    deviceNeighborData.put(device.getPaoIdentifier().getPaoId(), neighborData);
                }
            }
            json.put("neighborData", deviceNeighborData);
        } catch (NmCommunicationException e) {
            log.warn("Error connecting to NM getting the primary neighbor data for devices.", e);
            json.put("errorMsg", e.getMessage());
        }
        return json;
    }

    private Map<String, Object> getNetworkTree(List<Integer> gatewayIds) {
        Map<String, Object> json = new HashMap<>();
        try {
            List<Node<Pair<Integer, FeatureCollection>>> tree = networkTreeService.getNetworkTree(gatewayIds);
            json.put("tree", tree);
            networkTreeUpdateTime = networkTreeService.getNetworkTreeUpdateTime();
            json.put("routeLastUpdatedDateTime", networkTreeUpdateTime == null ? null : networkTreeUpdateTime.getMillis());
            json.put("isUpdatePossible", networkTreeService.isNetworkTreeUpdatePossible());
        } catch (NmNetworkException | NmCommunicationException e) {
            json.put("errorMsg", e.getMessage());
        }

        return json;
    }

    @GetMapping("networkTree")
    public @ResponseBody Map<String, Object> primaryRoutes(Integer deviceId) {
        Set<Integer> gatewayIds = new HashSet<Integer>();
        // check if device is gateway
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        if (device.getPaoIdentifier().getPaoType().isRfGateway()) {
            gatewayIds.add(deviceId);
        } else {
            gatewayIds = rfnDeviceDao.getGatewayIdsForDevices(new HashSet<>(Arrays.asList(deviceId)));
        }
        return getNetworkTree(new ArrayList<>(gatewayIds));
    }

    @GetMapping("getRouteDetails")
    public @ResponseBody Map<String, Object> getRouteDetails() {
        Map<String, Object> json = Maps.newHashMap();
        Instant lastUpdateDateTime = networkTreeService.getNetworkTreeUpdateTime();
        json.put("routeLastUpdatedDateTime", lastUpdateDateTime == null ? null : lastUpdateDateTime.getMillis());
        json.put("isUpdatePossible", networkTreeService.isNetworkTreeUpdatePossible());
        json.put("updateRoutes", networkTreeService.isNetworkTreeUpdated(networkTreeUpdateTime));
        return json;
    }

    @PostMapping("requestNetworkTreeUpdate")
    public @ResponseBody Map<String, Object> requestNetworkTreeUpdate(YukonUserContext yukonUserContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = Maps.newHashMap();
        boolean isUpdateRequestSent = networkTreeService.requestNetworkTreeUpdate();
        json.put("isUpdateRequestSent", isUpdateRequestSent);
        if (isUpdateRequestSent) {
            json.put("msgText",
                    accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.routeUpdateRequestSent.successMsg"));
        } else {
            json.put("msgText",
                    accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.routeUpdateRequestSent.errorMsg"));
        }
        return json;
    }
}