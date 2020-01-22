package com.cannontech.web.stars.wifi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.CollectionActionUrl;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;
import com.cannontech.web.stars.service.RfnWiFiCommDataService;
import com.cannontech.web.tools.mapping.MappingColorCollection;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
public class WifiConnectionController {
    
    @Autowired private RfnWiFiCommDataService wifiService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private DeviceMemoryCollectionProducer producer;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;

    private final static String baseKey = "yukon.web.modules.operator.wifiConnection.";

    @GetMapping("/wifiConnection/refresh")
    public void refreshAllConnections(Integer[] deviceIds, HttpServletResponse resp) {
        //TODO: Call code to refresh connection
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @GetMapping("/wifiConnection/connectedDevices/{gatewayId}")
    public String connectedDevices(@PathVariable int gatewayId, ModelMap model) {
        
        //This is needed for breadcrumbs
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(gatewayId);
        model.addAttribute("gateway", gateway);
        
        List<WiFiMeterCommData> wifiData = wifiService.getWiFiMeterCommDataForGateways(Arrays.asList(gatewayId));
        model.addAttribute("wifiData", wifiData);
        String deviceIdList = wifiData.stream()
                .map(d -> String.valueOf(d.getDevice().getPaoIdentifier().getPaoId()))
                .collect(Collectors.joining(","));
        model.addAttribute("deviceIds", deviceIdList);
        
        model.addAttribute("commStatusValues", CommStatusState.values());
        
        return "gateways/wifiConnection.jsp";
    }
    
    @GetMapping("/wifiConnection/connectedDevicesMapping")
    public String connectedDevicesMapping(Integer[] connectedIds, Integer[] disconnectedIds, String[] filteredCommStatus, YukonUserContext userContext, RedirectAttributes attrs) {
        List<MappingColorCollection> colorCollections = new ArrayList<MappingColorCollection>();
        Map<String, List<Integer>> mappingMap = new HashMap<String, List<Integer>>();
        List<String> selectedCommStatuses = Arrays.asList(filteredCommStatus);
        boolean includeConnected = selectedCommStatuses.isEmpty() || selectedCommStatuses.contains("Connected");
        boolean includeDisconnected = selectedCommStatuses.isEmpty() || selectedCommStatuses.contains("Disconnected");
        List<Integer> allDevices = new ArrayList<>();

        if (includeConnected) {
            List<Integer> connectedList = Arrays.asList(connectedIds);
            String connectedColor = "#093";
            DeviceCollection connectedCollection = producer.createDeviceCollection(connectedList);
            MappingColorCollection mapCollection = new MappingColorCollection(connectedCollection, connectedColor, "yukon.web.modules.operator.wifiConnection.commStatusState.CONNECTED");
            colorCollections.add(mapCollection);
            mappingMap.put(connectedColor, connectedList);
            allDevices.addAll(connectedList);
        }
        
        if (includeDisconnected) {
            List<Integer> disconnectedList = Arrays.asList(disconnectedIds);
            String disconnectedColor = "#D14836";
            DeviceCollection disconnectedCollection = producer.createDeviceCollection(disconnectedList);
            MappingColorCollection disconnectedMapCollection = new MappingColorCollection(disconnectedCollection, disconnectedColor, "yukon.web.modules.operator.wifiConnection.commStatusState.DISCONNECTED");
            colorCollections.add(disconnectedMapCollection);
            mappingMap.put(disconnectedColor, disconnectedList);
            allDevices.addAll(disconnectedList);
        }
        
        DeviceCollection allCollection = producer.createDeviceCollection(allDevices);
        List<SimpleDevice> deviceList = allCollection.getDeviceList();
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, deviceList);

        attrs.addFlashAttribute("mappingColors", mappingMap);
        attrs.addFlashAttribute("colorCollections", colorCollections);
        
        return "redirect:" + CollectionActionUrl.MAPPING.getUrl() + "?collectionType=group&group.name=" + tempGroup.getFullName();
    }
    
    @GetMapping("/wifiConnection/connectedDevicesDownload/{gatewayId}")
    public String connectedDevicesDownload(@PathVariable int gatewayId, String[] filteredCommStatus, 
                                           YukonUserContext userContext, HttpServletResponse response) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[5];

        headerRow[0] = accessor.getMessage("yukon.common.name");
        headerRow[1] = accessor.getMessage("yukon.common.attribute.builtInAttribute.COMM_STATUS");
        headerRow[2] = accessor.getMessage(baseKey + "statusLastUpdated");
        headerRow[3] = accessor.getMessage("yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR");
        headerRow[4] = accessor.getMessage(baseKey + "rssiLastUpdated");
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(gatewayId);
        List<WiFiMeterCommData> wifiData = wifiService.getWiFiMeterCommDataForGateways(Arrays.asList(gatewayId));
        
        List<String[]> dataRows = Lists.newArrayList();
        List<String> selectedCommStatuses = Arrays.asList(filteredCommStatus);
        for (WiFiMeterCommData data : wifiData) {
            String[] dataRow = new String[5];
            dataRow[0] = data.getDevice().getName();
            PointValueQualityHolder commStatus = asyncDynamicDataSource.getPointValue(data.getCommStatusPoint().getPointID());
            String commStatusValue = pointFormattingService.getValueString(commStatus, Format.VALUE, userContext);
            if (selectedCommStatuses.isEmpty() || selectedCommStatuses.contains(commStatusValue)) {
                dataRow[1] = pointFormattingService.getValueString(commStatus, Format.VALUE, userContext);
                dataRow[2] = pointFormattingService.getValueString(commStatus, Format.DATE, userContext);
                PointValueQualityHolder rssi = asyncDynamicDataSource.getPointValue(data.getRssiPoint().getPointID());
                dataRow[3] = pointFormattingService.getValueString(rssi, Format.VALUE, userContext);
                dataRow[4] = pointFormattingService.getValueString(rssi, Format.DATE, userContext);
                dataRows.add(dataRow);
            }
        }
        
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, gateway.getName() + "_ConnectedDevices" + "_" + now + ".csv");
        return null;
    }
    
}