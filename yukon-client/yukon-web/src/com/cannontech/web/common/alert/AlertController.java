package com.cannontech.web.common.alert;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class AlertController extends MultiActionController {
    private AlertService alertService;
    
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final Collection<IdentifiableAlert> alerts = alertService.getAll(user);
        mav.addObject("alerts", alerts);
        
        int count = alerts.size();
        mav.addObject("count", count);
        
        mav.setViewName("alert/alertView.jsp");
        return mav;
    }

    public void clear(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final String jsonString = ServletRequestUtils.getRequiredStringParameter(request, "jsonString");
        final JSONArray array = new JSONArray(jsonString);
        
        final int[] alertIds = new int[array.length()];
        for (int x = 0; x < array.length(); x++) {
            alertIds[x] = array.getInt(x);
        }
        
        alertService.remove(alertIds, user);
    }
    
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
}
