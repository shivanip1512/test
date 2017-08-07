package com.cannontech.web.amr.macsscheduler;

import java.text.ParseException;
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

import com.cannontech.amr.macsscheduler.model.MacsException;
import com.cannontech.amr.macsscheduler.model.MacsException.MACSExceptionType;
import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsScriptOptions;
import com.cannontech.amr.macsscheduler.model.MacsScriptTemplate;
import com.cannontech.amr.macsscheduler.model.MacsSimpleOptions;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy;
import com.cannontech.amr.macsscheduler.model.MacsStopPolicy;
import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.billing.FileFormatTypes;
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
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.SCHEDULER)
@Controller
@RequestMapping("/schedules/*")
public class MACSScheduleController extends MultiActionController {
    @Autowired private MACSScheduleService service;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("view")
    public String view(ModelMap model, @DefaultSort(dir=Direction.asc, sort="scheduleName") SortingParameters sorting, 
            @DefaultItemsPerPage(10) PagingParameters paging) {
        return "scheduledscripts.jsp";
    }
   
    @RequestMapping("innerView")
    public String innerView(ModelMap model, LiteYukonUser user, @DefaultSort(dir=Direction.asc, sort="scheduleName") SortingParameters sorting, 
            @DefaultItemsPerPage(10) PagingParameters paging, YukonUserContext userContext) {
 
        List<MacsSchedule> schedules = service.getAllSchedules();
        
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
            comparator = (o1, o2) -> o1.getNextStopTime().compareTo(o2.getNextStopTime());
        } else if (sortBy == ScriptsSortBy.startDateTime) {
            comparator = (o1, o2) -> o1.getNextRunTime().compareTo(o2.getNextRunTime());
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

        return "schedulesView.jsp";
    }
    
