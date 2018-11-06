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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.model.DeviceGroup;
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
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceMetadataService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
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
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
    @Autowired private RfnDeviceMetadataService metadataService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
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
    @RequestMapping("/map/dynamic")
    public String dynamic(ModelMap model, DeviceCollection deviceCollection, Integer monitorId, Boolean violationsOnly) {
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("description", deviceCollection.getDescription());
        model.addAttribute("dynamic", true);
        model.addAttribute("monitorId", monitorId);
        model.addAttribute("violationsOnly", violationsOnly);
        if (monitorId != null) {
            DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
            DeviceGroup all = monitor.getGroup();
            DeviceCollection allCollection = deviceGroupCollectionHelper.buildDeviceCollection(all);
            model.addAttribute("deviceCollection", allCollection);
            DeviceGroup violations = monitor.getViolationGroup();
            DeviceCollection violationsCollection = deviceGroupCollectionHelper.buildDeviceCollection(violations);
            model.addAttribute("violationsCollection", violationsCollection);
        }
        
        return "map/map.jsp";
    }

    @RequestMapping(value="/map")
    public String map(ModelMap model, DeviceCollection deviceCollection, YukonUserContext userContext) {
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("description", deviceCollection.getDescription());
        
        Map<AttributeGroup, Set<BuiltInAttribute>> groups = BuiltInAttribute.getAllGroupedStatusTypeAttributes();
        model.addAttribute("attributes", objectFormattingService.sortDisplayableValues(groups, userContext));
        
        Filter filter = new Filter();
        model.addAttribute("filter", filter);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/device/{id}/info")
    public String info(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
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
                } catch (NmCommunicationException e) {
                    log.error("Failed to get gateway data for " + id, e);
                    model.addAttribute("errorMsg", e.getMessage());
                }
            } else {
                try {
                    Map<RfnMetadata, Object> metadata = metadataService.getMetadata(rfnDevice);
                    model.addAttribute("macAddress", metadataService.getMetaDataValueAsString(RfnMetadata.NODE_ADDRESS, metadata));
                    String primaryGatewayName = metadataService.getMetaDataValueAsString(RfnMetadata.PRIMARY_GATEWAY, metadata);
                    model.addAttribute("primaryGatewayName", primaryGatewayName);
                    String nodeSN = metadataService.getMetaDataValueAsString(RfnMetadata.NODE_SERIAL_NUMBER, metadata);
                    model.addAttribute("nodeSN", nodeSN);
                    //primary gateway from NM contains the IP Address too
                    List<LiteYukonPAObject> foundGateways;
                    foundGateways = paoDao.getLiteYukonPaoByName(primaryGatewayName, false);
                    if (foundGateways.size() > 0) {
                        model.addAttribute("primaryGateway", foundGateways.get(0));
                    } else {
                        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
                        for (RfnGateway gateway : gateways) {
                            if (gateway.getNameWithIPAddress().equals(primaryGatewayName)) {
                                model.addAttribute("primaryGateway", gateway);
                                break;
                            }
                        }
                    }
                } catch (NmCommunicationException e) {
                    log.error("Failed to get metadata for " + id, e);
                    model.addAttribute("errorMsg", e.getMessage());
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
    
    @RequestMapping(value="/map/device/{id}", method=RequestMethod.DELETE)
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
    
    @RequestMapping("/map/locations/{monitorId}")
    public @ResponseBody Map<String, Object> monitorLocations(@PathVariable int monitorId, Boolean violationsOnly) {
        
        Map<String, Object> json = new HashMap<String, Object>();
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        DeviceGroup violations = monitor.getViolationGroup();
        DeviceCollection violationsCollection = deviceGroupCollectionHelper.buildDeviceCollection(violations);
        if (violationsOnly != null && violationsOnly == true) {
            FeatureCollection violationLocations = paoLocationService.getLocationsAsGeoJson(violationsCollection);
            json.put("locations",  violationLocations);
        } else {
            DeviceGroup all = monitor.getGroup();
            DeviceCollection allCollection = deviceGroupCollectionHelper.buildDeviceCollection(all);
            FeatureCollection allLocations = paoLocationService.getLocationsAsGeoJson(allCollection);
            json.put("locations",  allLocations);
        }
        List<SimpleDevice> violationDevices = violationsCollection.getDeviceList();
        json.put("violationDevices", violationDevices);        
        return json;
    }
    
    @RequestMapping("/map/locations")
    public @ResponseBody FeatureCollection locations(DeviceCollection deviceCollection) {
        
        FeatureCollection locations = paoLocationService.getLocationsAsGeoJson(deviceCollection.getDeviceList());
        
        return locations;
    }
    
    @RequestMapping("/map/filter/state-groups")
    public @ResponseBody List<LiteStateGroup> states(DeviceCollection deviceCollection, BuiltInAttribute attribute) {
        
        List<LiteStateGroup> stateGroups = attributeService.findStateGroups(deviceCollection.getDeviceList(), attribute);
        
        return stateGroups;
    }
    
    @RequestMapping(value = "/map/locations/download", method = RequestMethod.GET)
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
        headerRow[0] = accessor.getMessage("yukon.common.deviceName");
        headerRow[1] = accessor.getMessage(baseKey + "meterNumber");
        headerRow[2] = accessor.getMessage(baseKey + "latitude");
        headerRow[3] = accessor.getMessage(baseKey + "longitude");
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
    
    @RequestMapping("/map/filter")
    public @ResponseBody Map<Integer, Boolean> filter(DeviceCollection deviceCollection, @ModelAttribute Filter filter) {
        
        Map<Integer, Boolean> results = new HashMap<>();
        Map<Integer, Group> groups = Maps.uniqueIndex(filter.getGroups(), Group.ID_FUNCTION);
        
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
            } else {
                results.put(points.get(lp).getPaoId(), false);
            }
        }
        
        // Throw unsupported devices in as hidden
        Set<SimpleDevice> unsupported = Sets.difference(devices, Sets.newHashSet(PaoUtils.asSimpleDeviceList(points.values())));
        for (SimpleDevice device : unsupported) {
            results.put(device.getDeviceId(), false);
        }
        
        return results;
    }
}
