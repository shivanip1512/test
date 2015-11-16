package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlHistory.service.ControlHistoryEventService;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableGroupedControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
@RequestMapping(value ="/operator/program")
@Controller
public class OperatorProgramControlHistoryController {
	
    @Autowired private AccountCheckerService accountCheckerService;
    @Autowired private ControlHistoryEventService controlHistoryEventService;
    @Autowired private DisplayableProgramDao displayableProgramDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private ProgramDao programDao;
    
    @RequestMapping(value = "/controlHistory")
    public String controlHistory(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        int accountId = accountInfoFragment.getAccountId();
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        return "operator/program/controlHistory/controlHistory.jsp";
    }
    
    @RequestMapping(value = "/controlHistory/pastEnrollment")
    public String pastEnrollmentControlHistory(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        int accountId = accountInfoFragment.getAccountId();
        modelMap.addAttribute("accountId", accountId);
        
        // Get control history for previous enrollments.
        List<DisplayableProgram> previousControlHistory = 
            displayableProgramDao.getAllControlHistorySummary(accountId, userContext, ControlPeriod.PAST_YEAR, true);
        modelMap.addAttribute("previousControlHistory", previousControlHistory);
        
        return "operator/program/controlHistory/pastEnrollmentControlHistory.jsp";
    }

    @RequestMapping(value = "/controlHistory/currentEnrollment")
    public String currentEnrollmentControlHistory(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        int accountId = accountInfoFragment.getAccountId();
        boolean isNotEnrolled = !enrollmentDao.isAccountEnrolled(accountId);
        modelMap.addAttribute("isNotEnrolled", isNotEnrolled);
        modelMap.addAttribute("accountId", accountId);
        
        if (isNotEnrolled) {
            // If there are no programs enrolled, skip retrieving the current enrollment control history.
            return "operator/program/controlHistory/currentEnrollmentControlHistory.jsp"; 
        }
        List<DisplayableProgram> currentControlHistory = 
            displayableProgramDao.getAllControlHistorySummary(accountId, userContext, ControlPeriod.PAST_YEAR, false);
        modelMap.addAttribute("currentControlHistory", currentControlHistory);
        
        return "operator/program/controlHistory/currentEnrollmentControlHistory.jsp";
    }

    @RequestMapping(value = "/controlHistory/completeHistoryView")
    public String completeHistoryView(int programId, 
    		                          YukonUserContext yukonUserContext, 
    		                          ModelMap modelMap,
    		                          AccountInfoFragment accountInfoFragment,
    		                          boolean past) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        
        Program program = programDao.getByProgramId(programId);
        modelMap.addAttribute("program", program);

        ControlPeriod[] controlPeriods = ControlPeriod.values();
        modelMap.addAttribute("controlPeriods", controlPeriods);
        
        modelMap.addAttribute("past", past);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/controlHistory/completeControlHistory.jsp";
    }
    
    @RequestMapping(value = "/controlHistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(int programId, 
    		                               String controlPeriod, 
    		                               YukonUserContext yukonUserContext,
                                           ModelMap modelMap,
                                           AccountInfoFragment accountInfoFragment,
                                           boolean past) {
    	
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        Program program = programDao.getByProgramId(programId);
        modelMap.addAttribute("programId", programId);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        DisplayableProgram displayableProgram = displayableProgramDao.getDisplayableProgram(accountInfoFragment.getAccountId(), yukonUserContext, program, controlPeriodEnum, past);
        
        List<DisplayableControlHistory> controlHistoryList = displayableProgram.getDisplayableControlHistoryList();
        List<DisplayableGroupedControlHistory> displayableGroupedControlHistory = displayableProgramDao.getDisplayableGroupedControlHistory(controlHistoryList);
        
        modelMap.addAttribute("groupedControlHistory", displayableGroupedControlHistory);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/controlHistory/innerCompleteControlHistory.jsp";
    }
    
    @RequestMapping(value = "/controlHistory/controlHistoryEventGearName",
            method=RequestMethod.GET)
    public @ResponseBody List<String> controlHistoryEventDetail(
            @RequestParam(value="programId") int programId, 
            @RequestParam(value="startDates") String[] startDates, 
            @RequestParam(value="endDates") String[] endDates, 
            YukonUserContext userContext, 
            ModelMap modelMap) throws JsonProcessingException {
        List<DateTime> eventStartDates = new ArrayList<>();
        List<DateTime> eventEndDates = new ArrayList<>();
        
        ArrayList<String> startDatesList = new ArrayList<>(Arrays.asList(startDates));
        for (String startDate : startDatesList) {
            eventStartDates.add(new DateTime(Long.parseLong(startDate)));
        }
        ArrayList<String> endDatesList = new ArrayList<>(Arrays.asList(endDates));
        for (String endDate : endDatesList) {
            eventEndDates.add(new DateTime(Long.parseLong(endDate)));
        }
        
        List<String> gearNames = controlHistoryEventService.getHistoricalGearNames(
                programId, eventStartDates, eventEndDates, userContext);
        return gearNames;
    }
    
} 