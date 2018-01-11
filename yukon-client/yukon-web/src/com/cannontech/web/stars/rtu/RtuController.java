package com.cannontech.web.stars.rtu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.editor.CapControlCBC;

@Controller
public class RtuController {
    
    @Autowired private RtuDnpService rtuDnpService;
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        RtuDnp rtu = getRtuDnp(id, model);
        model.addAttribute("rtu", rtu);
        return "/rtu/rtuDetail.jsp";
    }
    
    private RtuDnp getRtuDnp(int id, ModelMap model) {
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        model.addAttribute("dnpConfig", rtu.getDnpConfig());
        model.addAttribute("heartbeatConfig", rtu.getHeartbeatConfig());
        return rtu;
    }
    
    //get points - CbcController line 240
}