package com.cannontech.web.stars.dr.consumer;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.servlet.YukonUserContextUtils;
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
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableOptOut;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableProgram;

@Controller
public class ControlHistoryController extends AbstractConsumerController {
    private static final String viewName = "consumer/controlhistory/controlHistory.jsp";
    private ControlHistoryService controlHistoryService;
    private ControlHistoryEventDao controlHistoryEventDao;
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
    @RequestMapping(value = "/consumer/controlhistory", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            HttpServletRequest request, HttpServletResponse response, ModelMap map) {

        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        
        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccount.getAccountId());
        List<Program> programs = programDao.getByAppliances(applianceList);
        
        Map<Integer, List<ControlHistory>> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, yukonUserContext);

        programEnrollmentService.removeNonEnrolledPrograms(programs, controlHistoryMap);

        boolean isNotEnrolled = programs.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);
        
        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show

        Map<Integer, Integer> totalDurationMap = controlHistoryService.calculateTotalDuration(controlHistoryMap);
        map.addAttribute("totalDurationMap", totalDurationMap);
        
        List<DisplayableProgram> displayablePrograms = displayableProgramDao.getAllDisplayablePrograms(customerAccount, yukonUserContext);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        DisplayableOptOut displayableOptOut = displayableOptOutDao.getDisplayableOptOut(customerAccount, yukonUserContext);
        map.addAttribute("displayableOptOut", displayableOptOut);
        
        return viewName;
    }
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
    @RequestMapping(value = "/consumer/controlhistory/completeHistoryView", method = RequestMethod.GET)
    public String completeHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        
        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccount.getAccountId());

        Program program = programDao.getByProgramId(programId);
        checkProgramAccess(customerAccount, program);
        map.addAttribute("program", program);
        
        Map<Integer, List<ControlHistory>> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, yukonUserContext);
        
        List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
        
        int totalDuration = controlHistoryService.calculateTotalDuration(controlHistoryList);
        map.addAttribute("totalDuration", totalDuration);

        return "consumer/controlhistory/completeControlHistory.jsp";
    }
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
    @RequestMapping(value = "/consumer/controlhistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, String controlPeriod, HttpServletRequest request, 
            HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        
        Program program = programDao.getByProgramId(programId);
        checkProgramAccess(customerAccount, program);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        Map<String, List<ControlHistoryEvent>> controlHistoryEventMap = 
            controlHistoryEventDao.getEventsByProgram(customerAccount.getAccountId(),
                                                      programId,
                                                      controlPeriodEnum,
                                                      yukonUserContext);
        map.addAttribute("controlHistoryEventMap", controlHistoryEventMap);
        
        return "consumer/controlhistory/innerCompleteControlHistory.jsp";
    }
    
    private void checkProgramAccess(CustomerAccount customerAccount, Program program) throws NotAuthorizedException {
        boolean hasProgramAccess = programService.hasProgramAccess(customerAccount, program);
        if (hasProgramAccess) return;
        throw new NotAuthorizedException("CustomerAccount " + customerAccount.getAccountId() +
                                         " not authorized for Program " + program.getProgramId());
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
