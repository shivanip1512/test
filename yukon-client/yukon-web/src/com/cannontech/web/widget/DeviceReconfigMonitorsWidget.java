package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.deviceReconfig.dao.DeviceReconfigMonitorDao;
import com.cannontech.common.deviceReconfig.model.DeviceReconfigMonitor;
import com.cannontech.common.events.loggers.DeviceReconfigEventLogService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.DEVICE_RECONFIG)
public class DeviceReconfigMonitorsWidget extends WidgetControllerBase {
    
    private DeviceReconfigMonitorDao deviceReconfigMonitorDao;
    private DeviceReconfigEventLogService deviceReconfigEventLogService;

    @Override
    public ModelAndView render(HttpServletRequest request,HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("deviceReconfigMonitorsWidget/render.jsp");

        List<DeviceReconfigMonitor> monitors = deviceReconfigMonitorDao.getAll();
        mav.addObject("monitors", monitors);

        return mav;
    }

    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int monitorId = WidgetParameterHelper.getRequiredIntParameter(request, "monitorId");
        
        String name = deviceReconfigMonitorDao.getById(monitorId).getName();
        deviceReconfigMonitorDao.delete(monitorId);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        deviceReconfigEventLogService.taskDeleted(userContext.getYukonUser(), name);
        
        ModelAndView mav = render(request, response);
        return mav;
    }

    @Autowired
    public void setDeviceReconfigMonitorDao(DeviceReconfigMonitorDao deviceReconfigMonitorDao) {
        this.deviceReconfigMonitorDao = deviceReconfigMonitorDao;
    }
    
    @Autowired
    public void setDeviceReconfigEventLogService(DeviceReconfigEventLogService deviceReconfigEventLogService) {
        this.deviceReconfigEventLogService = deviceReconfigEventLogService;
    }
}