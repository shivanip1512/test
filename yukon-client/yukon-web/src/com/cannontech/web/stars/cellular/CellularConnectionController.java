package com.cannontech.web.stars.cellular;

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
import com.cannontech.common.pao.PaoType;
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
import com.cannontech.web.stars.gateway.model.CellularDeviceCommData;
import com.cannontech.web.stars.gateway.model.ConnectedDevicesHelper;
import com.cannontech.web.stars.service.RfnCellularCommDataService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/cellConnection/*")
public class CellularConnectionController {
    
    @Autowired private RfnCellularCommDataService cellService;
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
        cellService.refreshCellularDeviceConnection(paoIdentifiers, userContext.getYukonUser());
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @GetMapping("connectedDevices/{gatewayId}")
    public String connectedDevices(@PathVariable int gatewayId, ModelMap model) {
        retrieveCellData(gatewayId, null, null, model);

        connectedDevicesHelper.setupConnectedDevicesModel(gatewayId, model);
        model.addAttribute("cellTypes", PaoType.getCellularTypes());
        
        return "gateways/cellConnection.jsp";
    }
    
    private List<CellularDeviceCommData> retrieveCellData(int gatewayId, Integer[] commStatuses, PaoType[] deviceTypes, ModelMap model) {
        List<PaoType> filterTypes = new ArrayList<>(PaoType.getCellularTypes());
        List<Integer> filterCommStatus = new ArrayList<>();
        if (deviceTypes != null && deviceTypes.length > 0) {
            filterTypes = Arrays.asList(deviceTypes);
        }
        if (commStatuses != null && commStatuses.length > 0) {
            filterCommStatus = Arrays.asList(commStatuses);
        }
        List<CellularDeviceCommData> cellData = cellService.getCellularDeviceCommDataForGateways(Arrays.asList(gatewayId), filterCommStatus, filterTypes);
        model.addAttribute("cellData", cellData);
        
        String deviceIdList = cellData.stream()
                                      .map(d -> String.valueOf(d.getDevice().getPaoIdentifier().getPaoId()))
                                      .collect(Collectors.joining(","));
        model.addAttribute("deviceIds", deviceIdList);
        return cellData;
    }
    
    @GetMapping("filterConnectedDevices/{gatewayId}")
    public String filterConnectedDevices(@PathVariable int gatewayId, Integer[] commStatuses, PaoType[] deviceTypes, ModelMap model) {
        retrieveCellData(gatewayId, commStatuses, deviceTypes, model);
        connectedDevicesHelper.setupConnectedDevicesModel(gatewayId, model);
        return "gateways/cellConnectionDeviceTable.jsp";
    }
    
    @GetMapping("connectedDevicesMapping")
    public String connectedDevicesMapping(Integer[] connectedIds, Integer[] disconnectedIds, Integer[] filteredCommStatus, 
                                          YukonUserContext userContext, RedirectAttributes attrs) {
        return connectedDevicesHelper.getConnectedDevicesMappingData(connectedIds, disconnectedIds, filteredCommStatus, userContext, attrs);
    }
    
    @GetMapping("connectedDevicesDownload/{gatewayId}")
    public String connectedDevicesDownload(@PathVariable int gatewayId, Integer[] filteredCommStatus, PaoType[] deviceTypes,
                                           YukonUserContext userContext, HttpServletResponse response) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[8];

        headerRow[0] = accessor.getMessage("yukon.common.name");
        headerRow[1] = accessor.getMessage("yukon.common.deviceType");
        headerRow[2] = accessor.getMessage("yukon.common.attribute.builtInAttribute.COMM_STATUS");
        headerRow[3] = accessor.getMessage(baseKey + "statusLastUpdated");
        headerRow[4] = accessor.getMessage("yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR");
        headerRow[5] = accessor.getMessage("yukon.common.attribute.builtInAttribute.REFERENCE_SIGNAL_RECEIVED_POWER");
        headerRow[6] = accessor.getMessage("yukon.common.attribute.builtInAttribute.REFERENCE_SIGNAL_RECEIVED_QUALITY");
        headerRow[7] = accessor.getMessage("yukon.common.attribute.builtInAttribute.SIGNAL_TO_INTERFERENCE_PLUS_NOISE_RATIO");
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(gatewayId);
        List<CellularDeviceCommData> cellData = retrieveCellData(gatewayId, filteredCommStatus, deviceTypes, new ModelMap());
        
        List<String[]> dataRows = Lists.newArrayList();
        for (CellularDeviceCommData data : cellData) {
            String[] dataRow = new String[8];
            dataRow[0] = data.getDevice().getName();
            dataRow[1] = data.getDevice().getPaoIdentifier().getPaoType().getPaoTypeName();
            PointValueQualityHolder commStatus = asyncDynamicDataSource.getPointValue(data.getCommStatusPoint().getPointID());
            dataRow[2] = pointFormattingService.getValueString(commStatus, Format.VALUE, userContext);
            dataRow[3] = pointFormattingService.getValueString(commStatus, Format.DATE, userContext);
            PointValueQualityHolder rssi = asyncDynamicDataSource.getPointValue(data.getRssiPoint().getPointID());
            dataRow[4] = pointFormattingService.getValueString(rssi, Format.VALUE, userContext);
            PointValueQualityHolder rsrp = asyncDynamicDataSource.getPointValue(data.getRsrpPoint().getPointID());
            dataRow[5] = pointFormattingService.getValueString(rsrp, Format.VALUE, userContext);
            PointValueQualityHolder rsrq = asyncDynamicDataSource.getPointValue(data.getRsrqPoint().getPointID());
            dataRow[6] = pointFormattingService.getValueString(rsrq, Format.VALUE, userContext);
            PointValueQualityHolder sinr = asyncDynamicDataSource.getPointValue(data.getSinrPoint().getPointID());
            dataRow[7] = pointFormattingService.getValueString(sinr, Format.VALUE, userContext);
            dataRows.add(dataRow);
        }
        
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, gateway.getName() + "_CellularConnectedDevices" + "_" + now + ".csv");
        return null;
    }
    
}