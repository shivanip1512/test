package com.cannontech.web.tools.mapping.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.model.PaoLocationDetails;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.EntityType;
import com.cannontech.common.rfn.message.metadatamulti.NodeData;
import com.cannontech.common.rfn.message.metadatamulti.PrimaryGatewayComm;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.tools.mapping.model.Filter;
import com.cannontech.web.tools.mapping.model.Group;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
public class MapController {
    
    private static final Logger log = YukonLogManager.getLogger(MapController.class);
    
    private final static String baseKey = "yukon.web.modules.tools.map.";
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PointService pointService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnGatewayDataCache gatewayDataCache;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private OutageMonitorDao outageMonitorDao;
    @Autowired private OutageMonitorService outageMonitorService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    
    List<BuiltInAttribute> attributes = ImmutableList.of(
        BuiltInAttribute.VOLTAGE,
        BuiltInAttribute.VOLTAGE_PHASE_A,
        BuiltInAttribute.VOLTAGE_PHASE_B,
        BuiltInAttribute.VOLTAGE_PHASE_C,
        BuiltInAttribute.USAGE,
        BuiltInAttribute.SERVICE_STATUS);

    /**
     * Meant for device collections that are not static. Like collections based on 
     * the violations device group of a status point or outage monitor.
     */
    @GetMapping("/map/dynamic")
    public String dynamic(ModelMap model, DeviceCollection deviceCollection, String monitorType, 
                          Integer monitorId, Boolean violationsOnly, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        model.addAttribute("deviceCollection", deviceCollection);
        String description = accessor.getMessage(deviceCollection.getDescription());
        model.addAttribute("description", StringEscapeUtils.escapeXml11(description));
        model.addAttribute("dynamic", true);
        model.addAttribute("monitorType", monitorType);
        model.addAttribute("monitorId", monitorId);
        model.addAttribute("violationsOnly", violationsOnly);
        if (monitorId != null) {
            if (monitorType.equalsIgnoreCase("Device Data")) {
                DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
                DeviceGroup all = monitor.getGroup();
                DeviceCollection allCollection = deviceGroupCollectionHelper.buildDeviceCollection(all);
                model.addAttribute("deviceCollection", allCollection);
                DeviceGroup violations = monitor.getViolationGroup();
                DeviceCollection violationsCollection = deviceGroupCollectionHelper.buildDeviceCollection(violations);
                model.addAttribute("violationsCollection", violationsCollection);
            } else if (monitorType.equalsIgnoreCase("Outage")) {
                OutageMonitor monitor = outageMonitorDao.getById(monitorId);
                DeviceGroup all = deviceGroupService.findGroupName(monitor.getGroupName());
                DeviceCollection allCollection = deviceGroupCollectionHelper.buildDeviceCollection(all);
                model.addAttribute("deviceCollection", allCollection);
                DeviceGroup violations = outageMonitorService.getOutageGroup(monitor.getName());
                DeviceCollection violationsCollection = deviceGroupCollectionHelper.buildDeviceCollection(violations);
                model.addAttribute("violationsCollection", violationsCollection);
            }

        }
        
        return "map/map.jsp";
    }

    @GetMapping("/map")
    public String map(ModelMap model, DeviceCollection deviceCollection, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        model.addAttribute("deviceCollection", deviceCollection);
        String description = accessor.getMessage(deviceCollection.getDescription());
        model.addAttribute("description", StringEscapeUtils.escapeXml11(description));
        
        Map<AttributeGroup, Set<BuiltInAttribute>> groups = BuiltInAttribute.getAllGroupedStatusTypeAttributes();
        model.addAttribute("attributes", objectFormattingService.sortDisplayableValues(groups, userContext));
        
        Filter filter = new Filter();
        String message = accessor.getMessage(baseKey + "filter.selectedDevices");
        DeviceCollection filteredCollection = deviceGroupCollectionHelper.createDeviceGroupCollection(deviceCollection.iterator(), message);
        Map<String, String> params = filteredCollection.getCollectionParameters();
        String groupName = params.get("group.name");
        model.addAttribute("filteredCollection", filteredCollection);
        filter.setTempDeviceGroupName(groupName);
        model.addAttribute("filter", filter);
        
        return "map/map.jsp";
    }
    
