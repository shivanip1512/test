package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
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
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
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
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentCommandFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentRowMapper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;


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
	
	private final String NO_FILTER = "All";
	
	public ScheduleController() {
	    PeriodFormatterBuilder builder = new PeriodFormatterBuilder()
	    .appendMinutes().appendLiteral(" min ")
	    .appendHours().appendLiteral(" hr ")
	    .appendDays().appendLiteral(" day ")
	    .appendWeeks().appendLiteral(" wk");
	    periodFormatter = builder.toFormatter();
	}
	
	@RequestMapping("scheduleAssignments")
	public String scheduleAssignments(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletRequestBindingException {
	    
        boolean hasEditingRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.CBC_DATABASE_EDIT, user);
	    boolean hasCapBankRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
        boolean hasSubbusRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
	    model.addAttribute("hasEditingRole", hasEditingRole);
	    model.addAttribute("hasActionRoles", hasCapBankRole && hasSubbusRole);
	    
		//Get items per page and start index
        int itemsPerPage = CtiUtilities.itemsPerPage(ServletRequestUtils.getIntParameter(request, "itemsPerPage"));
		int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        
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
		    public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2) {
		        return assignment1.compareTo(assignment2);
		    }
		};
		
		ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
		
		//Filter, sort and get search results
		SearchResults<PaoScheduleAssignment> result = 
		    filterService.filter(filter, sorter, startIndex, itemsPerPage, rowMapper);
		
        model.addAttribute("searchResult", result);
        model.addAttribute("itemList", result.getResultList());
        model.addAttribute("commandList", ScheduleCommand.values());
        model.addAttribute("verifyCommandsList", ScheduleCommand.getVerifyCommandsList());
        List<PAOSchedule> schedules = paoScheduleDao.getAllPaoScheduleNames(); 
        Collections.sort(schedules);
        model.addAttribute("scheduleList", schedules);
        
		return "schedule/scheduleassignment.jsp";
    }
	
	@RequestMapping("schedules")
    public String schedules(HttpServletRequest request, LiteYukonUser user, ModelMap mav) throws ServletRequestBindingException {
	    boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
	    mav.addAttribute("hasEditingRole", hasEditingRole);
	    List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
        
        int itemsPerPage = CtiUtilities.itemsPerPage(ServletRequestUtils.getIntParameter(request, "itemsPerPage"));
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = schedList.size();
        
        if (numberOfResults < toIndex) toIndex = numberOfResults;
        schedList = schedList.subList(startIndex, toIndex);
        
        SearchResults<PAOSchedule> result = new SearchResults<PAOSchedule>();
        result.setResultList(schedList);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        mav.addAttribute("searchResult", result);
        mav.addAttribute("scheduleList", result.getResultList());
        
        long startOfTime = CtiUtilities.get1990GregCalendar().getTime().getTime();
        mav.addAttribute("startOfTime", startOfTime);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "schedule/schedules.jsp";
    }
	
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
                command = CommandHelper.buildVerifyInactiveBanks(user, CommandType.VERIFY_INACTIVE_BANKS, assignment.getPaoId(), false, secondsNotOperatedIn);
            } else if (ScheduleCommand.getVerifyCommandsList().contains(schedCommand)) {
                command = CommandHelper.buildVerifyBanks(user, CommandType.getForId(schedCommand.getCapControlCommand()), assignment.getPaoId(), false);
            } else {
                command = CommandHelper.buildItemCommand(schedCommand.getCapControlCommand(), assignment.getPaoId(), user);
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
	public String startMultiple(HttpServletRequest request, YukonUserContext context, ModelMap map) {
	    String filterByCommand = ServletRequestUtils.getStringParameter(request, "startCommand", "");
	    String filterBySchedule = ServletRequestUtils.getStringParameter(request, "startSchedule", "");
	    
	    SearchResults<PaoScheduleAssignment> searchResult = 
	        filterPaoScheduleAssignments(filterByCommand, filterBySchedule, 0);
        
	    int numberFailed = 0;
        String result = "";
        
        for (PaoScheduleAssignment assignment : searchResult.getResultList()) {
            boolean success = executeScheduleCommand(assignment, context.getYukonUser());
            if (!success) numberFailed++;
        }
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.startedSchedules", searchResult.getResultCount() - numberFailed, numberFailed);
        
        //if at least one command succeeded, consider it a success
        boolean success = (searchResult.getResultCount() - numberFailed) > 0;
        map.addAttribute("success", success);
        map.addAttribute("resultText" , result);
        map.addAttribute("schedule", filterBySchedule);
        map.addAttribute("command", filterByCommand);
        
	    return "redirect:scheduleAssignments";
	}
	
	/**
     * Stop multiple schedule assignment commands.  The schedule assignments are optionally 
     * filtered by command and/or schedule.  This is only applicable to "verify" commands.  
     * Any other schedule assignment commands that match the filter criteria will be ignored. 
     */
	@RequestMapping("stopMultiple")
    public String stopMultiple(HttpServletRequest request, YukonUserContext context, ModelMap map) {
        String filterByCommand = ServletRequestUtils.getStringParameter(request, "stopCommand", "");
        String filterBySchedule = ServletRequestUtils.getStringParameter(request, "stopSchedule", "");
	    
        SearchResults<PaoScheduleAssignment> searchResult =
            filterPaoScheduleAssignments(filterByCommand, filterBySchedule, 0);
        
        String result = "";
        int commandsSentCount = 0;
        
        for (PaoScheduleAssignment assignment : searchResult.getResultList()) {
            
            boolean stopApplicable = true;
            int deviceId = assignment.getPaoId();
            ScheduleCommand schedCommand = null;
            
            try {
                schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName());
                if (schedCommand == ScheduleCommand.ConfirmSub || schedCommand == ScheduleCommand.SendTimeSyncs) {
                    //stop is not applicable to schedules with these commands
                    stopApplicable = false;
                    log.info("Schedule assignment stop ignored.  Command: " + assignment.getCommandName() + " is not a verification command.");
                }
            } catch(IllegalArgumentException e) {
                //invalid ScheduleCommand
                stopApplicable = false;
                log.error("Stop schedule assignment command failed.  Invalid command: " + assignment.getCommandName());
            }
            
            //send stop command
            if (stopApplicable) {
                VerifyBanks command = CommandHelper.buildStopVerifyBanks(context.getYukonUser(), CommandType.STOP_VERIFICATION, deviceId);
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
        boolean success = commandsSentCount > 0;
        map.addAttribute("success", success);
        map.addAttribute("resultText" , result);
        map.addAttribute("schedule", filterBySchedule);
        map.addAttribute("command", filterByCommand);
        
	    return "redirect:scheduleAssignments";
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
	        result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.startedScheduleSuccess", deviceName, assignment.getCommandName());
	    } else {
	        result = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.startedScheduleFailed", assignment.getCommandName());
	    }

	    Map<String, Object> json = Maps.newHashMapWithExpectedSize(3);
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
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
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
	
    @RequestMapping(value="setOvUv", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> setOvUv(Integer eventId, Integer ovuv, ModelMap map, YukonUserContext context) {
        boolean success = false;
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(3);
        try {
            PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
            assignment.setDisableOvUv(ovuv == 0 ? "Y" : "N");
            success = paoScheduleDao.updateAssignment(assignment);
        } catch (EmptyResultDataAccessException e) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
            String resultText = accessor.getMessage("yukon.web.modules.capcontrol.scheduleAssignments.setOvUvFailed");
            map.addAttribute("resultText" , resultText);
        }
        map.addAttribute("success", success);
        return json;
    }

	@RequestMapping(value="addPao", method=RequestMethod.POST)
	public String addPao(String addSchedule, String addCommand, String filterCommand, String filterSchedule, String paoIdList, ModelMap map, FlashScope flash) {
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
		
		map.addAttribute("schedule", filterSchedule);
		map.addAttribute("command", filterCommand);
		
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
        String timeString = assignment.getCommandName().replaceAll(ScheduleCommand.VerifyNotOperatedIn.getCommandName() + " ", "");
    
        //parse min/hr/day/wk value from command string
        Period period = periodFormatter.parsePeriod(timeString);
        return period.toStandardSeconds().getSeconds();
	}
	
    /* 
     * Helper method to simplify filtering schedule assignments.
     */
    private SearchResults<PaoScheduleAssignment> filterPaoScheduleAssignments(String filterCommand, 
                                                    String filterSchedule, int startIndex) {
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
            public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2) {
                return assignment1.compareTo(assignment2);
            }
        };
        
        ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
        
        //Filter, sort and get search results
        SearchResults<PaoScheduleAssignment> searchResult = 
            filterService.filter(filter, sorter, startIndex, Integer.MAX_VALUE, rowMapper);
        
        return searchResult;
    }
}