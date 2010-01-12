package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;


@Controller
@RequestMapping("/schedule/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ScheduleController {
    
	private PaoScheduleDao paoScheduleDao = null;
	private RolePropertyDao rolePropertyDao = null;
	
	@RequestMapping
	public String scheduleAssignments(ModelMap mav) {
                
        List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
		mav.addAttribute("scheduleList",schedList);
        
        mav.addAttribute("commandList", ScheduleCommand.values());
		
		List<PaoScheduleAssignment> paosOnSchedule = paoScheduleDao.getAllScheduleAssignments();
		Collections.sort(paosOnSchedule);
		mav.addAttribute("itemList",paosOnSchedule);
        
		return "schedule/scheduleassignment.jsp";
    }
	
	@RequestMapping
    public String schedules(HttpServletRequest request, LiteYukonUser user, ModelMap mav) {
	    boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
	    mav.addAttribute("hasEditingRole", hasEditingRole);
        List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
        Collections.sort(schedList);
        mav.addAttribute("scheduleList",schedList);
        
        long startOfTime = CtiUtilities.get1990GregCalendar().getTime().getTime();
        mav.addAttribute("startOfTime", startOfTime);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "schedule/schedules.jsp";
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
            PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
            assignment.setDisableOvUv(ovuv == 0 ? "Y" : "N");
            success = paoScheduleDao.updateAssignment(assignment);
            if (!success) {
                resultString = "The Device was not in the database. Please refresh this page.";
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
	public String addPao(String scheduleSelection, String paoIdList, String commandSelection, ModelMap map) throws ServletException, Exception {
		ScheduleCommand cmd = ScheduleCommand.valueOf(commandSelection);
		boolean failed = false;
		String failedReason = "";
		
		Integer schedId = Integer.parseInt(scheduleSelection);
		if( scheduleSelection != null && cmd != null) {
			//break String into paoIds
			List<Integer> paoIds = ServletUtil.getIntegerListFromString(paoIdList);
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
			} catch (DataIntegrityViolationException e) {
				failed = true;
				failedReason = "Cannot add duplicate commands on the schedules.";
			}
		} else {
			failed = true;
			failedReason = "A schedule and/or command was not chosen.";
		}
		
		map.addAttribute("defSchedule", scheduleSelection);
		map.addAttribute("defCommand", cmd.toString());
		map.addAttribute("failed", failed);
		
		if (failed) {
			map.addAttribute("failedReason", failedReason);
		}
		
		return "redirect:scheduleAssignments";
	}

	@Autowired
	public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
		this.paoScheduleDao = paoScheduleDao;
	}
	
	@Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
