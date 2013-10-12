package com.cannontech.web.common.alert;

import java.util.Collection;

import net.sf.jsonOLD.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    public void clear(LiteYukonUser user, String jsonString) {
        final JSONArray array = new JSONArray(jsonString);
        
        final int[] alertIds = new int[array.length()];
        for (int x = 0; x < array.length(); x++) {
            alertIds[x] = array.getInt(x);
        }
        
        alertService.remove(alertIds, user);
    }
    
}