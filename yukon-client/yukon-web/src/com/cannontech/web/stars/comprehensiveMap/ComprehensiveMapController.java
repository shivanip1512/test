package com.cannontech.web.stars.comprehensiveMap;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
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
import com.cannontech.common.rfn.message.metadatamulti.EntityType;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
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
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;


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
    
    private static final Logger log = YukonLogManager.getLogger(ComprehensiveMapController.class);
    
    @GetMapping("home")
    public String home(ModelMap model) {
        
        NetworkMapFilter filter = new NetworkMapFilter();
        model.addAttribute("filter", filter);                                                                
                                                                                                              
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());                                                                                                
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        
        model.addAttribute("colorCodeByOptions", ColorCodeBy.values());
        
        return "comprehensiveMap/map.jsp";
    }
    
    @GetMapping("filter")
    public @ResponseBody Map<String, Object> filter(@ModelAttribute("filter") NetworkMapFilter filter, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        NetworkMap map = null;
        try {
            map = nmNetworkService.getNetworkMap(filter);
            //create collection action group
            StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
            for(FeatureCollection feature : map.getMappedDevices().values()) {
                List<YukonPao> devices = feature.getFeatures().stream().map(d -> new SimpleDevice(d.getProperty("paoIdentifier"))).collect(Collectors.toList());
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
        headerRow[10] = accessor.getMessage(baseKey + "linkCost");
        
        DeviceGroup group = deviceGroupService.findGroupName(groupName);
        DeviceCollection collection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        Set<RfnIdentifier> devices = collection.getDeviceList().stream()
                .map(device -> rfnDeviceDao.getDeviceForId(device.getDeviceId()).getRfnIdentifier())
                .collect(Collectors.toSet());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = new HashMap<>();

        try {
            metaData = metadataMultiService.getMetadata(EntityType.NODE, devices, Set.of(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM, 
                                                                                         RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA,
                                                                                         RfnMetadataMulti.NODE_DATA));
        } catch (NmCommunicationException e1) {
            log.warn("caught exception in download", e1);
        } 
        List<String[]> dataRows = Lists.newArrayList();
        for (SimpleDevice device : collection.getDeviceList()) {
            int paoId = device.getPaoIdentifier().getPaoId();
            String[] dataRow = new String[11];
            RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(paoId);
            dataRow[0] = rfnDevice.getName();
            try {
                YukonMeter meter = meterDao.getForId(paoId);
                dataRow[1] = meter.getMeterNumber();
            } catch (NotFoundException e) {
                //not a meter...continue
            }
            dataRow[2] = device.getPaoIdentifier().getPaoType().getPaoTypeName();
            dataRow[3] = rfnDevice.getRfnIdentifier().getSensorSerialNumber();
            PaoLocation location = paoLocationDao.getLocation(paoId);
            dataRow[4] = String.valueOf(location.getLatitude());
            dataRow[5] = String.valueOf(location.getLongitude());
            RfnMetadataMultiQueryResult metadata = metaData.get(rfnDevice.getRfnIdentifier());
            if (metadata != null) {
                if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM)) {
                    NodeComm comm = (NodeComm) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM);
                    RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(comm.getGatewayRfnIdentifier());
                    dataRow[6] = gateway.getName();
                    dataRow[7] = comm.getNodeCommStatus().toString();
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.NODE_DATA)) {
                    NodeData data = (NodeData) metadata.getMetadatas().get(RfnMetadataMulti.NODE_DATA);
                    dataRow[8] = data.getMacAddress();
                    dataRow[9] = data.getNodeSerialNumber();
                }
                if (metadata.isValidResultForMulti(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA)) {
                    NeighborData neighbor = (NeighborData) metadata.getMetadatas().get(RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA);
                    dataRow[10] = neighbor.getNeighborLinkCost().toString();
                }
            }
            dataRows.add(dataRow);
        }

        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "comprehensiveMapDownload_" + now + ".csv");

      }
    
}