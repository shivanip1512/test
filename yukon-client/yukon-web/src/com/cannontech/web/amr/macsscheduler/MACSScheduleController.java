package com.cannontech.web.amr.macsscheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;
import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;

@CheckRole(YukonRole.SCHEDULER)
public class MACSScheduleController extends MultiActionController {
    private static final Comparator<Schedule> sortByName;
    private static final Comparator<Schedule> sortByCategory;
    private static final Comparator<Schedule> sortByState;
    private static final Comparator<Schedule> sortByStartDate;
    private static final Comparator<Schedule> sortByStopDate;
    private static final Comparator<Schedule> reverseSortByName;
    private static final Comparator<Schedule> reverseSortByCategory;
    private static final Comparator<Schedule> reverseSortByState;
    private static final Comparator<Schedule> reverseSortByStartDate;
    private static final Comparator<Schedule> reverseSortByStopDate;
    private MACSScheduleService<Schedule> service;
    private DateFormattingService dateFormattingService;
    private RolePropertyDao rolePropertyDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ToolsEventLogService toolsEventLogService;
    
    static {
        sortByName = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                return o1.getScheduleName().compareTo(o2.getScheduleName());
            }
        };
        
        sortByCategory = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                return o1.getCategoryName().compareTo(o2.getCategoryName());
            }
        };
        
        sortByState = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                int result = o1.getCurrentState().compareTo(o2.getCurrentState());
                if (result == 0) return sortByName.compare(o1, o2);
                return result;
            }
        };
        
        sortByStartDate = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                int result = o1.getNextRunTime().compareTo(o2.getNextRunTime());
                if (result == 0) return sortByName.compare(o1, o2);
                return result;
            }
        };
        
        sortByStopDate = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                int result = o1.getNextStopTime().compareTo(o2.getNextStopTime());
                if (result == 0) return sortByName.compare(o1, o2);
                return result;
            }
        };
        
        reverseSortByName = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                return o2.getScheduleName().compareTo(o1.getScheduleName());
            }
        };
        
        reverseSortByCategory = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                return o2.getCategoryName().compareTo(o1.getCategoryName());
            }
        };
        
        reverseSortByState = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                int result = o2.getCurrentState().compareTo(o1.getCurrentState());
                if (result == 0) return sortByName.compare(o1, o2);
                return result;
            }
        };
        
        reverseSortByStartDate = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                int result = o2.getNextRunTime().compareTo(o1.getNextRunTime());
                if (result == 0) return sortByName.compare(o1, o2);
                return result;
            }
        };
        
        reverseSortByStopDate = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                int result = o2.getNextStopTime().compareTo(o1.getNextStopTime());
                if (result == 0) return sortByName.compare(o1, o2);
                return result;
            }
        };
    }
    
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        mav.setViewName("scheduledscripts.jsp");
        String sortBy = ServletRequestUtils.getStringParameter(request, "sortBy");
        Boolean descending = ServletRequestUtils.getBooleanParameter(request, "descending");
        
        if (sortBy == null) sortBy = "Schedule Name";
        if (descending == null) descending = false;
        
        mav.addObject("sortBy", sortBy);
        mav.addObject("descending", descending);
        return mav;
    }
   
   
    
    public ModelAndView innerView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        List<Schedule> filteredSchedules = new ArrayList<>();
        List<Schedule> allSchedules = service.getAll();
        
        if (rolePropertyDao.checkRole(YukonRole.DEMAND_RESPONSE, user)) {
            filteredSchedules = paoAuthorizationService.filterAuthorized(user, allSchedules, Permission.LM_VISIBLE);
        } else {
            // Without the DEMAND_RESPONSE, we want them to be able to see all scripts.
            filteredSchedules = allSchedules;
        }

        String sortBy = ServletRequestUtils.getStringParameter(request, "sortBy");
        if (StringUtils.isEmpty(sortBy)) {
            sortBy = "Schedule Name";
        }
        boolean descending = ServletRequestUtils.getBooleanParameter(request, "descending", false);

        sort(filteredSchedules, sortBy, descending);

        List<MACSScheduleInfo> infoList = createScheduleInfoList(filteredSchedules, isEditable(user));

        mav.setViewName("schedulesView.jsp");
        mav.addObject("list", infoList);
        mav.addObject("sortBy", sortBy);
        mav.addObject("descending", descending);
        return mav;
    }
    
    public ModelAndView controlView(HttpServletRequest request, HttpServletResponse reponse) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        final ModelAndView mav = new ModelAndView();
        final String sortBy = ServletRequestUtils.getRequiredStringParameter(request, "sortBy");
        final Boolean descending = ServletRequestUtils.getRequiredBooleanParameter(request, "descending");
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        
        if (!isEditable(userContext.getYukonUser())) return view(request, reponse);
        
        final Schedule schedule = service.getById(id);
        final Calendar cal = dateFormattingService.getCalendar(userContext);
        final Calendar stopCal = (Calendar) cal.clone();
        stopCal.add(Calendar.HOUR_OF_DAY, 4);
        
        final String state = schedule.getCurrentState();
        
        mav.addObject("schedule", schedule);
        mav.addObject("sortBy", sortBy);
        mav.addObject("descending", descending);
        mav.addObject("zone", userContext.getTimeZone().getDisplayName(true, TimeZone.SHORT));
        
        if (state.equalsIgnoreCase("Running") || state.equalsIgnoreCase("Pending")) {
            mav.setViewName("stopView.jsp");
            mav.addObject("stopTime", schedule.getNextStopTime());
            return mav;
        }
        
        mav.setViewName("controlView.jsp");
        mav.addObject("schedule", schedule);
        mav.addObject("currentTime", cal.getTime());
        mav.addObject("stopTime", stopCal.getTime());
        mav.addObject("errorMsg", errorMsg);
        return mav;
    }
    
    public ModelAndView action(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final ModelAndView mav = new ModelAndView();
        final String sortBy = ServletRequestUtils.getRequiredStringParameter(request, "sortBy");
        final Boolean descending = ServletRequestUtils.getRequiredBooleanParameter(request, "descending");
        final Boolean isStart = ServletRequestUtils.getRequiredBooleanParameter(request, "isStart");
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String stopTime = ServletRequestUtils.getRequiredStringParameter(request, "stop");
        final LiteYukonUser liteYukonUser = yukonUserContext.getYukonUser();
        final TimeZone timeZone = yukonUserContext.getTimeZone();
        final Schedule schedule = service.getById(id);

        mav.setView(createRedirectView(sortBy, descending));

        Date stop = dateFormattingService.flexibleDateParser(stopTime, yukonUserContext);

        if (!isEditable(liteYukonUser)) return mav;

        if (isStart) {
            final boolean startNow = ServletRequestUtils.getBooleanParameter(request, "startNow", false);
            Date start = null;
            if (startNow) {
                start = Calendar.getInstance(timeZone).getTime();
            } else {
                String startTime = ServletRequestUtils.getStringParameter(request, "start");
                start = dateFormattingService.flexibleDateParser(startTime, yukonUserContext);
            }
            if (start.compareTo(stop) > 0) {
                mav.setView(createResetView(sortBy, descending, id, "Start date must be before stop date."));
                return mav;
            }
            service.start(schedule, start, stop);
            toolsEventLogService.macsScriptStarted(liteYukonUser, schedule.getScheduleName(), start, stop);
        } else {
            final boolean stopNow = ServletRequestUtils.getBooleanParameter(request, "stopNow", false);
            if (stopNow) {
                stop = Calendar.getInstance(timeZone).getTime();
            }
            service.stop(schedule, stop);
            toolsEventLogService.macsScriptStopped(liteYukonUser, schedule.getScheduleName(), stop);
        }

        return mav;
    }

    public ModelAndView toggleState(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final String sortBy = ServletRequestUtils.getRequiredStringParameter(request, "sortBy");
        final Boolean descending = ServletRequestUtils.getRequiredBooleanParameter(request, "descending");
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        if (!isEditable(user)) return mav;
        
        Schedule schedule = service.getById(id);
        String currentState = schedule.getCurrentState();
        
        if (currentState.equalsIgnoreCase("Disabled")) {
            service.enable(schedule);
        } else {
            service.disable(schedule);
        }
        toolsEventLogService.macsScriptEnabled(user, schedule.getScheduleName(), currentState);

        mav.setView(createRedirectView(sortBy, descending));
        return mav;
    }
    
    private boolean isEditable(LiteYukonUser user) {
    	
    	return rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS, user);
    }
    
    private void sort(final List<Schedule> list, final String sortBy, final Boolean descending) {
        Comparator<Schedule> c = null;
        String cleanSortBy = sortBy.trim();
        
        if (cleanSortBy.equalsIgnoreCase("Schedule Name")) { 
            c = (descending) ? reverseSortByName : sortByName;
        }
        if (cleanSortBy.equalsIgnoreCase("Category Name")) { 
            c = (descending) ? reverseSortByCategory : sortByCategory;
        }
        if (cleanSortBy.equalsIgnoreCase("Current State")) {
            c = (descending) ? reverseSortByState : sortByState;
        }
        if (cleanSortBy.equalsIgnoreCase("Start Date/Time")) {
            c = (descending) ? reverseSortByStartDate : sortByStartDate;
        }
        if (cleanSortBy.equalsIgnoreCase("Stop Date/Time")) {
            c = (descending) ? reverseSortByStopDate : sortByStopDate;
        }

        Collections.sort(list, c);
    }
    
    private RedirectView createRedirectView(final String sortBy, final Boolean descending) {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("sortBy", sortBy);
        model.put("descending", descending);
        
        RedirectView redirect = new RedirectView("/macsscheduler/schedules/view", true);
        redirect.setAttributesMap(model);
        return redirect;
    }
    
    private RedirectView createResetView(final String sortBy, final Boolean descending, int id, String errorMsg) {
    	
    	Map<String,Object> model = new HashMap<String,Object>();
    	model.put("id", id);
    	model.put("sortBy", sortBy);
        model.put("descending", descending);
        model.put("errorMsg", errorMsg);
        
        RedirectView redirect = new RedirectView("/macsscheduler/schedules/controlView", true);
        redirect.setAttributesMap(model);
        return redirect;
    }
    
    private List<MACSScheduleInfo> createScheduleInfoList(final List<Schedule> scheduleList, final boolean editable) {
        List<MACSScheduleInfo> infoList = new ArrayList<MACSScheduleInfo>(scheduleList.size());
        for (final Schedule schedule : scheduleList) {
            infoList.add(new MACSScheduleInfo(schedule, editable));
        }
        return infoList;
    }
    
    public void setScheduleService(final MACSScheduleService<Schedule> service) {
        this.service = service;
    }
    
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
}
