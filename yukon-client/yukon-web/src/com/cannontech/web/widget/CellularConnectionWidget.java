package com.cannontech.web.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.gateway.model.CellularDeviceCommData;
import com.cannontech.web.stars.service.RfnCellularCommDataService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display Cellular Comm Status, RSSI, RSRP, RSRQ, SINR
 */
@Controller
@RequestMapping("/cellularConnectionWidget/*")
public class CellularConnectionWidget extends AdvancedWidgetControllerBase {

    @Autowired private RfnCellularCommDataService cellService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    @Autowired
    public CellularConnectionWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId) {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("device", device);
        CellularDeviceCommData data = cellService.buildCellularDeviceCommDataObject(device);
        model.addAttribute("cellData", data);
        model.addAttribute("rssiAttribute", BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);
        model.addAttribute("rsrpAttribute", BuiltInAttribute.REFERENCE_SIGNAL_RECEIVED_POWER);
        model.addAttribute("rsrqAttribute", BuiltInAttribute.REFERENCE_SIGNAL_RECEIVED_QUALITY);
        model.addAttribute("sinrAttribute", BuiltInAttribute.SIGNAL_TO_INTERFERENCE_PLUS_NOISE_RATIO);

        return "cellConnectionWidget/render.jsp";
    }

}