package com.cannontech.web.amr.macsscheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.util.ServletUtil;

public class MACSScheduleController extends MultiActionController {
    private static final DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
    private MACSScheduleService<Schedule> service;
    private YukonUserDao userDao;
    private AuthDao authDao;
    
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final List<Schedule> list = service.getAll();
        final LiteYukonUser user = ServletUtil.getYukonUser(request);

        mav.setViewName("schedulesView.jsp");
        mav.addObject("list", list);
        mav.addObject("editable", isEditable(user, TDCRole.TDC_RIGHTS));
        return mav;
    }
    
    public ModelAndView controlView(HttpServletRequest request, HttpServletResponse reponse) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final TimeZone zone = userDao.getUserTimeZone(user);

        if (!isEditable(user, TDCRole.TDC_RIGHTS)) return view(request, reponse);
        
        final Schedule schedule = service.getById(id);
        final Calendar cal = Calendar.getInstance(zone);
        final Calendar stopCal = (Calendar) cal.clone();
        stopCal.add(Calendar.HOUR_OF_DAY, 4);
        
        final String state = schedule.getCurrentState();
        
        if (state.equalsIgnoreCase("Running") || state.equalsIgnoreCase("Pending")) {
            mav.setViewName("stopView.jsp");
            mav.addObject("schedule", schedule);
            mav.addObject("stopTime", schedule.getNextStopTime());
            mav.addObject("zone", zone.getDisplayName(true, TimeZone.SHORT));
            return mav;
        }
        
        mav.setViewName("controlView.jsp");
        mav.addObject("schedule", schedule);
        mav.addObject("currentTime", cal.getTime());
        mav.addObject("stopTime", stopCal.getTime());
        mav.addObject("zone", zone.getDisplayName(true, TimeZone.SHORT));
        return mav;
    }
    
    public ModelAndView action(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/spring/macsscheduler/scheduledscripts/view");
        
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String time = ServletRequestUtils.getRequiredStringParameter(request, "time");
        final String stopTime = ServletRequestUtils.getRequiredStringParameter(request, "stoptime");
        final String stopDate = ServletRequestUtils.getRequiredStringParameter(request, "stopdate");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final TimeZone zone = userDao.getUserTimeZone(user);
        Date stop = TimeUtil.flexibleDateParser(stopDate + " " + stopTime, zone);
        Date start = null;
        
        if (!isEditable(user, TDCRole.TDC_RIGHTS)) return mav;
        
        if (time.equals("startnow")) {
            start = Calendar.getInstance(zone).getTime();
        }
        
        if (time.equals("starttime")) {
            String startTime = ServletRequestUtils.getStringParameter(request, "starttime");
            String startDate = ServletRequestUtils.getStringParameter(request, "startdate");
            start = TimeUtil.flexibleDateParser(startDate + " " + startTime, zone);
        }
        
        if (time.equals("stopnow")) stop = Calendar.getInstance(zone).getTime();

        if (time.equals("stopnow") || time.equals("stoptime")) start = stop;
            
        if (start != null) {
            Schedule schedule = service.getById(id);
            service.start(schedule, start, stop);
        }
        
        return mav;
    }
    
    private boolean isEditable(LiteYukonUser user, int rolePropertyId) {
        if (authDao.checkRole(user, TDCRole.ROLEID)) {
            String value = authDao.getRolePropertyValue(user, TDCRole.TDC_RIGHTS);
            Integer i = Integer.decode(value);
            boolean result = (i & 0x00001000) != 0x00001000;
            return result;
            
        }
        return false;
    }
    
    public void setScheduleService(final MACSScheduleService<Schedule> service) {
        this.service = service;
    }
    
    public void setAuthDao(final AuthDao authDao) {
        this.authDao = authDao;
    }
    
    public void setYukonUserDao(final YukonUserDao userDao) {
        this.userDao = userDao;
    }
    
}