    @GetMapping("/map/device/{id}/info")
    public String info(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        YukonPao pao = databaseCache.getAllPaosMap().get(id);
        PaoType type = pao.getPaoIdentifier().getPaoType();
        DisplayablePao displayable = paoLoadingService.getDisplayablePao(pao);
        if (displayable instanceof DisplayableMeter) {
            DisplayableMeter meter = (DisplayableMeter) displayable;
            if (StringUtils.isNotBlank(meter.getMeter().getRoute())) {
                model.addAttribute("showRoute", true);
            }
            if (!type.isRfn() && meter.getMeter().getSerialOrAddress() != null) {
                if (StringUtils.isNotBlank(meter.getMeter().getSerialOrAddress())) {
                    model.addAttribute("address", meter.getMeter().getSerialOrAddress());
                }
            }
            if (StringUtils.isNotBlank(meter.getMeter().getMeterNumber())) {
                model.addAttribute("showMeterNumber", true);
            }
        }
        if (type.isRfn()) {
            RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(id);
            model.addAttribute("sensorSN", rfnDevice.getRfnIdentifier().getSensorSerialNumber());

            if (type.isRfGateway()) {
                // Gateways should get information from RfnGatewayData (not metadata)
                try {
                    RfnGatewayData gateway = gatewayDataCache.get(pao.getPaoIdentifier());
                    model.addAttribute("gatewayIPAddress", gateway.getIpAddress());
                    model.addAttribute("macAddress", gateway.getMacAddress());
                    String statusString = accessor.getMessage("yukon.web.modules.operator.gateways.connectionStatus." + gateway.getConnectionStatus().toString());
                    model.addAttribute("deviceStatus", statusString);
                } catch (NmCommunicationException e) {
                    log.error("Failed to get gateway data for " + id, e);
                    model.addAttribute("errorMsg", e.getMessage());
                }
            } else {
                String nmError = accessor.getMessage("yukon.web.modules.operator.mapNetwork.exception.metadataError");
                try {
                    Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData =
                        metadataMultiService.getMetadata(EntityType.NODE, rfnDevice.getRfnIdentifier(),
                            Set.of(RfnMetadataMulti.PRIMARY_GATEWAY_COMM, RfnMetadataMulti.NODE_DATA));
                    RfnMetadataMultiQueryResult metadata = metaData.get(rfnDevice.getRfnIdentifier());
                    if (metadata.getResultType() != RfnMetadataMultiQueryResultType.OK) {
                        log.error("NM returned query result:" + metadata.getResultType() + " message:" + metadata.getResultMessage()
                            + " for device:" + rfnDevice);
                        model.addAttribute("errorMsg", nmError);
                    } else {
                        PrimaryGatewayComm comm = (PrimaryGatewayComm) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_COMM);
                        if (comm != null) {
                            RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(comm.getRfnIdentifier());
                            RfnGateway rfnGateway = rfnGatewayService.getGatewayByPaoId(gateway.getPaoIdentifier().getPaoId());
                            String statusString = accessor.getMessage("yukon.web.modules.operator.mapNetwork.status." + comm.getCommStatusType());
                            model.addAttribute("deviceStatus", statusString);
                            model.addAttribute("primaryGatewayName", rfnGateway.getNameWithIPAddress());
                            model.addAttribute("primaryGateway", gateway);
                        } else {
                            // ignore, status will be set to "UNKNOWN"
                            log.error("NM didn't return communication status for " + rfnDevice);
                        }
                        
                        NodeData nodeData = (NodeData) metadata.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
                        if (nodeData != null) {
                            model.addAttribute("macAddress", nodeData.getMacAddress());
                            model.addAttribute("nodeSN", nodeData.getNodeSerialNumber());
                        } else {
                            log.error("NM didn't return node data for " + rfnDevice);
                        }  
                    }
                    
                } catch (NmCommunicationException e) {
                    log.error("Failed to get metadata for " + id, e);
                    model.addAttribute("errorMsg", nmError);
                } catch (NotFoundException e) {
                    log.error("Failed to find RFN Device for " + id, e);           
                }
            }
        }

        model.addAttribute("pao", displayable);
        
        List<Attribute> supported = new ArrayList<>();

        for(Attribute attribute : attributes) {
            if (attributeService.isAttributeSupported(pao, attribute)) {
                supported.add(attribute);
            }
        }
        model.addAttribute("attributes", supported);
        
        model.addAttribute("hasNotes", paoNotesService.hasNotes(id));
        
