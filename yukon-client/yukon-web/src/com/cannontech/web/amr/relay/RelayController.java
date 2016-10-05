package com.cannontech.web.amr.relay;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;

@Controller
@RequestMapping("/relay/*")
public class RelayController {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    
    @RequestMapping("home")
    public String home(HttpServletRequest request, ModelMap model, LiteYukonUser user, int deviceId) {
        
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);        
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());

        return "/relay/relayHome.jsp";
    }

    
}