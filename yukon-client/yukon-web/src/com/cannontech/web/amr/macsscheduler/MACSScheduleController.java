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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.SCHEDULER)
@Controller
@RequestMapping("/schedules/*")
public class MACSScheduleController extends MultiActionController {
    @Autowired private MACSScheduleService<Schedule> service;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("view")
    public String view(ModelMap model, @DefaultSort(dir=Direction.asc, sort="scheduleName") SortingParameters sorting, 
                       @DefaultItemsPerPage(10) PagingParameters paging) throws Exception {
        return "scheduledscripts.jsp";
    }
   
    @RequestMapping("innerView")
    public String innerView(ModelMap model, LiteYukonUser user, @DefaultSort(dir=Direction.asc, sort="scheduleName") SortingParameters sorting, 
                            @DefaultItemsPerPage(10) PagingParameters paging, YukonUserContext userContext) throws Exception {
        List<Schedule> filteredSchedules = new ArrayList<>();
        List<Schedule> allSchedules = service.getAll();
        
        if (rolePropertyDao.checkRole(YukonRole.DEMAND_RESPONSE, user)) {
            filteredSchedules = paoAuthorizationService.filterAuthorized(user, allSchedules, Permission.LM_VISIBLE);
        } else {
            // Without the DEMAND_RESPONSE, we want them to be able to see all scripts.
            filteredSchedules = allSchedules;
        }
        
        List<MACSScheduleInfo> infoList = createScheduleInfoList(filteredSchedules, isEditable(user));
        
        SearchResults<MACSScheduleInfo> searchResult = new SearchResults<MACSScheduleInfo>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, infoList.size());
        
        ScriptsSortBy sortBy = ScriptsSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<MACSScheduleInfo>itemList = Lists.newArrayList(infoList);
        Comparator<MACSScheduleInfo> comparator = (o1, o2) -> o1.getSchedule().getScheduleName().compareTo(o2.getSchedule().getScheduleName());
        if (sortBy == ScriptsSortBy.categoryName) {
            comparator = (o1, o2) -> o1.getSchedule().getCategoryName().compareTo(o2.getSchedule().getCategoryName());
        } else if (sortBy == ScriptsSortBy.currentState) {
            comparator = (o1, o2) -> o1.getSchedule().getCurrentState().compareTo(o2.getSchedule().getCurrentState());
        } else if (sortBy == ScriptsSortBy.stopDateTime) {
            comparator = (o1, o2) -> o1.getSchedule().getNextStopTime().compareTo(o2.getSchedule().getNextStopTime());
        } else if (sortBy == ScriptsSortBy.startDateTime) {
            comparator = (o1, o2) -> o1.getSchedule().getNextRunTime().compareTo(o2.getSchedule().getNextRunTime());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (ScriptsSortBy column : ScriptsSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        
        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, infoList.size());
        searchResult.setResultList(itemList);        
        
        model.addAttribute("list", searchResult);

        return "schedulesView.jsp";
    }
    
    @RequestMapping("{id}/view")
    public String viewSchedule(ModelMap model, @PathVariable int id) {
        //TODO: Get Script text and add to model
        model.addAttribute("script", "Test text");
        return "script.jsp";
    }
    
    @RequestMapping("{id}/startStop")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public String startStop(ModelMap model, YukonUserContext userContext, @PathVariable int id) throws Exception {        
        final Schedule schedule = service.getById(id);
        final Calendar cal = dateFormattingService.getCalendar(userContext);
        final Calendar stopCal = (Calendar) cal.clone();
        stopCal.add(Calendar.HOUR_OF_DAY, 4);
        
        final String state = schedule.getCurrentState();
        
        model.addAttribute("schedule", schedule);
        model.addAttribute("zone", userContext.getTimeZone().getDisplayName(true, TimeZone.SHORT));
        
        if (state.equalsIgnoreCase("Running") || state.equalsIgnoreCase("Pending")) {
            model.addAttribute("stopTime", schedule.getNextStopTime());
            return "stopDialog.jsp";
        }
        
        model.addAttribute("schedule", schedule);
        model.addAttribute("currentTime", cal.getTime());
        model.addAttribute("stopTime", stopCal.getTime());
        return "startDialog.jsp";
    }
    
