package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
public class ValidationMonitorsWidget extends WidgetControllerBase {

    private ValidationMonitorDao validationMonitorDao;
    private ValidationMonitorService validationMonitorService;
    
    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("validationMonitorsWidget/render.jsp");
        
        List<ValidationMonitor> monitors = validationMonitorDao.getAll();
        mav.addObject("monitors", monitors);
        
        return mav;
    }
    
    public ModelAndView toggleEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
		int validationMonitorId = WidgetParameterHelper.getRequiredIntParameter(request, "validationMonitorId");
        
		String validationMonitorsWidgetError = null;
        
        try {
        	validationMonitorService.toggleEnabled(validationMonitorId);
        } catch (ValidationMonitorNotFoundException e) {
        	validationMonitorsWidgetError = e.getMessage();
        }
        
        ModelAndView mav = render(request, response);
        mav.addObject("validationMonitorsWidgetError", validationMonitorsWidgetError);
        
        return mav;
	}

    @Autowired
    public void setValidationMonitorDao(ValidationMonitorDao validationMonitorDao) {
        this.validationMonitorDao = validationMonitorDao;
    }
    
    @Autowired
    public void setValidationMonitorService(ValidationMonitorService validationMonitorService) {
		this.validationMonitorService = validationMonitorService;
	}
}