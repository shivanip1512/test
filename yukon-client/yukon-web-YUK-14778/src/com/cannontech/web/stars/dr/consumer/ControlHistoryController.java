package com.cannontech.web.stars.dr.consumer;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlHistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlHistory.service.StarsControlHistoryService;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableGroupedControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ListMultimap;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
@Controller
public class ControlHistoryController extends AbstractConsumerController {
    private static final String viewNamePrefix = "consumer/controlhistory/";

    @Autowired private StarsControlHistoryService controlHistoryService;
    @Autowired private ControlHistoryDao controlHistoryDao;
    @Autowired private ProgramEnrollmentService programEnrollmentService;
    @Autowired private ProgramDao programDao;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private DisplayableProgramDao displayableProgramDao;
    @Autowired private AccountCheckerService accountCheckerService;
    @Autowired private RolePropertyDao rolePropertyDao;

    @RequestMapping(value = "/consumer/controlhistory", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        List<Appliance> applianceList = applianceDao.getAssignedAppliancesByAccountId(customerAccount.getAccountId());
        List<Program> programList = programDao.getByAppliances(applianceList);
        
        ListMultimap<Integer, ControlHistory> controlHistoryMap = 
                controlHistoryDao.getControlHistory(customerAccount.getAccountId(), yukonUserContext, ControlPeriod.PAST_DAY, false);

        programEnrollmentService.removeNonEnrolledPrograms(programList, controlHistoryMap);

        boolean isNotEnrolled = programList.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);
        
        if (isNotEnrolled) return viewNamePrefix + "controlHistory.jsp"; // if there are no programs enrolled there is nothing more to show

        Map<Integer, Duration> totalDurationMap = 
            controlHistoryService.calculateTotalDuration(controlHistoryMap);
        map.addAttribute("totalDurationMap", totalDurationMap);
        
        List<DisplayableProgram> displayablePrograms = 
            displayableProgramDao.getAllControlHistorySummary(
                                      customerAccount.getAccountId(), 
                                      yukonUserContext, 
                                      ControlPeriod.PAST_DAY, false);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        return viewNamePrefix + "controlHistory.jsp";
    }
    
    @RequestMapping(value = "/consumer/controlhistory/completeHistoryView", method = RequestMethod.GET)
    public String completeHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, YukonUserContext yukonUserContext, ModelMap map) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        
        Program program = programDao.getByProgramId(programId);
        map.addAttribute("program", program);

        ListMultimap<Integer, ControlHistory> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount.getAccountId(), yukonUserContext, ControlPeriod.PAST_DAY, false);
        
        List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
        
        Duration totalDuration = controlHistoryService.calculateTotalDuration(controlHistoryList);
        map.addAttribute("totalDuration", totalDuration);

        ControlPeriod[] controlPeriods = ControlPeriod.values();
        map.addAttribute("controlPeriods", controlPeriods);
        
        return viewNamePrefix + "completeControlHistory.jsp";
    }
    
    @RequestMapping(value = "/consumer/controlhistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            int programId, String controlPeriod, YukonUserContext yukonUserContext,
            ModelMap map) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        boolean showGroupedHistory = 
                rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_GROUPED_CONTROL_HISTORY_DISPLAY,
                                                        yukonUserContext.getYukonUser());
        accountCheckerService.checkProgram(user, programId);
        Program program = programDao.getByProgramId(programId);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        DisplayableProgram displayableProgram = 
            displayableProgramDao.getDisplayableProgram(customerAccount.getAccountId(), yukonUserContext, program, controlPeriodEnum, false);
        List<DisplayableControlHistory> controlHistoryList = displayableProgram.getDisplayableControlHistoryList();
        List<DisplayableGroupedControlHistory> displayableGroupedControlHistory = displayableProgramDao.getDisplayableGroupedControlHistory(controlHistoryList);
        
        map.addAttribute("groupedControlHistory", displayableGroupedControlHistory);
        map.addAttribute("showGroupedHistory", showGroupedHistory);
        
        return viewNamePrefix + "innerCompleteControlHistory.jsp";
    }
}
