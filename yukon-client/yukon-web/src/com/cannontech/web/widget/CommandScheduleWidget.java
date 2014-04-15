package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.events.loggers.CommandScheduleEventLogService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class CommandScheduleWidget extends WidgetControllerBase {
    
    @Autowired private CommandScheduleDao commandScheduleDao;
    @Autowired private CommandScheduleEventLogService commandScheduleEventLogService;
    @Autowired private EnergyCompanyDao ecDao;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("commandScheduleWidget/render.jsp");

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        List<CommandSchedule> schedules = commandScheduleDao.getAll(energyCompany.getId());
        mav.addObject("schedules", schedules);

        return mav;
    }
    
    public ModelAndView disable(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int scheduleId = WidgetParameterHelper.getRequiredIntParameter(request, "scheduleId");
        
        commandScheduleDao.disable(scheduleId);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        commandScheduleEventLogService.scheduleDisabled(userContext.getYukonUser(), scheduleId);
        
        ModelAndView mav = render(request, response);
        return mav;
    }
    
    public ModelAndView disableAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        commandScheduleDao.disableAll();
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        commandScheduleEventLogService.allSchedulesDisabled(userContext.getYukonUser());
        
        ModelAndView mav = render(request, response);
        return mav;
    }
    
    public ModelAndView enable(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int scheduleId = WidgetParameterHelper.getRequiredIntParameter(request, "scheduleId");
        
        commandScheduleDao.enable(scheduleId);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        commandScheduleEventLogService.scheduleEnabled(userContext.getYukonUser(), scheduleId);
        
        ModelAndView mav = render(request, response);
        return mav;
    }
}