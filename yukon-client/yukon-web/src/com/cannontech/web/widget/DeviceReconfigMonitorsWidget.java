package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.DEVICE_RECONFIG)
public class DeviceReconfigMonitorsWidget extends WidgetControllerBase {
    
    @Autowired private InventoryConfigTaskDao inventoryConfigTaskDao;
    @Autowired private InventoryConfigEventLogService inventoryConfigEventLogService;
    @Autowired private EnergyCompanyDao ecService;

    @Override
    public ModelAndView render(HttpServletRequest request,HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("deviceReconfigMonitorsWidget/render.jsp");

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        EnergyCompany energyCompany = ecService.getEnergyCompany(userContext.getYukonUser());
        List<InventoryConfigTask> tasks = inventoryConfigTaskDao.getAll(energyCompany.getId());
        mav.addObject("tasks", tasks);

        return mav;
    }

    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int taskId = WidgetParameterHelper.getRequiredIntParameter(request, "taskId");
        
        String name = inventoryConfigTaskDao.getById(taskId).getTaskName();
        inventoryConfigTaskDao.delete(taskId);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        inventoryConfigEventLogService.taskDeleted(userContext.getYukonUser(), name);
        
        ModelAndView mav = render(request, response);
        return mav;
    }
}