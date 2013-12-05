package com.cannontech.web.common.alert;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.database.data.lite.LiteYukonUser;

@Controller
@RequestMapping("/alert/*")
public class AlertController {
    
    @Autowired private AlertService alertService;
    
    @RequestMapping
    public String view(ModelMap model, LiteYukonUser user, @RequestParam(defaultValue="alertView") String style) {
        
        final Collection<IdentifiableAlert> alerts = alertService.getAll(user);
        model.addAttribute("alerts", alerts);
        
        int count = alerts.size();
        model.addAttribute("count", count);
        
        return "alert/" + style + ".jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public void clear(LiteYukonUser user, @RequestBody int[] alertIds) {
        alertService.remove(alertIds, user);
    }
    
}