    @RequestMapping("{id}/view")
    public String viewSchedule(ModelMap model,YukonUserContext yukonUserContext, @PathVariable int id) {
        //TODO: Get Script text and add to model
        try {
            String script = service.getScript(id, yukonUserContext.getYukonUser());
            model.addAttribute("script", script);
        } catch (MacsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "script.jsp";
    }
    
    @RequestMapping("{id}/startStop")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public String startStop(ModelMap model, YukonUserContext userContext, @PathVariable int id) throws Exception {        
        MacsSchedule schedule = service.getMacsScheduleById(id);
        final Calendar cal = dateFormattingService.getCalendar(userContext);
        final Calendar stopCal = (Calendar) cal.clone();
        stopCal.add(Calendar.HOUR_OF_DAY, 4);
                
        model.addAttribute("schedule", schedule);
        model.addAttribute("zone", userContext.getTimeZone().getDisplayName(true, TimeZone.SHORT));
        
        if (schedule.isRunning() || schedule.isPending()) {
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
                json.put("errorMsg", messageSourceAccessor.getMessage("yukon.web.modules.tools.scripts.start.error.startDateBeforeEndDate"));
            } else {
                try {
                    service.start(id, start, stop, yukonUserContext.getYukonUser());
                } catch (MacsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return json;
    }
    
    @RequestMapping("{id}/stop")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public @ResponseBody Map<String, Object> stop(@PathVariable int id, YukonUserContext yukonUserContext, 
                     @RequestParam(value="stopNow", required=false, defaultValue="false") boolean stopNow, 
                     @RequestParam("stop") String stopTime){
        final TimeZone timeZone = yukonUserContext.getTimeZone();
        Map<String, Object> json = new HashMap<>();

        Date stop = null;
        try {
            stop = dateFormattingService.flexibleDateParser(stopTime, yukonUserContext);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (stopNow) {
            stop = Calendar.getInstance(timeZone).getTime();
        }
        try {
            service.stop(id, stop, yukonUserContext.getYukonUser());
        } catch (MacsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping("{id}/toggleState")
    @CheckRoleProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS)
    public @ResponseBody Map<String, Object> toggleState(ModelMap model, @PathVariable int id, LiteYukonUser user){      
        Map<String, Object> json = new HashMap<>();
        try {
            service.enableDisableSchedule(id, user);
        } catch (MacsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }
    
    private boolean isEditable(LiteYukonUser user) {
    	return rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_DISABLE_SCRIPTS, user);
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
    
   
    /**
     *  qa14 - simple
     *  qa8 - script
     */
    private void createSchedule() {

        // FIRST SCREEN

        MacsSchedule schedule = new MacsSchedule();
        // check for duplicate - service - isScheduleNameExists
        schedule.setScheduleName("");
        // service - getCategories()
        schedule.setCategoryName("");

        // template list
        MacsScriptTemplate.values(); // description field

        MacsStartPolicy startPolicy = schedule.getStartPolicy();
        // getStartTime and getValidWeekDays needs to return a string in a certain format to populate the
        // message to c++ service
        // MacsStartPolicy.StartPolicy enum - CronTagStyleType?
        // ServerDatabaseCache.allHolidaySchedules
        startPolicy.setHolidayScheduleId(0);

        MacsStopPolicy stopPolicy = schedule.getStopPolicy();
        // getStopTime needs to return a string in a certain format to populate the message to c++ service

        // NEXT SCREEN
        if (schedule.isScript()) {
            MacsScriptOptions options = new MacsScriptOptions();
            schedule.setScriptOptions(options);
            // make sure file ends with ".ctl" or append ".ctl" to it and WebFileUtils.isValidWindowsFilename
            options.setFileName("");

            if (schedule.getTemplate().isNoTemplateSelected()) {
                // value from the text editor
                options.setScriptText("");

            }

            if (!schedule.getTemplate().isNoTemplateSelected()) {

                if (!schedule.getTemplate().isRetry()) {
                    // display retry options
                }
                if (schedule.getTemplate().isIed()) {

                    // display TOU rate dropdown
                    // value MacsScriptOptions.touRates

                    // selected value
                    options.setTouRate("");

                    // display reset demand checkbox
                    options.setHasDemandResetInfo(true); // if checked

                    // display reset retry count - possible values validated 0 - 5
                    options.setDemandResetRetryCount(0);

                    if (schedule.getTemplate().isIed300()) {
                        // display 2 checkboxs - "Landis-Gyr S4" and "Alpha"
                        options.setFrozenDemandRegister("Landis-Gyr S4 or Alpha");
                    }

                    if (schedule.getTemplate().isIed400()) {
                        // display IEDType dropdown
                        // MacsScriptOptions iedTypes
                        options.setIedType("");
                    }
                }
            }

            // OPTIONS TAB

            // if checked
            options.setHasNotificationInfo(true);
            // dropdown - ServerDatabaseCache getAllContactNotificationGroups()

            // if checked
            options.setHasBillingInfo(true);
            // dowpdown of file formats
            String[] formats = FileFormatTypes.getValidFormatTypes();

            // TEXT Editor Tab
            String content = MacsScriptHelper.build(options, schedule.getTemplate());
            options.setScriptText(content);

        }

        if (schedule.isSimple()) {
            MacsSimpleOptions options = new MacsSimpleOptions();
            schedule.setSimpleOptions(options);
            options.getStartCommand();
            options.getStopCommand();
            
            //TDC displays start/stop commands as dropdown, I couldn't find where it is populated
            //Searched SimpleSchedulePanel for xxxComboBox.addItem
            
            //the id user chose from the selection box.
            options.setTargetPAObjectId(0);
        }
        
        //SAVING SCHEDULE
        try {
            if (schedule.isScript()) {
                // writes script to disc
                MacsScriptHelper.write(schedule.getScriptOptions().getFileName(),
                    schedule.getScriptOptions().getScriptText());
            }
            // sends message to c++ service
            service.createSchedule(schedule, null);
        } catch (DuplicateException e) {
            if (schedule.isScript()) {
                MacsScriptHelper.delete(schedule.getScriptOptions().getFileName());
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MacsException e) {
            if (schedule.isScript()) {
                MacsScriptHelper.delete(schedule.getScriptOptions().getFileName());
            }
            
            MACSExceptionType type = e.getType();
            //display error based on type.
            
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
