package com.cannontech.web.amr.macsscheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.macsscheduler.model.MacsException;
import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsScriptOptions;
import com.cannontech.amr.macsscheduler.model.MacsScriptTemplate;
import com.cannontech.amr.macsscheduler.model.MacsSimpleOptions;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy.DayOfWeek;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy.StartPolicy;
import com.cannontech.amr.macsscheduler.model.MacsStopPolicy.StopPolicy;
import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.billing.FileFormatTypes;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.SCHEDULER)
@Controller
@RequestMapping("/schedules/*")
public class MACSScheduleController {
    @Autowired private MACSScheduleService service;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private HolidayScheduleDao holidaySchedules;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private MacsScheduleValidator validator;
    @Autowired private IDatabaseCache databaseCache;

    private final static String scheduleKey = "yukon.web.modules.tools.schedule.";
    private final static String scriptsKey = "yukon.web.modules.tools.scripts.";
    
    @RequestMapping("view")
    public String view(ModelMap model, @DefaultSort(dir=Direction.asc, sort="scheduleName") SortingParameters sorting, 
            PagingParameters paging, @ModelAttribute("filter") MacsScriptFilter filter) {
        model.addAttribute("filter", filter);
        model.addAttribute("categories", service.getCategories());
        return "scheduledscripts.jsp";
    }
   
    @RequestMapping(value="innerView", method = RequestMethod.GET)
    public String innerView(ModelMap model, LiteYukonUser user, @DefaultSort(dir=Direction.asc, sort="scheduleName") SortingParameters sorting, 
            PagingParameters paging, @ModelAttribute("filter") MacsScriptFilter filter, YukonUserContext userContext) {
 
        List<MacsSchedule> schedules = new ArrayList<>();
        if (StringUtils.isNotBlank(filter.getCategory())) {
            schedules = service.getSchedulesByCategory(filter.getCategory());
        } else {
            schedules = service.getAllSchedules();
        }
                
        if (rolePropertyDao.checkRole(YukonRole.DEMAND_RESPONSE, user)) {
            schedules = paoAuthorizationService.filterAuthorized(user, schedules, Permission.LM_VISIBLE);
            //Without the DEMAND_RESPONSE, we want them to be able to see all scripts.
        } 
          
        boolean isEditable = isEditable(user);
        schedules.forEach(s -> s.setEditable(isEditable));
        
        SearchResults<MacsSchedule> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, schedules.size());
        
        ScriptsSortBy sortBy = ScriptsSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<MacsSchedule>itemList = Lists.newArrayList(schedules);
        Comparator<MacsSchedule> comparator = (o1, o2) -> o1.getScheduleName().compareTo(o2.getScheduleName());
        if (sortBy == ScriptsSortBy.categoryName) {
            comparator = (o1, o2) -> o1.getCategoryName().compareTo(o2.getCategoryName());
        } else if (sortBy == ScriptsSortBy.currentState) {
            comparator = (o1, o2) -> o1.getState().compareTo(o2.getState());
        } else if (sortBy == ScriptsSortBy.stopDateTime) {
            comparator = Comparator.comparing(MacsSchedule::getNextStopTime, Comparator.nullsFirst(Date::compareTo));
        } else if (sortBy == ScriptsSortBy.startDateTime) {
            comparator = Comparator.comparing(MacsSchedule::getNextRunTime, Comparator.nullsFirst(Date::compareTo));
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
        searchResult.setBounds(startIndex, itemsPerPage, schedules.size());
        searchResult.setResultList(itemList);        
        
        model.addAttribute("list", searchResult);
        model.addAttribute("categories", service.getCategories());
        model.addAttribute("filter", filter);

        return "schedulesView.jsp";
    }
    
    @RequestMapping(value="create", method = RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.CREATE)
    public String createSchedule(ModelMap model) {
        MacsSchedule schedule = new MacsSchedule();
        model.addAttribute("schedule", schedule);
        model.addAttribute("mode", PageEditMode.CREATE);
        model.addAttribute("getTemplate", true);
        setupModel(model, schedule);
        return "schedule.jsp";
    }
    
