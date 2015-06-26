package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/tamperFlagMonitorsWidget/*")
@CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
public class TamperFlagMonitorsWidget extends WidgetControllerBase {

    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private TamperFlagMonitorService tamperFlagMonitorService;
	
    @Autowired
    public TamperFlagMonitorsWidget(RoleAndPropertyDescriptionService 
            roleAndPropertyDescriptionService) {
        
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService
                                         .compile("TAMPER_FLAG_PROCESSING"));
    }
    
    @Override
    @RequestMapping("render")
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("tamperFlagMonitorsWidget/render.jsp");
		
		List<TamperFlagMonitor> monitors = tamperFlagMonitorDao.getAll();
		Collections.sort(monitors);
		mav.addObject("monitors", monitors);
		
		return mav;
	}
	
	@RequestMapping("toggleEnabled")
	public ModelAndView toggleEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
		int tamperFlagMonitorId = WidgetParameterHelper.getRequiredIntParameter(request, "tamperFlagMonitorId");
        
		String tamperFlagMonitorsWidgetError = null;
        
        try {
        	tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId);
        } catch (TamperFlagMonitorNotFoundException e) {
        	tamperFlagMonitorsWidgetError = e.getMessage();
        }
        
        ModelAndView mav = render(request, response);
        mav.addObject("tamperFlagMonitorsWidgetError", tamperFlagMonitorsWidgetError);
        
        return mav;
	}
}
