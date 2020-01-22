package com.cannontech.web.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;
import com.cannontech.web.stars.service.RfnWiFiCommDataService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display Wi-Fi Comm Status and RSSI
 */
@Controller
@RequestMapping("/wifiConnectionWidget/*")
public class WifiConnectionWidget extends AdvancedWidgetControllerBase {

    @Autowired private RfnWiFiCommDataService wifiService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    public WifiConnectionWidget() {
    }
    
    @Autowired
    public WifiConnectionWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        addInput(simpleWidgetInput);
        String checkRole = YukonRole.METERING.name();
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId) {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("device", device);
        WiFiMeterCommData data = wifiService.buildWiFiMeterCommDataObject(device);
        model.addAttribute("wifiData", data);

        return "wifiConnectionWidget/render.jsp";
    }

}