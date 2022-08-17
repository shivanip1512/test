package com.cannontech.web.stars.dr.operator.inventory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.DeviceAndPointValue;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/operator/inventory/zbProblemDevices/*")
@CheckRole(YukonRole.INVENTORY)
public class ZbProblemDevicesController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private PointFormattingService pointFormattingService;
    
    @RequestMapping("view")
    public String view(ModelMap model, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String inWarehouseMsg = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.inWarehouse");
        List<DeviceAndPointValue> devices = inventoryDao.getZigbeeProblemDevices(inWarehouseMsg);
        model.addAttribute("devices", devices);
        List<Integer> inventoryIds = Lists.newArrayList();
        for (DeviceAndPointValue device : devices) {
            inventoryIds.add(device.getLmHardware().getInventoryIdentifier().getInventoryId());
        }
        Map<Integer, Integer> inventoryIdsToAccountIds = 
                customerAccountDao.getAccountIdsByInventoryIds(inventoryIds);
        Map<Integer, String> accountIdsToAccountNumbers = 
                customerAccountDao.getAccountNumbersByAccountIds(inventoryIdsToAccountIds.values());
        model.addAttribute("accountIdsToAccountNumbers", accountIdsToAccountNumbers);
        
        LiteStateGroup states = stateGroupDao.getStateGroup(StateGroupUtils.STATEGROUP_COMMISSIONED_STATE);
        Map<Double, String> stateColorMap = Maps.newHashMap();
        for (LiteState state : states.getStatesList()) {
            String colorString;
            int fgColor = state.getFgColor();
            if (fgColor == Colors.RED_ID) {
                colorString = "#D14836"; // yukon.css .error
            } else {
                colorString = Colors.getColorString(fgColor);
            }
            stateColorMap.put(new Double(state.getStateRawState()), colorString);
        }
        model.addAttribute("stateColorMap", stateColorMap);
        return "operator/inventory/zbProblemDevices.jsp";
    }
    
    @RequestMapping("csv")
    public String csv(HttpServletResponse response, YukonUserContext context) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String inWarehouseMsg = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.inWarehouse");

        String[] headerRow = new String[5];
        headerRow[0] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.serialNumber");
        headerRow[1] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.hardwareType");
        headerRow[2] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.label");
        headerRow[3] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.state");
        headerRow[4] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.timestamp");
        
        List<String[]> dataRows = Lists.newArrayList();
        List<DeviceAndPointValue> devices = inventoryDao.getZigbeeProblemDevices(inWarehouseMsg);
        for(DeviceAndPointValue device : devices) {
            String[] dataRow = new String[5];
            dataRow[0] = device.getLmHardware().getSerialNumber();
            dataRow[1] = accessor.getMessage(device.getLmHardware().getIdentifier().getHardwareType());
            dataRow[2] = device.getLmHardware().getLabel();
            
            PointValueHolder pointValue = device.getPointValue();
            String value = pointFormattingService.getValueString(pointValue, PointFormattingService.Format.VALUE, context);
            dataRow[3] = value;
            String timestamp = pointFormattingService.getValueString(pointValue, PointFormattingService.Format.DATE, context);
            dataRow[4] = timestamp;
            
            dataRows.add(dataRow);
        }
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "ZigBeeProblemDevices.csv");
        
        return null;
    }
}