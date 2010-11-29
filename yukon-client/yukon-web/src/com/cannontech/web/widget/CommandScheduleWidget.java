package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.events.loggers.CommandScheduleEventLogService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class CommandScheduleWidget extends WidgetControllerBase {
    
    private CommandScheduleDao commandScheduleDao;
    private CommandScheduleEventLogService commandScheduleEventLogService;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("commandScheduleWidget/render.jsp");

        List<CommandSchedule> schedules = commandScheduleDao.getAll();
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
    
    public ModelAndView enable(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int scheduleId = WidgetParameterHelper.getRequiredIntParameter(request, "scheduleId");
        
        commandScheduleDao.enable(scheduleId);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        commandScheduleEventLogService.scheduleEnabled(userContext.getYukonUser(), scheduleId);
        
        ModelAndView mav = render(request, response);
        return mav;
    }

    @Autowired
    public void setCommandScheduleDao(CommandScheduleDao commandScheduleDao) {
        this.commandScheduleDao = commandScheduleDao;
    }
    
    @Autowired
    public void setCommandScheduleEventLogService(CommandScheduleEventLogService commandScheduleEventLogService) {
        this.commandScheduleEventLogService = commandScheduleEventLogService;
    }

}