package com.cannontech.web.stars.dr.operator.inventory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/operator/inventory/zbProblemDevices/*")
@CheckRole(YukonRole.INVENTORY)
public class ZbProblemDevicesController {
    
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private InventoryDao inventoryDao;
    private StateDao stateDao;
    private PointFormattingService pointFormattingService;
    
    @RequestMapping
    public String view(ModelMap model, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        List<Pair<LiteLmHardware, SimplePointValue>> devices = inventoryDao.getZigbeeProblemDevices(accessor);
        model.addAttribute("devices", devices);
        LiteStateGroup states = stateDao.getLiteStateGroup(StateGroupUtils.STATEGROUP_COMMISSIONED_STATE);
        Map<Double, String> stateColorMap = Maps.newHashMap();
        for (LiteState state : states.getStatesList()) {
            String colorString;
            int fgColor = state.getFgColor();
            if (fgColor == Colors.RED_ID) {
                colorString = "#CC0000"; //YukonGeneralStyles.css .errorMessage
            } else {
                colorString = Colors.getColorString(fgColor);
            }
            stateColorMap.put(new Double(state.getStateRawState()), colorString);
        }
        model.addAttribute("stateColorMap", stateColorMap);
        return "operator/inventory/zbProblemDevices.jsp";
    }
    
    @RequestMapping
    public String csv(HttpServletResponse response, YukonUserContext context) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        String[] headerRow = new String[5];
        headerRow[0] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.serialNumber");
        headerRow[1] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.hardwareType");
        headerRow[2] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.label");
        headerRow[3] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.state");
        headerRow[4] = accessor.getMessage("yukon.web.modules.operator.zbProblemDevices.timestamp");
        
        List<String[]> dataRows = Lists.newArrayList();
        List<Pair<LiteLmHardware, SimplePointValue>> devices = inventoryDao.getZigbeeProblemDevices(accessor);
        for(Pair<LiteLmHardware, SimplePointValue> pair : devices) {
            String[] dataRow = new String[5];
            dataRow[0] = pair.first.getSerialNumber();
            dataRow[1] = accessor.getMessage(pair.first.getIdentifier().getHardwareType());
            dataRow[2] = pair.first.getLabel();
            
            PointValueHolder pointValue = pair.second;
            String value = pointFormattingService.getValueString(pointValue, PointFormattingService.Format.VALUE, context);
            dataRow[3] = value;
            String timestamp = pointFormattingService.getValueString(pointValue, PointFormattingService.Format.DATE, context);
            dataRow[4] = timestamp;
            
            dataRows.add(dataRow);
        }
        
        //set up output for CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = "ZigBeeProblemDevices.csv";
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        
        //write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        csvWriter.writeNext(headerRow);
        for (String[] line : dataRows) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        return "";
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }
    
}