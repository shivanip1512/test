package com.cannontech.web.tdc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;

@Controller
public class TdcDisplayController {

    @Autowired DeviceDao deviceDao;
    @Autowired YukonPointHelper yph;
    @Autowired PaoLoadingService paoLoadingService;
    
    @RequestMapping(value="/{displayId}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int displayId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        
        int deviceId = 1140;
        
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        List<LiteYukonPoint> liteYukonPoints = yph.getYukonPoints(device);
        
        model.addAttribute("device", device);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("displayName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("points", liteYukonPoints);
        
        return "display.jsp";
    }
    
    @RequestMapping(value="/{displayId}", method=RequestMethod.PUT)
    public String update(ModelMap model, @PathVariable int displayId) {
        model.addAttribute("mode", "update");
        return "display.jsp";
    }
    
    @RequestMapping(value="/{displayId}", method=RequestMethod.DELETE)
    public String delete(ModelMap model, @PathVariable int displayId) {
        model.addAttribute("mode", "delete");
        return "display.jsp";
    }
    
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        return "display.jsp";
    }
    
    @RequestMapping(value="/create", method=RequestMethod.GET)
    public String edit(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        return "display.jsp";
    }
    
    @RequestMapping(value="/{displayId}/edit", method=RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int displayId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        return "display.jsp";
    }
    
}