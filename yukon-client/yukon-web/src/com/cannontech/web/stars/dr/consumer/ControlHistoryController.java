package com.cannontech.web.stars.dr.consumer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ListMultimap;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
@Controller
public class ControlHistoryController extends AbstractConsumerController {
    private static final String viewName = "consumer/controlhistory/controlHistory.jsp";
    private ControlHistoryService controlHistoryService;
    
    @RequestMapping(value = "/consumer/controlhistory", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext userContext, ModelMap map) {
        
        List<Appliance> applianceList = applianceDao.getAssignedAppliancesByAccountId(customerAccount.getAccountId());
        List<Program> programList = programDao.getByAppliances(applianceList);
        
        ListMultimap<Integer, ControlHistory> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, userContext, ControlPeriod.PAST_DAY);

        programEnrollmentService.removeNonEnrolledPrograms(programList, controlHistoryMap);

        boolean isNotEnrolled = programList.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);
        
        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show

        Map<Integer, Integer> totalDurationMap = 
            controlHistoryService.calculateTotalDuration(controlHistoryMap);
        map.addAttribute("totalDurationMap", totalDurationMap);
        
        List<DisplayableProgram> displayablePrograms = 
            displayableProgramDao.getAllControlHistorySummaryDisplayablePrograms(
                                      customerAccount, 
                                      userContext, 
                                      ControlPeriod.PAST_DAY);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        return viewName;
    }
    
    @RequestMapping(value = "/consumer/controlhistory/completeHistoryView", method = RequestMethod.GET)
    public String completeHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, YukonUserContext yukonUserContext, ModelMap map) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        
        List<Appliance> applianceList = applianceDao.getAssignedAppliancesByAccountId(customerAccount.getAccountId());

        Program program = programDao.getByProgramId(programId);
        map.addAttribute("program", program);

        ListMultimap<Integer, ControlHistory> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, yukonUserContext, ControlPeriod.PAST_DAY);
        
        List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
        
        int totalDuration = controlHistoryService.calculateTotalDuration(controlHistoryList);
        map.addAttribute("totalDuration", totalDuration);

        ControlPeriod[] controlPeriods = ControlPeriod.values();
        map.addAttribute("controlPeriods", controlPeriods);
        
        return "consumer/controlhistory/completeControlHistory.jsp";
    }
    
    @RequestMapping(value = "/consumer/controlhistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, String controlPeriod, YukonUserContext yukonUserContext,
            ModelMap map) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        Program program = programDao.getByProgramId(programId);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        DisplayableProgram displayableProgram = 
            displayableProgramDao.getDisplayableProgram(customerAccount, yukonUserContext, program, controlPeriodEnum);
        map.addAttribute("displayableControlHistoryMap", displayableProgram.getDisplayableControlHistoryList());
        
        return "consumer/controlhistory/innerCompleteControlHistory.jsp";
    }
    
    @Autowired
    public void setControlHistoryService(
            ControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }
    
}
