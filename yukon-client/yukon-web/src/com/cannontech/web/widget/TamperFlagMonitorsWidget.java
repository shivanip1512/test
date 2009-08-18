package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;

@CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
public class TamperFlagMonitorsWidget extends WidgetControllerBase {

	private TamperFlagMonitorDao tamperFlagMonitorDao;
	private TamperFlagMonitorService tamperFlagMonitorService;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("tamperFlagMonitorsWidget/render.jsp");
		
		List<TamperFlagMonitor> monitors = tamperFlagMonitorDao.getAll();
		mav.addObject("monitors", monitors);
		
		return mav;
	}
	
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorsWidget_deleteTamperFlagMonitorId");
        
        String tamperFlagMonitorsWidgetError = null;
        
        try {
        	tamperFlagMonitorService.deleteTamperFlagMonitor(tamperFlagMonitorId);
        } catch (TamperFlagMonitorNotFoundException e) {
        	tamperFlagMonitorsWidgetError = e.getMessage();
        }
        
        ModelAndView mav = render(request, response);
        mav.addObject("tamperFlagMonitorsWidgetError", tamperFlagMonitorsWidgetError);
        
        return mav;
	}

	
	@Autowired
	public void setTamperFlagMonitorDao(TamperFlagMonitorDao tamperFlagMonitorDao) {
		this.tamperFlagMonitorDao = tamperFlagMonitorDao;
	}
	
	@Autowired
	public void setTamperFlagMonitorService(TamperFlagMonitorService tamperFlagMonitorService) {
		this.tamperFlagMonitorService = tamperFlagMonitorService;
	}
}
