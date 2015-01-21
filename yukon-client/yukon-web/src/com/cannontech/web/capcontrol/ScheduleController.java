package com.cannontech.web.capcontrol;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.cbc.commands.CapControlCommandExecutor;
import com.cannontech.cbc.commands.CommandHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.core.schedule.model.PaoSchedule;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentCommandFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentRowMapper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@RequestMapping("/schedule/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ScheduleController {
    
    private static final Logger log = YukonLogManager.getLogger(ScheduleController.class);
    
    private PeriodFormatter periodFormatter;

    @Autowired private PaoScheduleDao paoScheduleDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private FilterDao filterService;
    @Autowired private CapControlCommandExecutor executor;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

    private final String NO_FILTER = "All";
    
    public ScheduleController() {
        PeriodFormatterBuilder builder = new PeriodFormatterBuilder()
        .appendMinutes().appendLiteral(" min ")
        .appendHours().appendLiteral(" hr ")
        .appendDays().appendLiteral(" day ")
        .appendWeeks().appendLiteral(" wk");
        periodFormatter = builder.toFormatter();
    }
    
    private void setUpModel(HttpServletRequest request, LiteYukonUser user, ModelMap model) {
        
        boolean hasEditingRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        boolean hasCapBankRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
        boolean hasSubbusRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        model.addAttribute("hasActionRoles", hasCapBankRole && hasSubbusRole);
        
        //Create filters
        boolean isFiltered = false;
        String filterByCommand = ServletRequestUtils.getStringParameter(request, "command", "");
        String filterBySchedule = ServletRequestUtils.getStringParameter(request, "schedule", "");
        List<UiFilter<PaoScheduleAssignment>> filters = new ArrayList<UiFilter<PaoScheduleAssignment>>();
        
        if (StringUtils.isNotEmpty(filterByCommand) && !filterByCommand.equals("All")) {
            filters.add(new ScheduleAssignmentCommandFilter(ScheduleCommand.valueOf(filterByCommand)));
            isFiltered = true;
        }
        if (StringUtils.isNotEmpty(filterBySchedule) && !filterBySchedule.equals("All")) {
            filters.add(new ScheduleAssignmentFilter(filterBySchedule));
            isFiltered = true;
        }
        model.addAttribute("isFiltered", isFiltered);
        UiFilter<PaoScheduleAssignment> filter = UiFilterList.wrap(filters);
        
        Comparator<PaoScheduleAssignment> sorter = new Comparator<PaoScheduleAssignment>() {
            @Override
            public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2) {
                return assignment1.compareTo(assignment2);
            }
        };
        
        ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
        
        //Filter, sort and get search results
        List<PaoScheduleAssignment> assignments = filterService.filter(filter, sorter, rowMapper);
        model.addAttribute("assignments", assignments);
        
        model.addAttribute("commandList", ScheduleCommand.values());
        model.addAttribute("verifyCommandsList", ScheduleCommand.getVerifyCommandsList());
        List<PAOSchedule> schedules = paoScheduleDao.getAllPaoScheduleNames(); 
        Collections.sort(schedules);
        
        model.addAttribute("schedules", schedules);
        
    }
    
    @RequestMapping("scheduleAssignmentsTable")
    public String scheduleAssignmentsTable(HttpServletRequest request, LiteYukonUser user, ModelMap model) {
        
        setUpModel(request, user, model);
        return "schedule/scheduleassignmentTable.jsp";
    }

    @RequestMapping("scheduleAssignments")
    public String scheduleAssignments(HttpServletRequest request, LiteYukonUser user, ModelMap model) {
        
        setUpModel(request, user, model);
        return "schedule/scheduleassignment.jsp";
    }

    @RequestMapping("filter")
    public String scheduleAssignmentsFilter(HttpServletRequest request, LiteYukonUser user, ModelMap model) {
        
        setUpModel(request, user, model);
        return "schedule/scheduleassignmentTable.jsp";
    }
    
    private void setupSchedulesTabModel(HttpServletRequest request, LiteYukonUser user, ModelMap model) {

        List<PaoSchedule> schedules = paoScheduleDao.getAll();
        model.addAttribute("schedules", schedules);

        model.addAttribute("epoch1990", epoch1990.plus(Duration.standardSeconds(1)));

    }
    
    @RequestMapping("schedules")
    public String schedules(HttpServletRequest request, LiteYukonUser user, ModelMap map) {
        setupSchedulesTabModel(request, user, map);
        return "schedule/schedules.jsp";
    }
    
    private static final Instant epoch1990 = new Instant(CtiUtilities.get1990GregCalendar().getTime());

    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        PaoSchedule schedule = paoScheduleDao.getForId(id);
        boolean authorizedEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        PageEditMode pageMode = authorizedEdit ? PageEditMode.EDIT : PageEditMode.VIEW;
        model.addAttribute("mode", pageMode);
        model.addAttribute("scheduleDuration", Duration.standardSeconds(schedule.getRepeatSeconds()));
        List<PaoScheduleAssignment> assignments = paoScheduleDao.getScheduleAssignmentByScheduleId(id);
        model.addAttribute("assignments", assignments);

        return setupEditModelMap(model, schedule);
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        PaoSchedule schedule = new PaoSchedule();
        schedule.setNextRunTime(Instant.now());
        return setupEditModelMap(model, schedule);
    }

    private String setupEditModelMap(ModelMap model, PaoSchedule schedule) {
        model.addAttribute("schedule", schedule);
        model.addAttribute("epoch1990", epoch1990.plus(Duration.standardSeconds(1)));
        
        model.addAttribute("intervals", PaoSchedule.ScheduleInterval.values());
        return "schedule/schedule.jsp";
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value="{id}", method=RequestMethod.POST)
    public String save(HttpServletResponse response, ModelMap model, @ModelAttribute("schedule") PaoSchedule schedule, BindingResult bindingResult) {
        if (schedule.getLastRunTime() == null) {
            schedule.setLastRunTime(epoch1990);
        }
        scheduleValidator.validate(schedule, bindingResult);
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return setupEditModelMap(model, schedule);
        } else {
            try {
                paoScheduleDao.save(schedule);
            //Name Conflict
            } catch (DataIntegrityViolationException e) {
                bindingResult.rejectValue("name", "yukon.web.modules.capcontrol.schedules.error.nameConflict");
                return setupEditModelMap(model, schedule);
            }
            return null;
        }
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value="{id}/delete")
    public String delete(HttpServletResponse response, ModelMap model, @PathVariable int id) {
        paoScheduleDao.delete(id);
        return null;
    }

    private final Validator scheduleValidator = new SimpleValidator<PaoSchedule>(PaoSchedule.class) {
        @Override
        public void doValidation(PaoSchedule schedule, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "name", "yukon.web.modules.capcontrol.schedules.error.nameEmpty");

            //For create, we cannot take an existing name
            if (schedule.getId() == null) {
                boolean nameTaken = paoScheduleDao.doesNameExist(schedule.getName());
                if (nameTaken) {
                    errors.rejectValue("name", "yukon.web.modules.capcontrol.schedules.error.nameConflict");
                }
            //For edit, we cannot take a different schedules name
            } else {
                PaoSchedule existingWithName = paoScheduleDao.findForName(schedule.getName());
                if (existingWithName != null && ! existingWithName.getId().equals(schedule.getId())) {
                    errors.rejectValue("name", "yukon.web.modules.capcontrol.schedules.error.nameConflict");
                }
            }

            if (schedule.getNextRunTime() == null) {
                errors.rejectValue("nextRunTime", "yukon.web.modules.capcontrol.schedules.error.date");
            }
        }
    };

    private boolean executeScheduleCommand(PaoScheduleAssignment assignment, LiteYukonUser user) {
        boolean isCommandValid = true;
        ScheduleCommand schedCommand = null;
        
        try{
            schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName());
        } catch(IllegalArgumentException e) {
            isCommandValid = false;
            log.error("Run schedule assignment command failed.  Invalid command: " + assignment.getCommandName());
        }
        
        if (isCommandValid) {
            
            CapControlCommand command;
            if (schedCommand == ScheduleCommand.VerifyNotOperatedIn) {
                //VerifyNotOperatedIn command is special.  It has a time value associated
                //with it that must be parsed from the command string.
                long secondsNotOperatedIn = ScheduleCommand.DEFAULT_INACTIVITY_TIME;
                secondsNotOperatedIn = parseSecondsNotOperatedIn(assignment);
                command = CommandHelper.buildVerifyInactiveBanks(user, CommandType.VERIFY_INACTIVE_BANKS, 
                        assignment.getPaoId(), false, secondsNotOperatedIn);
            } else if (ScheduleCommand.getVerifyCommandsList().contains(schedCommand)) {
                command = CommandHelper.buildVerifyBanks(user, CommandType.getForId(schedCommand.getCapControlCommand()), 
                        assignment.getPaoId(), false);
            } else {
                command = CommandHelper.buildItemCommand(schedCommand.getCapControlCommand(), assignment.getPaoId(), 
                        user);
            }
            try {
                executor.execute(command);
            } catch (CommandExecutionException e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    
    /**
     * Run multiple schedule assignment commands.  The schedule assignments are optionally 
     * filtered by command and/or schedule.
     */
    @RequestMapping("startMultiple")
    public @ResponseBody Map<String, Object> startMultiple(HttpServletRequest request, YukonUserContext context, 
            LiteYukonUser user, ModelMap map) {
        
        setUpModel(request, user, map);
        
        String filterByCommand = ServletRequestUtils.getStringParameter(request, "startCommand", "");
        String filterBySchedule = ServletRequestUtils.getStringParameter(request, "startSchedule", "");
        
        List<PaoScheduleAssignment> assignments = filterPaoScheduleAssignments(filterByCommand, filterBySchedule);
        
        int numberFailed = 0;
        String result = "";
        
        for (PaoScheduleAssignment assignment : assignments) {
            boolean success = executeScheduleCommand(assignment, context.getYukonUser());
            if (!success) numberFailed++;
        }
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.startedSchedules", 
                assignments.size() - numberFailed, numberFailed);
        
        Map<String, Object> json = new HashMap<>();
        //if at least one command succeeded, consider it a success
        boolean success = (assignments.size() - numberFailed) > 0;
        json.put("success", success);
        json.put("resultText" , result);
        json.put("schedule", filterBySchedule);
        json.put("command", filterByCommand);
        
        return json;
    }
    
    /**
     * Stop multiple schedule assignment commands.  The schedule assignments are optionally 
     * filtered by command and/or schedule.  This is only applicable to "verify" commands.  
     * Any other schedule assignment commands that match the filter criteria will be ignored. 
     */
    @RequestMapping("stopMultiple")
    public  @ResponseBody Map<String, Object> stopMultiple(HttpServletRequest request, YukonUserContext context, 
            ModelMap map) {
        String filterByCommand = ServletRequestUtils.getStringParameter(request, "stopCommand", "");
        String filterBySchedule = ServletRequestUtils.getStringParameter(request, "stopSchedule", "");
        
        List<PaoScheduleAssignment> assignments = filterPaoScheduleAssignments(filterByCommand, filterBySchedule);
        
        String result = "";
        int commandsSentCount = 0;
        
        for (PaoScheduleAssignment assignment : assignments) {
            
            boolean stopApplicable = true;
            int deviceId = assignment.getPaoId();
            ScheduleCommand schedCommand = null;
            
            try {
                schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName());
                if (schedCommand == ScheduleCommand.ConfirmSub || schedCommand == ScheduleCommand.SendTimeSyncs) {
                    //stop is not applicable to schedules with these commands
                    stopApplicable = false;
                    log.info("Schedule assignment stop ignored.  Command: " + assignment.getCommandName() + 
                            " is not a verification command.");
                }
            } catch(IllegalArgumentException e) {
                //invalid ScheduleCommand
                stopApplicable = false;
                log.error("Stop schedule assignment command failed.  Invalid command: " + assignment.getCommandName());
            }
            
            //send stop command
            if (stopApplicable) {
                VerifyBanks command = CommandHelper.buildStopVerifyBanks(context.getYukonUser(), 
                        CommandType.STOP_VERIFICATION, deviceId);
                try {
                    executor.execute(command);
                    commandsSentCount++;
                } catch (CommandExecutionException e) {
                    log.error("Stop schedule assignment command failed: " + assignment.getCommandName(), e);
                }
            }    
        }
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.stoppedSchedules", commandsSentCount);
        
        //if at least one command went out, consider it a success
        Map<String, Object> json = new HashMap<>();
        boolean success = commandsSentCount > 0;
        json.put("success", success);
        json.put("resultText" , result);
        json.put("schedule", filterBySchedule);
        json.put("command", filterByCommand);
        
        return json;
    }
    
    /**
     * Run a single schedule assignment command.
     */
    @RequestMapping("startSchedule")
    public @ResponseBody Map<String, Object> startSchedule(Integer eventId, String deviceName, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String result;
        PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
        boolean success = executeScheduleCommand(assignment, context.getYukonUser());
        if (success) {
            result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.startedScheduleSuccess", 
                    deviceName, assignment.getCommandName());
        } else {
            result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.startedScheduleFailed", 
                    assignment.getCommandName());
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
    @RequestMapping("stopSchedule")
    public @ResponseBody Map<String, Object> stopSchedule(Integer deviceId, String deviceName, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        String result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.stopVerify", deviceName);
        Map<String, Object> json = new HashMap<>();
        json.put("resultText" , result);
            try {
            executor.execute(CommandHelper.buildStopVerifyBanks(context.getYukonUser(),
                                                                CommandType.STOP_VERIFICATION, deviceId));
            json.put("success", true);
        } catch (CommandExecutionException e) {
            log.warn("caught exception in stopSchedule", e);
            json.put("success", false);
        }

        return json;
    }
    
    @RequestMapping(value="removePao", method=RequestMethod.POST)
    public String removePao(Integer eventId, Integer paoId, ModelMap map, FlashScope flash) {
        boolean success = paoScheduleDao.unassignCommandByEventId(eventId);
        
        if (success) {
            //Send DB Change for affected bus.
            DBChangeMsg dbChange = new DBChangeMsg(paoId,
                                                   DBChangeMsg.CHANGE_PAO_DB,
                                                   PaoType.CAP_CONTROL_SUBBUS.getPaoCategory().getDbString(),
                                                   PaoType.CAP_CONTROL_SUBBUS.getDbString(),
                                                   DbChangeType.UPDATE);
            dbChangeManager.processDbChange(dbChange);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.scheduleAssignments.deleteSuccess"));
        } else {
            //Warn the user, the only way this happens is if we attempted to delete something that didn't exist.
            flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.scheduleAssignments.deleteFailed"));
        }
        
        return "redirect:scheduleAssignments";
    }
    
    @RequestMapping(value="deleteSchedule")
    public String deleteSchedule(int scheduleId, FlashScope flash) {
        List<PaoScheduleAssignment> assignments = paoScheduleDao.getScheduleAssignmentByScheduleId(scheduleId);
        boolean success = paoScheduleDao.delete(scheduleId);
        if (success) {
            //Send DB Change
            //These can only be assigned to sub bus objects right now, if that changes, this needs to change with it.
            for (PaoScheduleAssignment assignment : assignments) {
                //Each assignment is a bus to be reloaded.
                DBChangeMsg dbChange = new DBChangeMsg(assignment.getPaoId(),
                                                       DBChangeMsg.CHANGE_PAO_DB,
                                                       PaoType.CAP_CONTROL_SUBBUS.getPaoCategory().getDbString(),
                                                       PaoType.CAP_CONTROL_SUBBUS.getDbString(),
                                                       DbChangeType.UPDATE);
                dbChangeManager.processDbChange(dbChange);
            }
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.schedules.deleteSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.schedules.deleteFailed"));
        }
        
        return "redirect:schedules";
    }
    
    @RequestMapping("setOvUv")
    public @ResponseBody Map<String, Object> setOvUv(Integer eventId, Integer ovuv, ModelMap map, 
            YukonUserContext context) {
        boolean success = false;
        Map<String, Object> json = new HashMap<>();
        try {
            PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
            assignment.setDisableOvUv(ovuv == 0 ? "Y" : "N");
            success = paoScheduleDao.updateAssignment(assignment);
        } catch (EmptyResultDataAccessException e) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
            String resultText = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.setOvUvFailed");
            json.put("resultText" , resultText);
        }
        json.put("id", eventId);
        json.put("success", success);
        return json;
    }

    @RequestMapping(value="addPao")
    public String addPao(String addSchedule, String addCommand, String filterCommand, String filterSchedule, 
            String paoIdList, ModelMap map, FlashScope flash) {
        ScheduleCommand cmd = ScheduleCommand.valueOf(addCommand);
        boolean success = true;
        YukonMessageSourceResolvable message;
        
        Integer schedId = Integer.parseInt(addSchedule);
        if (addSchedule != null && cmd != null) {
            List<Integer> paoIds = ServletUtil.getIntegerListFromString(paoIdList);

            if (paoIds.size() == 0) {
                success = false;
                message = new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.scheduleAssignments.noDeviceSelected");
            } else {
                List<PaoScheduleAssignment> assignments = new ArrayList<PaoScheduleAssignment>();
                
                for (Integer paoId : paoIds) {
                    PaoScheduleAssignment newAssignment = new PaoScheduleAssignment();
                    newAssignment.setCommandName(cmd.getCommandName());
                    newAssignment.setPaoId(paoId);
                    newAssignment.setScheduleId(schedId);
                    newAssignment.setDisableOvUv("N");
                    
                    assignments.add(newAssignment);
                }
                
                try {
                    paoScheduleDao.assignCommand(assignments);
                    message = new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.scheduleAssignments.addSuccessful", assignments.size());
                    
                    //Send DB Change
                    //These can only be assigned to sub bus objects right now, if that changes, this needs to change with it.
                    for (Integer paoId : paoIds) {
                        //Each paoId is a bus to be reloaded.
                        DBChangeMsg dbChange = new DBChangeMsg(paoId,
                                                               DBChangeMsg.CHANGE_PAO_DB,
                                                               PaoType.CAP_CONTROL_SUBBUS.getPaoCategory().getDbString(),
                                                               PaoType.CAP_CONTROL_SUBBUS.getDbString(),
                                                               DbChangeType.UPDATE);
                        dbChangeManager.processDbChange(dbChange);
                    }
                    
                    
                } catch (DataIntegrityViolationException e) {
                    success = false;
                    message = new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.scheduleAssignments.duplicate");
                }
            }
        } else {
            success = false;
            message = new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.scheduleAssignments.noScheduleOrCommand");
        }
        
        if (success) {
            flash.setConfirm(message);
        } else {
            flash.setError(message);
        }
        
        return "redirect:scheduleAssignments";
    }
    
    /**
     * Returns the "Run Multiple Schedule Assignment Commands" popup form.
     */
    @RequestMapping("startMultiScheduleAssignmentPopup")
    public String startMultiScheduleAssignmentPopup(HttpServletRequest request, ModelMap map) {
        String schedule = ServletRequestUtils.getStringParameter(request, "schedule", NO_FILTER);
        String command = ServletRequestUtils.getStringParameter(request, "command", NO_FILTER);
        List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
        
        map.addAttribute("schedule", schedule);
        map.addAttribute("command", command);
        map.addAttribute("commandList", ScheduleCommand.values());
        map.addAttribute("scheduleList", schedList);
        
        return "schedule/startMultiScheduleAssignmentPopup.jsp";
    }
    
    /**
     * Returns the "Stop Multiple Schedule Assignment Commands" popup form.
     */
    @RequestMapping("stopMultiScheduleAssignmentPopup")
    public String stopMultiScheduleAssignmentPopup(HttpServletRequest request, ModelMap map) {
        String schedule = ServletRequestUtils.getStringParameter(request, "schedule", NO_FILTER);
        String command = ServletRequestUtils.getStringParameter(request, "command", NO_FILTER);
        List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
        
        map.addAttribute("schedule", schedule);
        map.addAttribute("command", command);
        map.addAttribute("verifyCommandsList", ScheduleCommand.getVerifyCommandsList());
        map.addAttribute("scheduleList", schedList);
        
        return "schedule/stopMultiScheduleAssignmentPopup.jsp";
    }
    
    /**
     * Returns the "Run Multiple Schedule Assignment Commands" popup form.
     */
    @RequestMapping("newScheduleAssignmentPopup")
    public String newScheduleAssignmentPopup(HttpServletRequest request, ModelMap map) {
        String schedule = ServletRequestUtils.getStringParameter(request, "schedule", NO_FILTER);
        String command = ServletRequestUtils.getStringParameter(request, "command", NO_FILTER);
        List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
        
        map.addAttribute("schedule", schedule);
        map.addAttribute("command", command);
        map.addAttribute("commandList", ScheduleCommand.values());
        map.addAttribute("scheduleList", schedList);
        
        return "schedule/newScheduleAssignmentPopup.jsp";
    }
    
    /*
     * Helper method to parse the time variables out of a
     * "verify not operated in..." command
     */
    private long parseSecondsNotOperatedIn(PaoScheduleAssignment assignment) {
        String timeString = assignment.getCommandName().replaceAll(ScheduleCommand.VerifyNotOperatedIn.getCommandName() 
                + " ", "");
    
        //parse min/hr/day/wk value from command string
        Period period = periodFormatter.parsePeriod(timeString);
        return period.toStandardSeconds().getSeconds();
    }
    
    /* 
     * Helper method to simplify filtering schedule assignments.
     */
    private List<PaoScheduleAssignment> filterPaoScheduleAssignments(String filterCommand, String filterSchedule) {
        
        //Convert filter strings into filters
        //Filtering on "All" is equivalent to no filters
        List<UiFilter<PaoScheduleAssignment>> filters = new ArrayList<UiFilter<PaoScheduleAssignment>>();
        if (StringUtils.isNotEmpty(filterCommand) && !filterCommand.equals(NO_FILTER)) {
            filters.add(new ScheduleAssignmentCommandFilter(ScheduleCommand.valueOf(filterCommand)));
        }
        if (StringUtils.isNotEmpty(filterSchedule) && !filterSchedule.equals(NO_FILTER)) {
            filters.add(new ScheduleAssignmentFilter(filterSchedule));
        }
        UiFilter<PaoScheduleAssignment> filter = UiFilterList.wrap(filters);
        
        Comparator<PaoScheduleAssignment> sorter = new Comparator<PaoScheduleAssignment>() {
            @Override
            public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2) {
                return assignment1.compareTo(assignment2);
            }
        };
        
        ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
        
        List<PaoScheduleAssignment> assignments = filterService.filter(filter, sorter, rowMapper);
        
        return assignments;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {

        PropertyEditor instantEditor = datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, userContext, BlankMode.NULL);

        binder.registerCustomEditor(Instant.class, instantEditor);
    }
}