    @RequestMapping("{id}/start")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public @ResponseBody Map<String, Object> start(@PathVariable int id, YukonUserContext yukonUserContext, 
                      @RequestParam(value="startNow", required=false, defaultValue="false") Boolean startNow, 
                      @RequestParam("start") String startTime, @RequestParam("stop") String stopTime) throws Exception {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        final LiteYukonUser liteYukonUser = yukonUserContext.getYukonUser();
        final TimeZone timeZone = yukonUserContext.getTimeZone();
        final Schedule schedule = service.getById(id);
        Map<String, Object> json = new HashMap<>();

        Date stop = dateFormattingService.flexibleDateParser(stopTime, yukonUserContext);
        Date start = null;
        if (startNow) {
            start = Calendar.getInstance(timeZone).getTime();
        } else {
            start = dateFormattingService.flexibleDateParser(startTime, yukonUserContext);
        }
        if (start.compareTo(stop) > 0) {
            json.put("errorMsg", messageSourceAccessor.getMessage("yukon.web.modules.tools.scripts.start.error.startDateBeforeEndDate"));
        } else {
            service.start(schedule, start, stop);
            toolsEventLogService.macsScriptStarted(liteYukonUser, schedule.getScheduleName(), start, stop);
        }

        return json;
    }
    
    @RequestMapping("{id}/stop")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public @ResponseBody Map<String, Object> stop(@PathVariable int id, YukonUserContext yukonUserContext, 
                     @RequestParam(value="stopNow", required=false, defaultValue="false") boolean stopNow, 
                     @RequestParam("stop") String stopTime) throws Exception {
        final LiteYukonUser liteYukonUser = yukonUserContext.getYukonUser();
        final TimeZone timeZone = yukonUserContext.getTimeZone();
        final Schedule schedule = service.getById(id);
        Map<String, Object> json = new HashMap<>();

        Date stop = dateFormattingService.flexibleDateParser(stopTime, yukonUserContext);
        if (stopNow) {
            stop = Calendar.getInstance(timeZone).getTime();
        }
        service.stop(schedule, stop);
        toolsEventLogService.macsScriptStopped(liteYukonUser, schedule.getScheduleName(), stop);
        return json;
    }

    @RequestMapping("{id}/toggleState")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public @ResponseBody Map<String, Object> toggleState(ModelMap model, @PathVariable int id, LiteYukonUser user) throws Exception {      
        Map<String, Object> json = new HashMap<>();
        Schedule schedule = service.getById(id);
        String currentState = schedule.getCurrentState();
        
        if (currentState.equalsIgnoreCase("Disabled")) {
            service.enable(schedule);
        } else {
            service.disable(schedule);
        }
        toolsEventLogService.macsScriptEnabled(user, schedule.getScheduleName(), currentState);
        return json;
    }
    
    private boolean isEditable(LiteYukonUser user) {
    	return rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS, user);
    }
    
    private List<MACSScheduleInfo> createScheduleInfoList(final List<Schedule> scheduleList, final boolean editable) {
        List<MACSScheduleInfo> infoList = new ArrayList<MACSScheduleInfo>(scheduleList.size());
        for (final Schedule schedule : scheduleList) {
            infoList.add(new MACSScheduleInfo(schedule, editable));
        }
        return infoList;
    }
    
    public enum ScriptsSortBy implements DisplayableEnum {
        
        scheduleName,
        categoryName,
        currentState,
        startDateTime,
        stopDateTime;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.scripts.innerView." + name();
        }
    }
    
}
