package com.cannontech.web.stars.dr.consumer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableScheduledOptOut;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableProgram;

@CheckRole(ResidentialCustomerRole.ROLEID)
@CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
@Controller
public class ControlHistoryController extends AbstractConsumerController {
    private static final String viewName = "consumer/controlhistory/controlHistory.jsp";
    private ControlHistoryService controlHistoryService;
    private ControlHistoryEventDao controlHistoryEventDao;
    
    @RequestMapping(value = "/consumer/controlhistory", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        
        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccount.getAccountId());
        List<Program> programs = programDao.getByAppliances(applianceList);
        
        Map<Integer, List<ControlHistory>> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, yukonUserContext);

        programEnrollmentService.removeNonEnrolledPrograms(programs, controlHistoryMap);

        boolean isNotEnrolled = programs.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);
        
        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show

        Map<Integer, Integer> totalDurationMap = 
            controlHistoryService.calculateTotalDuration(controlHistoryMap);
        map.addAttribute("totalDurationMap", totalDurationMap);
        
        List<DisplayableProgram> displayablePrograms = 
            displayableProgramDao.getAllDisplayablePrograms(customerAccount, yukonUserContext);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        DisplayableScheduledOptOut displayableOptOut = 
            displayableScheduledOptOutDao.getLastDisplayableScheduledOptOut(customerAccount,
                                                                   yukonUserContext);
        map.addAttribute("displayableOptOut", displayableOptOut);
        
        return viewName;
    }
    
    @RequestMapping(value = "/consumer/controlhistory/completeHistoryView", method = RequestMethod.GET)
    public String completeHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, YukonUserContext yukonUserContext, ModelMap map) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        
        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccount.getAccountId());

        Program program = programDao.getByProgramId(programId);
        map.addAttribute("program", program);
        
        Map<Integer, List<ControlHistory>> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, yukonUserContext);
        
        List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
        
        int totalDuration = controlHistoryService.calculateTotalDuration(controlHistoryList);
        map.addAttribute("totalDuration", totalDuration);

        return "consumer/controlhistory/completeControlHistory.jsp";
    }
    
    @RequestMapping(value = "/consumer/controlhistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, String controlPeriod, YukonUserContext yukonUserContext,
            ModelMap map) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        Map<String, List<ControlHistoryEvent>> controlHistoryEventMap = 
            controlHistoryEventDao.getEventsByProgram(customerAccount.getAccountId(),
                                                      programId,
                                                      controlPeriodEnum,
                                                      yukonUserContext);
        map.addAttribute("controlHistoryEventMap", controlHistoryEventMap);
        
        return "consumer/controlhistory/innerCompleteControlHistory.jsp";
    }
    
    @Autowired
    public void setControlHistoryService(
            ControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }
    
    @Autowired
    public void setControlHistoryEventDao(
            ControlHistoryEventDao controlHistoryEventDao) {
        this.controlHistoryEventDao = controlHistoryEventDao;
    }
    
}
