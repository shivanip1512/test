package com.cannontech.web.widget.relayInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/relayInformationWidget/*")
public class RelayInformationWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
        
    @Autowired
    public RelayInformationWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setLazyLoad(true);
    }

    @RequestMapping("render")
    public String render(ModelMap model, int deviceId) {
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("relay", device);
        
        return "relayInformationWidget/render.jsp";
    }
}