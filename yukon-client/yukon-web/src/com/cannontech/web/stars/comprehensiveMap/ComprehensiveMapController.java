package com.cannontech.web.stars.comprehensiveMap;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
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
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.mapping.model.NetworkMap;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.ColorCodeBy;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.LinkQuality;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@RequestMapping("/comprehensiveMap/*")
@Controller
@CheckRoleProperty({YukonRoleProperty.INFRASTRUCTURE_ADMIN, 
                    YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, 
                    YukonRoleProperty.INFRASTRUCTURE_DELETE, 
                    YukonRoleProperty.INFRASTRUCTURE_VIEW})
public class ComprehensiveMapController {
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private NmNetworkService nmNetworkService;
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
        
        return "comprehensiveMap/map.jsp";
    }
    
    @GetMapping("filter")
    public @ResponseBody Map<String, Object> filter(@ModelAttribute("filter") NetworkMapFilter filter, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        NetworkMap map = null;
        try {
            map = nmNetworkService.getNetworkMap(filter, accessor);
            log.debug("Devices in map="+ map.getTotalDevices());
            //create collection action group
            StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
            for(FeatureCollection feature : map.getMappedDevices().values()) {
                List<YukonPao> devices = feature.getFeatures().stream()
                        .map(d -> new SimpleDevice(d.getProperty("paoIdentifier"))).collect(Collectors.toList());
                deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
            }
            json.put("collectionActionRedirect", CollectionActionUrl.COLLECTION_ACTIONS.getUrl() + "?collectionType=group&group.name=" + tempGroup.getFullName());
            json.put("collectionGroup", tempGroup.getFullName());
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
        //search for a Sensor Serial Number with the provided text
        Integer sensor = rfnDeviceDao.findDeviceBySensorSerialNumber(searchText);
        if (sensor != null) {
            foundPaoIds.add(sensor);
        }
        //search for a Meter Number with the provided text
        try {
            YukonMeter meter = meterDao.getForMeterNumber(searchText);
            if (meter != null) {
                foundPaoIds.add(meter.getDeviceId());
            }
        } catch (NotFoundException e) {
            //continue looking for matches
        }
        //search by partial device name
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
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        //device name, meter number, device type, sensor s/n, lat, long, primary gateway, comm status, mac address, node s/n, link cost
        //TODO: eventually add hop count and total link cost??
        String[] headerRow = new String[11];
        
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
        
        DeviceGroup group = deviceGroupService.findGroupName(groupName);
        DeviceCollection collection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        
        log.debug("Devices in a group {}", collection.getDeviceCount());
        
        Set<RfnIdentifier> rfnIdentifiers = collection.getDeviceList().stream()
                .map(device -> rfnDeviceDao.getDeviceForId(device.getDeviceId()).getRfnIdentifier())
                .collect(Collectors.toSet());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = new HashMap<>();
        log.debug("Getting data for download for {} devices", rfnIdentifiers.size());
        try {
            metaData = metadataMultiService.getMetadataForDeviceRfnIdentifiers(rfnIdentifiers, Set.of(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM, 
                                                                                         RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA,
                                                                                         RfnMetadataMulti.NODE_DATA));
        } catch (NmCommunicationException e1) {
            log.warn("caught exception in download", e1);
        } 

        Map<String, String> gatewayNames = rfnGatewayService.getAllGateways()
                .stream()
                .collect(Collectors.toMap(g -> g.getRfnIdentifier().getCombinedIdentifier(), g-> g.getName()));
        
        Map<Integer, PaoLocation> locations = paoLocationDao.getLocations(collection.getDeviceList()).stream()
                .collect(Collectors.toMap(l -> l.getPaoIdentifier().getPaoId(), l->l));
        
        List<String[]> dataRows = Lists.newArrayList();
        log.debug("Got data from NM for {} devices", metaData.keySet().size());
        for (RfnIdentifier device : metaData.keySet()) {
            int paoId = rfnDeviceDao.getDeviceIdsForRfnIdentifiers(Sets.newHashSet(device)).iterator().next();
            String[] dataRow = new String[11];
            LiteYukonPAObject rfnDevice = cache.getAllPaosMap().get(paoId);
            dataRow[0] = rfnDevice.getPaoName();
            SimpleMeter meter = cache.getAllMeters().get(paoId);
            if(meter != null) {
                dataRow[1] = meter.getMeterNumber();
            }
            dataRow[2] = rfnDevice.getPaoType().getPaoTypeName();
            dataRow[3] = device.getSensorSerialNumber();
            PaoLocation location = locations.get(paoId);
            if(location != null) {
                dataRow[4] = String.valueOf(location.getLatitude());
                dataRow[5] = String.valueOf(location.getLongitude());
            }
            RfnMetadataMultiQueryResult metadata = metaData.get(device);
            if (metadata != null) {
                if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM)) {
                    NodeComm comm = (NodeComm) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM);
                    dataRow[6] = gatewayNames.get(comm.getGatewayRfnIdentifier().getCombinedIdentifier());
                    dataRow[7] = accessor.getMessage(baseKey + "status." + comm.getNodeCommStatus());
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
                    NodeData data = (NodeData) metadata.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
                    dataRow[8] = data.getMacAddress();
                    dataRow[9] = data.getNodeSerialNumber();
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA)) {
                    NeighborData neighbor = (NeighborData) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA);
                    LinkQuality linkQuality = LinkQuality.getLinkQuality(neighbor);
                    String linkQualityFormatted = accessor.getMessage(linkQuality.getFormatKey());
                    dataRow[10] = linkQualityFormatted;
                }
            }
            dataRows.add(dataRow);
        }
        
        log.debug("Generated {} rows for CSV file", dataRows.size());

        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "ComprehensiveMapDownload_" + now + ".csv");

      }
    
    @GetMapping("allPrimaryRoutes")
    public @ResponseBody List<FeatureCollection> primaryRoutes(String groupName) {
        DeviceGroup group = deviceGroupService.findGroupName(groupName);
        DeviceCollection collection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        
        List<List<SimpleDevice>> chunks = Lists.partition(collection.getDeviceList(), 10);
        List<FeatureCollection> features = chunks.stream().map(chunk -> paoLocationService.getLocationsAsGeoJson(chunk)).collect(Collectors.toList());
        return features;
    }
    
}