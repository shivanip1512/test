package com.cannontech.web.capcontrol;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.core.schedule.model.PaoSchedule;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.PaoScheduleService;
import com.cannontech.web.capcontrol.service.PaoScheduleService.AssignmentStatus;
import com.cannontech.web.capcontrol.service.impl.PaoScheduleServiceHelper;
import com.cannontech.web.capcontrol.validators.PaoScheduleValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@RequestMapping("/schedules")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ScheduleController {
    
    @Autowired private PaoScheduleDao paoScheduleDao;
    @Autowired private PaoScheduleService scheduleService;
    @Autowired private PaoScheduleValidator scheduleValidator;
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PaoScheduleServiceHelper paoScheduleServiceHelper;
    
    private static final String baseKey = "yukon.web.modules.capcontrol.scheduleAssignments";

    private static final String NO_FILTER = "All";
    
    private static final Instant epoch1990 = new Instant(CtiUtilities.get1990GregCalendar().getTime());

    private void setUpModel(String command, String schedule, LiteYukonUser user, ModelMap model) {
        
        boolean hasCapBankRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
        boolean hasSubbusRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
        model.addAttribute("hasActionRoles", hasCapBankRole && hasSubbusRole);
        
        //Create filters
        boolean isFiltered = false;

        if (StringUtils.isNotEmpty(command) && !command.equals(NO_FILTER)) {
            isFiltered = true;
        }
        if (StringUtils.isNotEmpty(schedule) && !schedule.equals(NO_FILTER)) {
            isFiltered = true;
        }
        model.addAttribute("isFiltered", isFiltered);
        
        
        //Filter, sort and get search results
        List<PaoScheduleAssignment> assignments = scheduleService.getAssignmentsByFilter(command, schedule);
        
        assignments = paoScheduleServiceHelper.getAssignmentsByDMVFilter(assignments);
        model.addAttribute("assignments", assignments);

        setDMVTestCommand(model);

        model.addAttribute("verifyCommandsList", ScheduleCommand.getVerifyCommandsList());

        List<PaoSchedule> schedules = paoScheduleDao.getAll(); 
        model.addAttribute("scheduleList", schedules);
    }
    
    @RequestMapping("assignments")
    public String scheduleAssignments(
            ModelMap model,
            @RequestParam(defaultValue="All")String command, 
            @RequestParam(defaultValue="All")String schedule, 
            LiteYukonUser user) {
        
        setUpModel(command, schedule, user, model);
        return "schedule/scheduleassignment.jsp";
    }

    @RequestMapping("filter")
    public String scheduleAssignmentsFilter(
            ModelMap model,
            @RequestParam(defaultValue="All")String command, 
            @RequestParam(defaultValue="All")String schedule, 
            LiteYukonUser user) {
        command = getCommandName(command);
        setUpModel(command, schedule, user, model);
        return "schedule/scheduleassignmentTable.jsp";
    }

    @RequestMapping("")
    public String schedules(ModelMap model) {
        List<PaoSchedule> schedules = paoScheduleDao.getAll();
        model.addAttribute("schedules", schedules);

        model.addAttribute("epoch1990", epoch1990.plus(Duration.standardSeconds(1)));

        return "schedule/schedules.jsp";
    }
    

    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        PaoSchedule schedule = paoScheduleDao.getForId(id);
        boolean authorizedEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        PageEditMode pageMode = authorizedEdit ? PageEditMode.EDIT : PageEditMode.VIEW;
        model.addAttribute("mode", pageMode);
        model.addAttribute("scheduleDuration", Duration.standardSeconds(schedule.getRepeatSeconds()));

        Map<String, Collection<String>> deviceAssignments = scheduleService.getDeviceToCommandMapForSchedule(id);
        
        model.addAttribute("assignments", deviceAssignments);

        return setupEditModelMap(model, schedule);
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        PaoSchedule schedule = new PaoSchedule();
        Instant nowTime = Instant.now();
        schedule.setNextRunTime(nowTime);
        model.addAttribute("nowTime", nowTime);
        return setupEditModelMap(model, schedule);
    }

    private String setupEditModelMap(ModelMap model, PaoSchedule schedule) {
        model.addAttribute("schedule", schedule);
        model.addAttribute("epoch1990", epoch1990.plus(Duration.standardSeconds(1)));
        Instant nowTime = Instant.now();
        model.addAttribute("nowTime", nowTime);
        model.addAttribute("intervals", PaoSchedule.ScheduleInterval.values());
        return "schedule/schedule.jsp";
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value={"{id}","create"}, method=RequestMethod.POST)
    public String save(HttpServletResponse response, ModelMap model, @ModelAttribute("schedule") PaoSchedule schedule, BindingResult bindingResult) {
        if (schedule.getLastRunTime() == null) {
            schedule.setLastRunTime(epoch1990);
        }
        scheduleValidator.validate(schedule, bindingResult);
        if (!schedule.isLater()) {
            schedule.setNextRunTime(Instant.now());
        }
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return setupEditModelMap(model, schedule);
        } else {
            try {
                paoScheduleDao.save(schedule);
            //Name Conflict
            } catch (DataIntegrityViolationException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                bindingResult.rejectValue("name", "yukon.web.modules.capcontrol.schedules.error.nameConflict");
                return setupEditModelMap(model, schedule);
            }
            return null;
        }
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value="{id}", method=RequestMethod.DELETE)
    public String delete(HttpServletResponse response, @PathVariable int id) {
        
        scheduleService.delete(id);
        
        response.setStatus(HttpStatus.NO_CONTENT.value());
        return null;
    }


    /**
     * Run multiple schedule assignment commands.  The schedule assignments are optionally 
     * filtered by command and/or schedule.
     */
    @RequestMapping("start-multiple")
    public @ResponseBody Map<String, Object> startMultiple(
        ModelMap map,
        String startCommand, 
        String startSchedule, 
        YukonUserContext context) {
        
        startCommand = getCommandName(startCommand);
        setUpModel(startCommand, startSchedule, context.getYukonUser(), map);
        
        List<PaoScheduleAssignment> assignments = scheduleService.getAssignmentsByFilter(startCommand, startSchedule);
        
        int numberFailed = 0;
        String result = "";
        
        assignments = paoScheduleServiceHelper.getAssignmentsByDMVFilter(assignments);
        
        for (PaoScheduleAssignment assignment : assignments) {
            boolean success = scheduleService.sendStartCommand(assignment, context.getYukonUser());
            if (!success) numberFailed++;
        }
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        result = accessor.getMessage(baseKey + ".startedSchedules", assignments.size() - numberFailed, numberFailed);
        
        Map<String, Object> json = new HashMap<>();
        //if at least one command succeeded, consider it a success
        boolean success = (assignments.size() - numberFailed) > 0;
        json.put("success", success);
        json.put("resultText" , result);
        json.put("schedule", startSchedule);
        json.put("command", startCommand);
        
        return json;
    }
    
    /**
     * Stop multiple schedule assignment commands.  The schedule assignments are optionally 
     * filtered by command and/or schedule.  This is only applicable to "verify" commands.  
     * Any other schedule assignment commands that match the filter criteria will be ignored. 
     */
    @RequestMapping("stop-multiple")
    public  @ResponseBody Map<String, Object> stopMultiple(String stopCommand, String stopSchedule, YukonUserContext context) {
        stopCommand = getCommandName(stopCommand);
        int commandsSentCount = scheduleService.sendStopCommands(stopCommand, stopSchedule, context.getYukonUser());
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String result = accessor.getMessage(baseKey + ".stoppedSchedules", commandsSentCount);
        
        //if at least one command went out, consider it a success
        Map<String, Object> json = new HashMap<>();
        boolean success = commandsSentCount > 0;
        json.put("success", success);
        json.put("resultText" , result);
        json.put("schedule", stopSchedule);
        json.put("command", stopCommand);
        
        return json;
    }
    
    /**
     * Run a single schedule assignment command.
     */
    @RequestMapping("start")
    public @ResponseBody Map<String, Object> startSchedule(Integer eventId, String deviceName, YukonUserContext context) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
        
        boolean success = scheduleService.sendStartCommand(assignment, context.getYukonUser());
        String result;
        if (success) {
            result = accessor.getMessage(baseKey + ".startedScheduleSuccess", deviceName, assignment.getCommandName());
        } else {
            result = accessor.getMessage(baseKey + ".startedScheduleFailed", assignment.getCommandName());
        }

        Map<String, Object> json = new HashMap<>();
        json.put("sentCommand", assignment.getCommandName());
        json.put("success", success);
        json.put("resultText" , result);

        return json;
    }
    
    /**
     * Send a stop verify command to the specified subbus. 
     */
    @RequestMapping("stop")
    public @ResponseBody Map<String, Object> stopSchedule(Integer deviceId, String deviceName, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        String result = accessor.getMessage(baseKey + ".stopVerify", deviceName);
        Map<String, Object> json = new HashMap<>();
        json.put("resultText" , result);
        
        boolean success = scheduleService.sendStop(deviceId, context.getYukonUser());
        json.put("success", success);

        return json;
    }
    
    @RequestMapping(value="remove-assignment", method=RequestMethod.POST)
    public String removePao(Integer eventId, FlashScope flash) {
        
        boolean success = scheduleService.unassignCommand(eventId);
        
        if (success) {
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deleteSuccess"));
        } else {
            //Warn the user, the only way this happens is if we attempted to delete something that didn't exist.
            flash.setWarning(new YukonMessageSourceResolvable(baseKey + ".deleteFailed"));
        }
        
        return "redirect:assignments";
    }

    @RequestMapping("set-ovuv")
    public @ResponseBody Map<String, Object> setOvUv(Integer eventId, Integer ovuv, YukonUserContext context) {
        boolean success = false;
        Map<String, Object> json = new HashMap<>();
        try {
            PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
            assignment.setDisableOvUv(ovuv == 0 ? "Y" : "N");
            success = paoScheduleDao.updateAssignment(assignment);
        } catch (EmptyResultDataAccessException e) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
            String resultText = accessor.getMessage(baseKey + ".setOvUvFailed");
            json.put("resultText" , resultText);
        }
        json.put("id", eventId);
        json.put("success", success);
        return json;
    }

    @RequestMapping(value = "addPao")
    public String addPao(ModelMap map, String schedule, String command, int scheduleId, ScheduleCommand cmd,
            String paoIdList, String cmdInput, Integer dmvTestId, FlashScope flash, HttpServletResponse response) {

        List<Integer> paoIds = ServletUtil.getIntegerListFromString(paoIdList);

        AssignmentStatus result = scheduleService.assignCommand(scheduleId, cmd, paoIds, cmdInput, dmvTestId);

        switch (result) {
        case DUPLICATE:
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".duplicate"));
            setDMVTestCommand(map);
            setScheduleAssignmentPop(map, schedule, command);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "schedule/newScheduleAssignmentPopup.jsp";
        case NO_DEVICES:
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".noDeviceSelected"));
            setDMVTestCommand(map);
            setScheduleAssignmentPop(map, schedule, command);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "schedule/newScheduleAssignmentPopup.jsp";
        case SUCCESS:
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".addSuccessful", paoIds.size()));
            break;
        case INVALID:
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".noScheduleOrCommand"));
            setDMVTestCommand(map);
            setScheduleAssignmentPop(map, schedule, command);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "schedule/newScheduleAssignmentPopup.jsp";
        case NO_DMVTEST:
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".noDmvTestCommand"));
            setDMVTestCommand(map);
            setScheduleAssignmentPop(map, schedule, command);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "schedule/newScheduleAssignmentPopup.jsp";
        }

        return null;
    }
    
    /**
     * Returns the "Run Multiple Schedule Assignment Commands" popup form.
     */
    @RequestMapping("startMultiScheduleAssignmentPopup")
    public String startMultiScheduleAssignmentPopup(ModelMap map,
            @RequestParam(defaultValue="All") String schedule,
            @RequestParam(defaultValue="All") String command) {

        setDMVTestCommand(map);

        setScheduleAssignmentPop(map, schedule, command);
        return "schedule/startMultiScheduleAssignmentPopup.jsp";
    }
    
    /**
     * Returns the "Stop Multiple Schedule Assignment Commands" popup form.
     */
    @RequestMapping("stop-multiple-settings")
    public String stopMultiScheduleAssignmentPopup(ModelMap map,
            @RequestParam(defaultValue="All") String schedule,
            @RequestParam(defaultValue="All") String command) {

        setScheduleAssignmentPop(map, schedule, command);
        map.addAttribute("verifyCommandsList", ScheduleCommand.getVerifyCommandsList());
        
        return "schedule/stopMultiScheduleAssignmentPopup.jsp";
    }
    
    /**
     * Returns the "Run Multiple Schedule Assignment Commands" popup form.
     */
    @RequestMapping("create-settings")
    public String newScheduleAssignmentPopup(ModelMap map,
            @RequestParam(defaultValue="All") String schedule,
            @RequestParam(defaultValue="All") String command) {
        setDMVTestCommand(map);
        setScheduleAssignmentPop(map, schedule, command);
        
        return "schedule/newScheduleAssignmentPopup.jsp";
    }

    private void setDMVTestCommand(ModelMap map) {
        boolean usesDmvTest = MasterConfigLicenseKey.DEMAND_MEASUREMENT_VERIFICATION_ENABLED.getKey().equals(
            configurationSource.getString("DEMAND_MEASUREMENT_VERIFICATION_ENABLED"));

        if (usesDmvTest) {
            map.addAttribute("dmvTestCommand", ScheduleCommand.DmvTest);
            map.addAttribute("commandList", ScheduleCommand.values());
        } else {
            map.addAttribute("commandList", ScheduleCommand.getRequiredCommands());
        }
    }

    private void setScheduleAssignmentPop(ModelMap map, String schedule, String command) {
        List<PaoSchedule> schedList = paoScheduleDao.getAll();

        map.addAttribute("schedule", schedule);
        map.addAttribute("command", command);
        map.addAttribute("scheduleList", schedList);
    }

    private String getCommandName(String commandName) {
        if(!commandName.equals("All")) {
        ScheduleCommand scheduleCommand = ScheduleCommand.valueOf(commandName);
        commandName = scheduleCommand.getCommandName();
        }
        return commandName;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {

        PropertyEditor instantEditor = datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, userContext, BlankMode.NULL);

        binder.registerCustomEditor(Instant.class, instantEditor);
    }
}