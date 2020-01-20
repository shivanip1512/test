package com.cannontech.web.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display Wi-Fi Comm Status and RSSI
 */
@Controller
@RequestMapping("/wifiConnectionWidget/*")
public class WifiConnectionWidget extends AdvancedWidgetControllerBase {

    @Autowired private AttributeService attributeService;
    @Autowired private MeterDao meterDao;
    
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

        YukonMeter meter = meterDao.getForId(deviceId);
        model.addAttribute("device", meter);
        LitePoint commStatusPoint = attributeService.findPointForAttribute(meter.getPaoIdentifier(), BuiltInAttribute.COMM_STATUS);
        LitePoint rssiPoint = attributeService.findPointForAttribute(meter.getPaoIdentifier(),
                BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);
        model.addAttribute("commStatus", commStatusPoint);
        model.addAttribute("rssi", rssiPoint);

        return "wifiConnectionWidget/render.jsp";
    }

}