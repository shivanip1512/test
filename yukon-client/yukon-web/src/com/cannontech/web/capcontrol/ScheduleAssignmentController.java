package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;


@Controller
@RequestMapping("/scheduleAssignments/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ScheduleAssignmentController {
    
	private PaoScheduleDao paoScheduleDao = null;
	
	@RequestMapping
	public String schedule(ModelMap mav) {
                
        List<PAOSchedule> schedList = paoScheduleDao.getAllPaoScheduleNames();
		mav.addAttribute("scheduleList",schedList);
        
        mav.addAttribute("commandList", ScheduleCommand.values());
		
		List<PaoScheduleAssignment> paosOnSchedule = paoScheduleDao.getAllScheduleAssignments();
		Collections.sort(paosOnSchedule);
		mav.addAttribute("itemList",paosOnSchedule);
        
		return "scheduleassignment";
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
		
		return "redirect:schedule";
	}

	@Autowired
	public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
		this.paoScheduleDao = paoScheduleDao;
	}
}