    @RequestMapping(value="getTemplate", method = RequestMethod.GET)
    public String getTemplate(ModelMap model, @ModelAttribute("schedule") MacsSchedule schedule) {
        model.addAttribute("mode", PageEditMode.CREATE);
        model.addAttribute("templateReceived", true);
        if (schedule.isScript()) {
            schedule.setScriptOptions(new MacsScriptOptions());
            MacsScriptHelper.loadDefaultScriptOptions(schedule);
            setupModel(model, schedule);
            return "scriptsTab.jsp";
        } else {
            schedule.setSimpleOptions(new MacsSimpleOptions());
            setupModel(model, schedule);
            return "commandsTab.jsp";
        }
    }
    
    @RequestMapping(value="{id}/view", method = RequestMethod.GET)
    public String viewSchedule(ModelMap model, @PathVariable int id, YukonUserContext userContext, FlashScope flash) {
        MacsSchedule schedule = service.getMacsScheduleById(id);
        model.addAttribute("schedule", schedule);
        model.addAttribute("mode", PageEditMode.VIEW);
        setupModel(model, schedule);
        try {
            loadScriptInfo(schedule, userContext.getYukonUser());
        } catch (MacsException e) {
            return addMacsExceptionToError(flash, scheduleKey + "loadScript.failure", e, userContext, "schedule.jsp");
        }
        return "schedule.jsp";
    }
    
    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.UPDATE)
    public String editSchedule(ModelMap model, @PathVariable int id, YukonUserContext userContext, FlashScope flash) {
        MacsSchedule schedule = service.getMacsScheduleById(id);
        model.addAttribute("schedule", schedule);
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, schedule);
        try {
            loadScriptInfo(schedule, userContext.getYukonUser());
        } catch (MacsException e) {
            return addMacsExceptionToError(flash, scheduleKey + "loadScript.failure", e, userContext, "schedule.jsp");
        }
        return "schedule.jsp";
    }
    
    private void loadScriptInfo(MacsSchedule schedule, LiteYukonUser user) throws MacsException {
        if (schedule.isScript()) {
            String script = service.getScript(schedule.getId(), user);
            MacsScriptHelper.loadOptionsFromScriptFile(script, schedule, databaseCache);
            schedule.getScriptOptions().setScriptText(script);
        }
    }
    
    private void setupModel(ModelMap model, MacsSchedule schedule) {
        model.addAttribute("types", new ArrayList<>(Arrays.asList(PaoType.SCRIPT, PaoType.SIMPLE_SCHEDULE)));
        model.addAttribute("categories", service.getCategories());
        model.addAttribute("templates", MacsScriptTemplate.values());
        model.addAttribute("startPolicyOptions", StartPolicy.values());
        model.addAttribute("holidaySchedules", holidaySchedules.getAllHolidaySchedules());
        model.addAttribute("stopPolicyOptions", StopPolicy.values());
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        model.addAttribute("fileFormats", FileFormatTypes.getValidFormatTypes());
        model.addAttribute("touRates", MacsScriptOptions.getTouRates());
        model.addAttribute("retryTypes", MacsScriptTemplate.getRetryTypes());
        model.addAttribute("ied300Types", MacsScriptTemplate.getIed300Types());
        model.addAttribute("ied400Types", MacsScriptTemplate.getIed400Types());
        model.addAttribute("iedTypes", MacsScriptOptions.getIedTypes());
        //check if device or load group
        model.addAttribute("target", "LOADGROUP");
        if (schedule.isSimple() && schedule.getSimpleOptions().getTargetPAObjectId() != 0) {
            LiteYukonPAObject pao = cache.getAllPaosMap().get(schedule.getSimpleOptions().getTargetPAObjectId());
            if (pao != null && !pao.getPaoType().isLoadGroup()) {
                model.addAttribute("target", "DEVICE");
            }
        }
    }
    
    @RequestMapping(value="{id}/delete", method = RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.OWNER)
    public String deleteSchedule(ModelMap model, @PathVariable int id, YukonUserContext userContext, FlashScope flash) {
        try {
            MacsSchedule schedule = service.getMacsScheduleById(id);
            service.delete(id, userContext.getYukonUser());
            flash.setConfirm(new YukonMessageSourceResolvable(scheduleKey + "delete.successful", schedule.getScheduleName()));
        } catch (MacsException e) {
            return addMacsExceptionToError(flash, scheduleKey + "delete.failure", e, userContext, "redirect:/macsscheduler/schedules/" + id + "/view");
        }
        return "redirect:/macsscheduler/schedules/view";
    }
    
    @RequestMapping(value="createScript", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.CREATE)
    public String createScript(@ModelAttribute("schedule") MacsSchedule schedule, YukonUserContext yukonUserContext, 
                               ModelMap model, BindingResult result, HttpServletResponse resp) {
        validator.validate(schedule, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModel(model, schedule);
            return "schedule.jsp";
        }
        MacsScriptHelper.generateScript(schedule, databaseCache);
        model.addAttribute("script", schedule.getScriptOptions().getScriptText());
        return "scriptEditorDialog.jsp";
    }
    
    @RequestMapping(value="save", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.UPDATE)
    public String saveSchedule(@ModelAttribute("schedule") MacsSchedule schedule, BindingResult result, YukonUserContext userContext, 
                               FlashScope flash, ModelMap model, HttpServletResponse resp) {
        int id = schedule.getId();
        validator.validate(schedule, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModel(model, schedule);
            return "schedule.jsp";
        }
        if (schedule.isGenerateScript()) {
            MacsScriptHelper.generateScript(schedule, databaseCache);
        }
        try {
            if (id > 0) {
                service.updateSchedule(schedule, userContext.getYukonUser());
            } else {
                id = service.createSchedule(schedule, userContext.getYukonUser());
            }
        } catch (DuplicateException e) {
            result.rejectValue("scheduleName", "yukon.web.error.nameConflict");
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModel(model, schedule);
            return "schedule.jsp";
        } catch (MacsException e) {
            setupModel(model, schedule);
            return addMacsExceptionToError(flash, scheduleKey + "save.failure", e, userContext, "schedule.jsp");
        }
        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(scheduleKey + "save.successful"));
        return "redirect:/macsscheduler/schedules/view";
    }
    
    @RequestMapping(value="{id}/startStop", method = RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.LIMITED)
    public String startStop(ModelMap model, YukonUserContext userContext, @PathVariable int id) throws Exception {        
        MacsSchedule schedule = service.getMacsScheduleById(id);
        final Calendar cal = dateFormattingService.getCalendar(userContext);
        final Calendar stopCal = (Calendar) cal.clone();
        stopCal.add(Calendar.HOUR_OF_DAY, 4);
                
        model.addAttribute("schedule", schedule);
        model.addAttribute("zone", userContext.getTimeZone().getDisplayName(true, TimeZone.SHORT));
        
        if (schedule.isRunning() || schedule.isPending()) {
            Date stopTime = new Date();
            if (schedule.getNextStopTime() != null) {
                stopTime = schedule.getNextStopTime();
            }
            model.addAttribute("stopTime", stopTime);
            return "stopDialog.jsp";
        }
        
        model.addAttribute("schedule", schedule);
        model.addAttribute("currentTime", cal.getTime());
        model.addAttribute("stopTime", stopCal.getTime());
        return "startDialog.jsp";
    }
    
    @RequestMapping(value="{id}/start", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.LIMITED)
    public @ResponseBody Map<String, Object> start(@PathVariable int id, YukonUserContext yukonUserContext, 
                      @RequestParam(value="startNow", required=false, defaultValue="false") Boolean startNow, 
                      @RequestParam("start") String startTime, @RequestParam("stop") String stopTime){
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        final TimeZone timeZone = yukonUserContext.getTimeZone();
        Map<String, Object> json = new HashMap<>();

        try {
            Date stop = dateFormattingService.flexibleDateParser(stopTime, yukonUserContext);
            Date start = null;
            if (startNow) {
                start = Calendar.getInstance(timeZone).getTime();
            } else {
                start = dateFormattingService.flexibleDateParser(startTime, yukonUserContext);
            }
            if (start.compareTo(stop) > 0) {
                json.put("errorMsg", messageSourceAccessor.getMessage(scriptsKey + "error.startDateBeforeEndDate"));
            } else {
                try {
                    service.start(id, start, stop, yukonUserContext.getYukonUser());
                } catch (MacsException e) {
                    String errorMsg = messageSourceAccessor.getMessage(scriptsKey + "error.start");
                    json.put("errorMsg",  messageSourceAccessor.getMessage(scheduleKey + "exception." + e.getType(), errorMsg));
                }
            }
        } catch (ParseException e) {
            json.put("errorMsg", messageSourceAccessor.getMessage(scriptsKey + "error.start.invalidDate"));
        }

        return json;
    }
    
    @RequestMapping(value="{id}/stop", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.LIMITED)
    public @ResponseBody Map<String, Object> stop(@PathVariable int id, YukonUserContext yukonUserContext, 
                     @RequestParam(value="stopNow", required=false, defaultValue="false") boolean stopNow, 
                     @RequestParam("stop") String stopTime){
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        final TimeZone timeZone = yukonUserContext.getTimeZone();
        Map<String, Object> json = new HashMap<>();

        Date stop = null;
        try {
            stop = dateFormattingService.flexibleDateParser(stopTime, yukonUserContext);
        } catch (ParseException e) {
            json.put("errorMsg", messageSourceAccessor.getMessage(scriptsKey + "error.stop.invalidDate"));
        }
        if (stopNow) {
            stop = Calendar.getInstance(timeZone).getTime();
        }
        try {
            service.stop(id, stop, yukonUserContext.getYukonUser());
        } catch (MacsException e) {
            String errorMsg = messageSourceAccessor.getMessage(scriptsKey + "error.stop");
            json.put("errorMsg",  messageSourceAccessor.getMessage(scheduleKey + "exception." + e.getType(), errorMsg));
        }
        return json;
    }

    @RequestMapping(value="{id}/toggleState", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MACS_SCRIPTS, level = HierarchyPermissionLevel.LIMITED)
    public @ResponseBody Map<String, Object> toggleState(@PathVariable int id, YukonUserContext yukonUserContext){    
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            service.enableDisableSchedule(id, yukonUserContext.getYukonUser());
        } catch (MacsException e) {
            String errorMsg = messageSourceAccessor.getMessage(scriptsKey + "error.enableDisable");
            json.put("errorMsg",  messageSourceAccessor.getMessage(scheduleKey + "exception." + e.getType(), errorMsg));
        }
        return json;
    }
    
    private boolean isEditable(LiteYukonUser user) {
    	return rolePropertyDao.checkLevel(YukonRoleProperty.MACS_SCRIPTS, HierarchyPermissionLevel.UPDATE, user);
    }
    
    private String addMacsExceptionToError(FlashScope flash, String errorKey, MacsException e, 
                                           YukonUserContext yukonUserContext, String returnView) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        String errorMsg = messageSourceAccessor.getMessage(errorKey);
        flash.setError(new YukonMessageSourceResolvable(scheduleKey + "exception." + e.getType(), errorMsg));
        if (e.getType() == MacsException.MACSExceptionType.NO_REPLY) {
            return "redirect:/macsscheduler/schedules/view";
        } else {
            return returnView;
        }
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