        return "map/info.jsp";
    }
    
    @DeleteMapping("/map/device/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public @ResponseBody Map<String, String> deleteCoordinates(@PathVariable int id, YukonUserContext userContext, 
                                                    HttpServletResponse response) {
        
        Map<String, String> json = new HashMap<>();
        try {
            paoLocationService.deleteLocationForPaoId(id);
            log.debug("Deleted coordinates for paoId " + id);
            response.setStatus(HttpStatus.OK.value());
            return json;
        } catch (Exception e) {
            log.error("Error deleting coordinates for paoId " + id);
            
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String message = accessor.getMessage("yukon.web.modules.tools.map.deleteCoordinates.error", e.getMessage());
            json.put("message", message);
            
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return json;
        }
    }
    
    @GetMapping("/map/locations/{monitorType}/{monitorId}")
    public @ResponseBody Map<String, Object> monitorLocations(@PathVariable String monitorType, 
                                                              @PathVariable int monitorId, Boolean violationsOnly) {
        
        Map<String, Object> json = new HashMap<String, Object>();
        DeviceGroup violations = null;
        DeviceGroup all = null;
        if (monitorType.equalsIgnoreCase("Device Data")) {
            DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
            violations = monitor.getViolationGroup();
            all = monitor.getGroup();
        } else if (monitorType.equalsIgnoreCase("Outage")) {
            OutageMonitor monitor = outageMonitorDao.getById(monitorId);
            violations = outageMonitorService.getOutageGroup(monitor.getName());
            all = deviceGroupService.findGroupName(monitor.getGroupName());
        }
        
        if (violations != null && all != null) {
            DeviceCollection violationsCollection = deviceGroupCollectionHelper.buildDeviceCollection(violations);
            if (violationsOnly != null && violationsOnly == true) {
                FeatureCollection violationLocations = paoLocationService.getLocationsAsGeoJson(violationsCollection);
                json.put("locations",  violationLocations);
            } else {
                DeviceCollection allCollection = deviceGroupCollectionHelper.buildDeviceCollection(all);
                FeatureCollection allLocations = paoLocationService.getLocationsAsGeoJson(allCollection);
                json.put("locations",  allLocations);
            }
            List<SimpleDevice> violationDevices = violationsCollection.getDeviceList();
            json.put("violationDevices", violationDevices);  
        }
      
        return json;
    }
    
    @GetMapping("/map/locations")
    public @ResponseBody FeatureCollection locations(DeviceCollection deviceCollection) {
        
        FeatureCollection locations = paoLocationService.getLocationsAsGeoJson(deviceCollection.getDeviceList());
        
        return locations;
    }
    
    @GetMapping("/map/filter/state-groups")
    public @ResponseBody List<LiteStateGroup> states(DeviceCollection deviceCollection, BuiltInAttribute attribute) {
        
        List<LiteStateGroup> stateGroups = attributeService.findStateGroups(deviceCollection.getDeviceList(), attribute);
        
        return stateGroups;
    }
    
    @GetMapping("/map/locations/download")
    public void download(DeviceCollection deviceCollection, YukonUserContext userContext, HttpServletResponse response)
            throws IOException {
        List<SimpleDevice> simpleDevices = deviceCollection.getDeviceList();
        List<Integer> paoIds = simpleDevices.stream()
                                            .map(sd -> sd.getPaoIdentifier().getPaoId())
                                            .collect(Collectors.toList());
        List<PaoLocationDetails> paoLocationDetails = paoLocationService.getLocationDetailsForPaos(paoIds);
        String[] headerRow = getHeaderRows(userContext);
        List<String[]> dataRows = getDataRows(paoLocationDetails, userContext);

        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "mapLocations_" + now + ".csv");
    }
    
    private String[] getHeaderRows(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[7];
        headerRow[0] = BulkFieldColumnHeader.NAME.name();
        headerRow[1] = BulkFieldColumnHeader.METER_NUMBER.name();
        headerRow[2] = BulkFieldColumnHeader.LATITUDE.name();
        headerRow[3] = BulkFieldColumnHeader.LONGITUDE.name();
        headerRow[4] = accessor.getMessage(baseKey + "lastChangedDate");
        headerRow[5] = accessor.getMessage(baseKey + "lastChangedTime");
        headerRow[6] = accessor.getMessage(baseKey + "origin");
        return headerRow;
    }
    
    private List<String[]> getDataRows(List<PaoLocationDetails> paoLocationDetails, YukonUserContext userContext) {
        List<String[]> dataRows = Lists.newArrayList();
        for (PaoLocationDetails paoLocation : paoLocationDetails) {
            String[] dataRow = new String[7];
            dataRow[0] = paoLocation.getPaoName();
            dataRow[1] = paoLocation.getMeterNumber();
            dataRow[2] = paoLocation.getLatitude();
            dataRow[3] = paoLocation.getLongitude();
            String dateTime = paoLocation.getLastChangedDate();
            if (dateTime != null) {
                try {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                    LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
                    // add date in yyyy-MM-dd format
                    dataRow[4] = parsedDateTime.toLocalDate().toString();
                    // add time in HH:mm:ss format
                    dataRow[5] = parsedDateTime.toLocalTime().toString().split("\\.")[0];
                } catch (DateTimeParseException e) {
                    log.debug("Unable to parse the date string", e);
                }
            }
            dataRow[6] = paoLocation.getOrigin() != null ? paoLocation.getOrigin().toString() : "";
            dataRows.add(dataRow);
        }
        return dataRows;
    }
    
    @PostMapping("/map/filter")
    public @ResponseBody Map<String, Object> filter(DeviceCollection deviceCollection, @ModelAttribute Filter filter) {
        
        Map<String, Object> json = new HashMap<String, Object>();
        Map<Integer, Boolean> results = new HashMap<>();
        Map<Integer, Group> groups = Maps.uniqueIndex(filter.getGroups(), Group.ID_FUNCTION);
        List<Integer> filteredDeviceIds = new ArrayList<Integer>();
        List<SimpleDevice> filteredDevices = new ArrayList<SimpleDevice>();
        
        Set<SimpleDevice> devices = Sets.newHashSet(deviceCollection.getDeviceList());
        BiMap<LitePoint, PaoIdentifier> points = 
                attributeService.getPoints(devices, filter.getAttribute()).inverse();
        
        Map<Integer, LitePoint> statusPoints = new HashMap<>();
        Map<Integer, LitePoint> nonStatusPoints = new HashMap<>();
        
        for (LitePoint point : points.keySet()) {
            if (point.getPointTypeEnum().isStatus()) {
                statusPoints.put(point.getLiteID(), point);
            } else {
                nonStatusPoints.put(point.getLiteID(), point);
            }
        }
        
        // Determine visibility for status points
        Set<? extends PointValueQualityHolder> statusValues = asyncDynamicDataSource.getPointValues(statusPoints.keySet());
        
        for (PointValueQualityHolder pvqh : statusValues) {
            LitePoint lp = statusPoints.get(pvqh.getId());
            Group group = groups.get(lp.getStateGroupID());
            LiteState state = stateGroupDao.findLiteState(lp.getStateGroupID(), (int) pvqh.getValue());
            if (state.getStateRawState() == group.getState()) {
                results.put(points.get(lp).getPaoId(), true);
                filteredDeviceIds.add(points.get(lp).getPaoId());
            } else {
                results.put(points.get(lp).getPaoId(), false);
            }
        }
        
        // Determine visibility for NON status points
        Map<Integer, Set<Signal>> signals = asyncDynamicDataSource.getSignals(nonStatusPoints.keySet());
        
        for (Integer pointId : signals.keySet()) {
            LitePoint lp = nonStatusPoints.get(pointId);
            Group group = groups.get(lp.getStateGroupID());
            LiteState state = pointService.getCurrentStateForNonStatusPoint(lp, signals.get(pointId));
            if (state.getStateRawState() == group.getState()) {
                results.put(points.get(lp).getPaoId(), true);
                filteredDeviceIds.add(points.get(lp).getPaoId());
            } else {
                results.put(points.get(lp).getPaoId(), false);
            }
        }
        
        // Throw unsupported devices in as hidden
        Set<SimpleDevice> unsupported = Sets.difference(devices, Sets.newHashSet(PaoUtils.asSimpleDeviceList(points.values())));
        for (SimpleDevice device : unsupported) {
            results.put(device.getDeviceId(), false);
        }
        List<SimpleDevice> deviceList = new ArrayList<SimpleDevice>(devices);
        filteredDevices = deviceList.stream().filter(device -> filteredDeviceIds.contains(device.getDeviceId())).collect(Collectors.toList());
        StoredDeviceGroup group = (StoredDeviceGroup) deviceGroupService.resolveGroupName(filter.getTempDeviceGroupName());
        deviceGroupMemberEditorDao.removeAllChildDevices(group);
        deviceGroupMemberEditorDao.addDevices(group, filteredDevices);
        
        json.put("results", results);
        json.put("filteredDeviceCount", filteredDevices.size());
        
        return json;
    }
}
