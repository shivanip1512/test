package com.cannontech.web.common.alert;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Controller
@RequestMapping("/alert/*")
public class AlertController {
    
    @Autowired private AlertService alertService;
    
    @RequestMapping("view")
    public String view(ModelMap model, LiteYukonUser user) {
        
        final Collection<IdentifiableAlert> alerts = alertService.getAll(user);
        model.addAttribute("alerts", alerts);
        
        int count = alerts.size();
        model.addAttribute("count", count);
        
        return "alert/alertView.jsp";
    }
    
    @RequestMapping(value="clear", method=RequestMethod.POST)
    @ResponseBody
    public void clear(LiteYukonUser user, @RequestBody AlertIdsContainer alertIdsContainer) {
        alertService.remove(alertIdsContainer.alertIds, user);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlertIdsContainer {
        public int[] alertIds;
    }

}