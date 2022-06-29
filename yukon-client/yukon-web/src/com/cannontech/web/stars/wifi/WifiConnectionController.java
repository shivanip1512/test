package com.cannontech.web.stars.wifi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.gateway.model.ConnectedDevicesHelper;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;
import com.cannontech.web.stars.service.RfnWiFiCommDataService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/wifiConnection/*")
public class WifiConnectionController {
    
    @Autowired private RfnWiFiCommDataService wifiService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private PaoDao paoDao;
    @Autowired private ConnectedDevicesHelper connectedDevicesHelper;

    private final static String baseKey = "yukon.web.modules.operator.connectedDevices.";

    @GetMapping("refresh")
    public void refreshAllConnections(Integer[] deviceIds, HttpServletResponse resp, YukonUserContext userContext) {
        List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(Arrays.asList(deviceIds));
        wifiService.refreshWiFiMeterConnection(paoIdentifiers, userContext.getYukonUser());
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @GetMapping("connectedDevices/{gatewayId}")
    public String connectedDevices(@PathVariable int gatewayId, ModelMap model) {
        retrieveWifiData(gatewayId, null, model);
        connectedDevicesHelper.setupConnectedDevicesModel(gatewayId, model);
        return "gateways/wifiConnection.jsp";
    }
    
    @GetMapping("filterConnectedDevices/{gatewayId}")
    public String filterConnectedDevices(@PathVariable int gatewayId, Integer[] commStatuses, ModelMap model) {
        retrieveWifiData(gatewayId, commStatuses, model);
        connectedDevicesHelper.setupConnectedDevicesModel(gatewayId, model);
        return "gateways/wifiConnectionDeviceTable.jsp";
    }
    
    private List<WiFiMeterCommData> retrieveWifiData(int gatewayId, Integer[] commStatuses, ModelMap model) {
        List<Integer> filterCommStatus = new ArrayList<>();
        if (commStatuses != null && commStatuses.length > 0) {
            filterCommStatus = Arrays.asList(commStatuses);
        }
        List<WiFiMeterCommData> wifiData = wifiService.getWiFiMeterCommDataForGateways(Arrays.asList(gatewayId), filterCommStatus);
        model.addAttribute("wifiData", wifiData);
        
        String deviceIdList = wifiData.stream()
                                      .map(d -> String.valueOf(d.getDevice().getPaoIdentifier().getPaoId()))
                                      .collect(Collectors.joining(","));
        model.addAttribute("deviceIds", deviceIdList);
        return wifiData;
    }
    
    @GetMapping("connectedDevicesMapping")
    public String connectedDevicesMapping(Integer[] connectedIds, Integer[] disconnectedIds, Integer[] filteredCommStatus, 
                                          YukonUserContext userContext, RedirectAttributes attrs) {
        return connectedDevicesHelper.getConnectedDevicesMappingData(connectedIds, disconnectedIds, filteredCommStatus, userContext, attrs);
    }
    
    @GetMapping("connectedDevicesDownload/{gatewayId}")
    public String connectedDevicesDownload(@PathVariable int gatewayId, Integer[] filteredCommStatus, 
                                           YukonUserContext userContext, HttpServletResponse response) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[5];

        headerRow[0] = accessor.getMessage("yukon.common.name");
        headerRow[1] = accessor.getMessage("yukon.common.attribute.builtInAttribute.COMM_STATUS");
        headerRow[2] = accessor.getMessage(baseKey + "statusLastUpdated");
        headerRow[3] = accessor.getMessage("yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR");
        headerRow[4] = accessor.getMessage(baseKey + "rssiLastUpdated");
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(gatewayId);
        List<WiFiMeterCommData> wifiData = retrieveWifiData(gatewayId, filteredCommStatus, new ModelMap());
        
        List<String[]> dataRows = Lists.newArrayList();
        for (WiFiMeterCommData data : wifiData) {
            String[] dataRow = new String[5];
            dataRow[0] = data.getDevice().getName();
            PointValueQualityHolder commStatus = asyncDynamicDataSource.getPointValue(data.getCommStatusPoint().getPointID());
            dataRow[1] = pointFormattingService.getValueString(commStatus, Format.VALUE, userContext);
            dataRow[2] = pointFormattingService.getValueString(commStatus, Format.DATE, userContext);
            PointValueQualityHolder rssi = asyncDynamicDataSource.getPointValue(data.getRssiPoint().getPointID());
            dataRow[3] = pointFormattingService.getValueString(rssi, Format.VALUE, userContext);
            dataRow[4] = pointFormattingService.getValueString(rssi, Format.DATE, userContext);
            dataRows.add(dataRow);
        }
        
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, gateway.getName() + "_WiFiConnectedDevices" + "_" + now + ".csv");
        return null;
    }
    
}