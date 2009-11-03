package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;

@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
public class ValidationMonitorsWidget extends WidgetControllerBase {

    private ValidationMonitorDao validationMonitorDao;
    
    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("validationMonitorsWidget/render.jsp");
        
        List<ValidationMonitor> monitors = validationMonitorDao.getAll();
        mav.addObject("monitors", monitors);
        
        return mav;
    }
    
    @Autowired
    public void setValidationMonitorDao(ValidationMonitorDao validationMonitorDao) {
        this.validationMonitorDao = validationMonitorDao;
    }
    
}