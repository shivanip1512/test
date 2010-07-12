package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentCommandFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentRowMapper;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;
import com.cannontech.yukon.cbc.CCVerifySubBus;
import com.cannontech.yukon.cbc.CapControlCommand;


@Controller
@RequestMapping("/schedule/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ScheduleController {
    private Logger log = YukonLogManager.getLogger(ScheduleController.class);
	private PaoScheduleDao paoScheduleDao = null;
	private RolePropertyDao rolePropertyDao = null;
	private FilterService filterService = null;
	private CapControlCache capControlCache = null;
	private final String NO_FILTER = "All";
	
	@RequestMapping
	public String scheduleAssignments(HttpServletRequest request, LiteYukonUser user, ModelMap mav) {        
        boolean hasEditingRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.CBC_DATABASE_EDIT, user);
	    boolean hasCapBankRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
        boolean hasSubbusRole = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
	    mav.addAttribute("hasEditingRole", hasEditingRole);
	    mav.addAttribute("hasActionRoles", hasCapBankRole && hasSubbusRole);
	    
	    mav.addAttribute("commandList", ScheduleCommand.values());
	    //verifyList is subset of command list - only verify commands
	    //these can be stopped and have "disable ovuv" checkboxes
	    mav.addAttribute("verifyCommandsList", ScheduleCommand.getVerifyCommandsList());
	    List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
		mav.addAttribute("scheduleList", schedList);
		
		//Get items per page and start index
		int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
		int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        
		//Create filters
		boolean isFiltered = false;
		String filterByCommand = ServletRequestUtils.getStringParameter(request, "command", "");
		String filterBySchedule = ServletRequestUtils.getStringParameter(request, "schedule", "");
		List<UiFilter<PaoScheduleAssignment>> filters = new ArrayList<UiFilter<PaoScheduleAssignment>>();
		if(StringUtils.isNotEmpty(filterByCommand) && !filterByCommand.equals("All")){
		    filters.add(new ScheduleAssignmentCommandFilter(ScheduleCommand.valueOf(filterByCommand)));
		    isFiltered = true;
		}
		if(StringUtils.isNotEmpty(filterBySchedule) && !filterBySchedule.equals("All")){
		    filters.add(new ScheduleAssignmentFilter(filterBySchedule));
		    isFiltered = true;
		}
		mav.addAttribute("isFiltered", isFiltered);
		UiFilter<PaoScheduleAssignment> filter = UiFilterList.wrap(filters);
		
		Comparator<PaoScheduleAssignment> sorter = new Comparator<PaoScheduleAssignment>(){
		    public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2){
		        return assignment1.compareTo(assignment2);
		    }
		};
		
		ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
		
		//Filter, sort and get search results
		SearchResult<PaoScheduleAssignment> result = 
		    filterService.filter(filter, sorter, startIndex, itemsPerPage, rowMapper);
		
        mav.addAttribute("searchResult", result);
        mav.addAttribute("itemList", result.getResultList());
        
		return "schedule/scheduleassignment.jsp";
    }
	
	@RequestMapping
    public String schedules(HttpServletRequest request, LiteYukonUser user, ModelMap mav) {
	    boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
	    mav.addAttribute("hasEditingRole", hasEditingRole);
	    List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = schedList.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        schedList = schedList.subList(startIndex, toIndex);
        
        SearchResult<PAOSchedule> result = new SearchResult<PAOSchedule>();
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
	
	/**
	 * Run multiple schedule assignment commands.  The schedule assignments are optionally 
	 * filtered by command and/or schedule.
	 */
	@RequestMapping
	public String startMultiple(HttpServletRequest request, LiteYukonUser user, ModelMap map){
        //get filter strings
	    String filterByCommand = ServletRequestUtils.getStringParameter(request, "startCommand", "");
	    String filterBySchedule = ServletRequestUtils.getStringParameter(request, "startSchedule", "");
	    
	    SearchResult<PaoScheduleAssignment> searchResult =
	        filterPaoScheduleAssignments(filterByCommand, filterBySchedule, 0, Integer.MAX_VALUE);
        
	    int numberFailed = 0;
        String result = "";
        List<PaoScheduleAssignment> resultList = searchResult.getResultList();
        for(int i = 0; i < resultList.size(); i++){
            boolean isCommandValid = true;
            PaoScheduleAssignment assignment = resultList.get(i);
            ScheduleCommand schedCommand = null;
            try{
                schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName());
            } catch(IllegalArgumentException e){
                isCommandValid = false;
                log.error("Run schedule assignment command failed.  Invalid command: " + assignment.getCommandName());
            }
            
            //VerifyNotOperatedIn command is special.  It has a time value associated
            //with it that must be parsed from the command string.  If we're processing
            //a different command, pass the default time value, which will be ignored
            long secondsNotOperatedIn = CCVerifySubBus.DEFAULT_CB_INACT_TIME;
            int commandId = -1;
            if(schedCommand == ScheduleCommand.VerifyNotOperatedIn){
                commandId = CapControlCommand.CMD_BANKS_NOT_OPERATED_IN;
                secondsNotOperatedIn = parseSecondsNotOperatedIn(assignment);
            } else {
                //deal with all other possible commands
                if(schedCommand == ScheduleCommand.ConfirmSub){
                    commandId = CapControlCommand.CONFIRM_SUB;
                } else if(schedCommand == ScheduleCommand.SendTimeSyncs){
                    commandId = CapControlCommand.SEND_TIMESYNC;
                } else if(schedCommand == ScheduleCommand.VerifyAll){
                    commandId = CapControlCommand.CMD_ALL_BANKS;
                } else if(schedCommand == ScheduleCommand.VerifyFailed){
                    commandId = CapControlCommand.CMD_FAILED_BANKS;
                } else if(schedCommand == ScheduleCommand.VerifyFailedAndQuestionable){
                    commandId = CapControlCommand.CMD_FQ_BANKS;
                } else if(schedCommand == ScheduleCommand.VerifyQuestionable){
                    commandId = CapControlCommand.CMD_QUESTIONABLE_BANKS;
                } else if(schedCommand == ScheduleCommand.VerifyStandalone){
                    commandId = CapControlCommand.CMD_STANDALONE_VERIFY;
                }
            }
            
            //if the command is valid, send it
            if(isCommandValid){
                CapControlCommandExecutor executor = new CapControlCommandExecutor(capControlCache, user);
                executor.execute(CapControlType.SUBBUS, commandId, assignment.getPaoId(), secondsNotOperatedIn, user);
            } else {
                numberFailed++;
            }
        }
        String s = (searchResult.getResultCount() - numberFailed) > 1 ? "s" : "";
        result = "Start Multiple Schedules: " + (searchResult.getResultCount() - numberFailed) + " command" + s + " sent. ";
        
        if(numberFailed > 0){
            result += "Errors: " + numberFailed;
        }
        
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
	@RequestMapping
    public String stopMultiple(HttpServletRequest request, LiteYukonUser user, ModelMap map){
        //Create filters
        String filterByCommand = ServletRequestUtils.getStringParameter(request, "stopCommand", "");
        String filterBySchedule = ServletRequestUtils.getStringParameter(request, "stopSchedule", "");
	    
        SearchResult<PaoScheduleAssignment> searchResult =
            filterPaoScheduleAssignments(filterByCommand, filterBySchedule, 0, Integer.MAX_VALUE);
        
        String result = "";
        int commandsSentCount = 0;
        List<PaoScheduleAssignment> resultList = searchResult.getResultList();
        for(int i = 0; i < resultList.size(); i++){
            boolean stopApplicable = true;
            PaoScheduleAssignment item = resultList.get(i);
            int deviceId = item.getPaoId();
            ScheduleCommand schedCommand = null;
            try{
                schedCommand = ScheduleCommand.getScheduleCommand(item.getCommandName());
                if(schedCommand == ScheduleCommand.ConfirmSub
                        || schedCommand == ScheduleCommand.SendTimeSyncs){
                    //stop is not applicable to schedules with these commands
                    stopApplicable = false;
                    log.info("Schedule assignment stop ignored.  Command: " 
                             + item.getCommandName() + " is not a verification command.");
                }
            } catch(IllegalArgumentException e){
                //invalid ScheduleCommand
                stopApplicable = false;
                log.error("Stop schedule assignment command failed.  Invalid command: " 
                          + item.getCommandName());
            }
            
            //send stop command
            if(stopApplicable){
                CapControlCommandExecutor executor = new CapControlCommandExecutor(capControlCache, user);
                executor.execute(CapControlType.SUBBUS, CapControlCommand.CMD_DISABLE_VERIFY, deviceId, user);
                commandsSentCount++;
            }    
        }
        
        String s = commandsSentCount > 1 ? "s" : "";
        result = "Stop Multiple Schedules: " + commandsSentCount + " stop command" + s + " sent.";
        
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
	@RequestMapping(method=RequestMethod.POST)
	public View startSchedule(Integer eventId, String deviceName, LiteYukonUser user, 
	                          ModelMap map) throws ServletException{
	    PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
	    boolean success = true;
	    
	    String result = "";
	    ScheduleCommand schedCommand = null;
	    try{
	        schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName()); 
	    } catch(IllegalArgumentException e){
	        success = false;
	        result = "Error - invalid command: " + assignment.getCommandName();
	        log.error("Run schedule assignment command failed.  Invalid command: " 
                      + assignment.getCommandName());
	    }
	    
	    //VerifyNotOperatedIn command is special.  It has a time value associated
	    //with it that must be parsed from the command string.  If we're processing
	    //a different command, pass the default time value, which will be ignored
	    long secondsNotOperatedIn = CCVerifySubBus.DEFAULT_CB_INACT_TIME;
        int commandId = -1;
	    if(schedCommand == ScheduleCommand.VerifyNotOperatedIn){
	        commandId = CapControlCommand.CMD_BANKS_NOT_OPERATED_IN;
	        secondsNotOperatedIn = parseSecondsNotOperatedIn(assignment);
	    } else {
    	    //Deal with all other possible commands:
    	    //get command ID
    	    if(schedCommand == ScheduleCommand.ConfirmSub){
    	        commandId = CapControlCommand.CONFIRM_SUB;
    	    } else if(schedCommand == ScheduleCommand.SendTimeSyncs){
    	        commandId = CapControlCommand.SEND_TIMESYNC;
    	    } else if(schedCommand == ScheduleCommand.VerifyAll){
    	        commandId = CapControlCommand.CMD_ALL_BANKS;
    	    } else if(schedCommand == ScheduleCommand.VerifyFailed){
    	        commandId = CapControlCommand.CMD_FAILED_BANKS;
    	    } else if(schedCommand == ScheduleCommand.VerifyFailedAndQuestionable){
    	        commandId = CapControlCommand.CMD_FQ_BANKS;
    	    } else if(schedCommand == ScheduleCommand.VerifyQuestionable){
    	        commandId = CapControlCommand.CMD_QUESTIONABLE_BANKS;
    	    } else if(schedCommand == ScheduleCommand.VerifyStandalone){
    	        commandId = CapControlCommand.CMD_STANDALONE_VERIFY;
    	    }
	    }
	    
	    //send the command
	    if(success){
	        CapControlCommandExecutor executor = new CapControlCommandExecutor(capControlCache, user);
	        executor.execute(CapControlType.SUBBUS, commandId, assignment.getPaoId(), secondsNotOperatedIn, user);
	        result = deviceName + ": '" + assignment.getCommandName() + "' sent.";
	    }
	    
	    map.addAttribute("sentCommand", assignment.getCommandName());
	    map.addAttribute("success", success);
        map.addAttribute("resultText" , result);
        return new JsonView();
	}
	
	/**
     * Send a stop verify command to the specified subbus. 
     */
	@RequestMapping(method=RequestMethod.POST)
    public View stopSchedule(Integer deviceId, String deviceName, 
                              LiteYukonUser user, ModelMap map) throws ServletException {
	    //Send stop command
	    CapControlCommandExecutor executor = new CapControlCommandExecutor(capControlCache, user);
	    executor.execute(CapControlType.SUBBUS, CapControlCommand.CMD_DISABLE_VERIFY, deviceId, user);
	    String result = deviceName + ": 'Disable Verify' sent.";
	    
	    map.addAttribute("success", true);
        map.addAttribute("resultText" , result);
	    
	    return new JsonView();
	}
	
	@RequestMapping(method=RequestMethod.POST)
    public View deleteSchedule(Integer scheduleId, ModelMap map) throws ServletException, Exception {
        boolean success = true;
        String resultString = "Schedule deleted successfully.";
        if( scheduleId == null) {
            success = false;
            resultString = "Delete failed, scheduleId was NULL";
        } else {
            success = paoScheduleDao.delete(scheduleId);
            if (!success) {
                resultString = "The schedule was not in the database. Please refresh this page.";
            }
        }
        map.addAttribute("success", success);
        map.addAttribute("resultText" , resultString);
        return new JsonView();
    }
	
    @RequestMapping(method = RequestMethod.POST)
    public View setOvUv(Integer eventId, Integer ovuv, ModelMap map) throws ServletException, Exception {
        boolean success = true;
        String resultString = "The Delete was a success.";
        if( eventId == null) {
            success = false;
            resultString = "The Delete failed, eventId was NULL";
        } else {
            try{
                PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
                assignment.setDisableOvUv(ovuv == 0 ? "Y" : "N");
                success = paoScheduleDao.updateAssignment(assignment);
                if (!success) {
                    resultString = "The Device was not in the database. Please refresh this page.";
                }
            } catch(EmptyResultDataAccessException e){
                success = false;
                resultString = "The Schedule Assignment was not in the database.  Please refresh this page.";
            }
        }
        map.addAttribute("success", success);
        map.addAttribute("resultText" , resultString);
        return new JsonView();
    }

	@RequestMapping(method=RequestMethod.POST)
	public View removePao(Integer eventId, ModelMap map) throws ServletException, Exception {
		boolean success = true;
		String resultString = "The Delete was a success.";
		if( eventId == null) {
			success = false;
			resultString = "The Delete failed, eventId was NULL";
		} else {
			success = paoScheduleDao.unassignCommandByEventId(eventId);
			if (!success) {
				resultString = "The Device was not in the database. Please refresh this page.";
			}
		}
		map.addAttribute("success", success);
		map.addAttribute("resultText" , resultString);
		return new JsonView();
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String addPao(String addSchedule, String addCommand, String filterCommand, String filterSchedule, String paoIdList, ModelMap map) throws ServletException, Exception {
		ScheduleCommand cmd = ScheduleCommand.valueOf(addCommand);
		boolean success = true;
		String resultText = "";
		
		Integer schedId = Integer.parseInt(addSchedule);
		if( addSchedule != null && cmd != null) {
			//break String into paoIds
			List<Integer> paoIds = ServletUtil.getIntegerListFromString(paoIdList);
			if(paoIds.size() == 0){
			    success = false;
			    resultText = "No device was selected.";
			}
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
				int numOfAssigns = assignments.size();
				String s = numOfAssigns > 1 ? "s" : "";
				resultText = numOfAssigns + " assignment" + s + " added successfully.";
			} catch (DataIntegrityViolationException e) {
				success = false;
				resultText = "Cannot add duplicate commands on the schedules.";
			}
		} else {
			success = false;
			resultText = "A schedule and/or command was not chosen.";
		}
		resultText = "New Assignment: " + resultText;
		
		map.addAttribute("schedule", filterSchedule);
		map.addAttribute("command", filterCommand);
		map.addAttribute("success", success);
		map.addAttribute("resultText", resultText);
		
		return "redirect:scheduleAssignments";
	}
	
	/*
	 * Helper method to parse the time variables out of a
	 * "verify not operated in..." command
	 */
	private long parseSecondsNotOperatedIn(PaoScheduleAssignment assignment){
            //parse min/hr/day/wk value from command string
            long[] minHrDayWkValues = new long[4];
            Pattern digitsPattern = Pattern.compile("\\d+");
            Matcher matcher = digitsPattern.matcher(assignment.getCommandName());
            for(int j = 0; j < 4; j++){
                matcher.find();
                String temp = matcher.group();
                minHrDayWkValues[j] = Long.parseLong(temp);
            }
            long secondsNotOperatedIn = (minHrDayWkValues[0] * 60)  //minutes
                + (minHrDayWkValues[1] * 60 * 60)                  //hours
                + (minHrDayWkValues[2] * 60 * 60 * 24)             //days
                + (minHrDayWkValues[3] * 60 * 60 * 24 * 7);        //weeks
            
            return secondsNotOperatedIn;
	}
	
    /* 
     * Helper method to simplify filtering schedule assignments.
     */
    private SearchResult<PaoScheduleAssignment> filterPaoScheduleAssignments(String filterCommand, 
                                                    String filterSchedule, int startIndex, int itemsPerPage){
        //Convert filter strings into filters
        //Filtering on "All" is equivalent to no filters
        List<UiFilter<PaoScheduleAssignment>> filters = new ArrayList<UiFilter<PaoScheduleAssignment>>();
        if(StringUtils.isNotEmpty(filterCommand) && !filterCommand.equals(NO_FILTER)){
            filters.add(new ScheduleAssignmentCommandFilter(ScheduleCommand.valueOf(filterCommand)));
        }
        if(StringUtils.isNotEmpty(filterSchedule) && !filterSchedule.equals(NO_FILTER)){
            filters.add(new ScheduleAssignmentFilter(filterSchedule));
        }
        UiFilter<PaoScheduleAssignment> filter = UiFilterList.wrap(filters);
        
        Comparator<PaoScheduleAssignment> sorter = new Comparator<PaoScheduleAssignment>(){
            public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2){
                return assignment1.compareTo(assignment2);
            }
        };
        
        ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
        
        //Filter, sort and get search results
        SearchResult<PaoScheduleAssignment> searchResult = 
            filterService.filter(filter, sorter, startIndex, itemsPerPage, rowMapper);
        
        return searchResult;
    }
	
	@Autowired
	public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
		this.paoScheduleDao = paoScheduleDao;
	}
	
	@Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
	
	@Autowired
	public void setFilterService(FilterService filterService){
	    this.filterService = filterService;
	}
	
	@Autowired
	public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
}
