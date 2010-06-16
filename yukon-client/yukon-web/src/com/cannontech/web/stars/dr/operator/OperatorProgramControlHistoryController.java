package com.cannontech.web.stars.dr.operator;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.ListMultimap;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
@Controller
public class OperatorProgramControlHistoryController {
	
    private static final String viewName = "operator/program/controlHistory/controlHistory.jsp";
    private AccountCheckerService accountCheckerService;
    private ApplianceDao applianceDao;
    private ControlHistoryDao controlHistoryDao;
    private CustomerAccountDao customerAccountDao;
    private DisplayableProgramDao displayableProgramDao;
    private ProgramDao programDao;
    private ProgramEnrollmentService programEnrollmentService;
    private ControlHistoryService controlHistoryService;
    
    @RequestMapping(value = "/operator/program/controlHistory")
    public String controlHistory(ModelMap modelMap,
    							 YukonUserContext userContext,
    							 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
                
        List<Appliance> applianceList = applianceDao.getAssignedAppliancesByAccountId(customerAccount.getAccountId());
        List<Program> programList = programDao.getByAppliances(applianceList);
        
        ListMultimap<Integer, ControlHistory> controlHistoryMap = controlHistoryDao.getControlHistory(customerAccount, applianceList, userContext, ControlPeriod.PAST_DAY);

        programEnrollmentService.removeNonEnrolledPrograms(programList, controlHistoryMap);

        boolean isNotEnrolled = programList.size() == 0;
        modelMap.addAttribute("isNotEnrolled", isNotEnrolled);
        
        if (isNotEnrolled) {
        	return viewName; // if there are no programs enrolled there is nothing more to show
        }

        Map<Integer, Duration> totalDurationMap = 
            controlHistoryService.calculateTotalDuration(controlHistoryMap);
        modelMap.addAttribute("totalDurationMap", totalDurationMap);
        
        List<DisplayableProgram> displayablePrograms = 
            displayableProgramDao.getAllControlHistorySummaryDisplayablePrograms(
                                      customerAccount, 
                                      userContext, 
                                      ControlPeriod.PAST_DAY);
        modelMap.addAttribute("displayablePrograms", displayablePrograms);
        return viewName;
    }

    @RequestMapping(value = "/operator/program/controlHistory/completeHistoryView")
    public String completeHistoryView(int programId, 
    		                          YukonUserContext yukonUserContext, 
    		                          ModelMap modelMap,
    		                          AccountInfoFragment accountInfoFragment) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        
        Program program = programDao.getByProgramId(programId);
        modelMap.addAttribute("program", program);

        ControlPeriod[] controlPeriods = ControlPeriod.values();
        modelMap.addAttribute("controlPeriods", controlPeriods);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/controlHistory/completeControlHistory.jsp";
    }
    
    @RequestMapping(value = "/operator/program/controlHistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(int programId, 
    		                               String controlPeriod, 
    		                               YukonUserContext yukonUserContext,
                                           ModelMap modelMap,
                                           AccountInfoFragment accountInfoFragment) {
    	
    	CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        Program program = programDao.getByProgramId(programId);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        DisplayableProgram displayableProgram = displayableProgramDao.getDisplayableProgram(customerAccount, yukonUserContext, program, controlPeriodEnum);
        modelMap.addAttribute("displayableControlHistoryMap", displayableProgram.getDisplayableControlHistoryList());
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/controlHistory/innerCompleteControlHistory.jsp";
    }
    
    @Autowired
    public void setAccountCheckerService(AccountCheckerService accountCheckerService) {
        this.accountCheckerService = accountCheckerService;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setControlHistoryDao(ControlHistoryDao controlHistoryDao) {
        this.controlHistoryDao = controlHistoryDao;
    }

    @Autowired
    public void setControlHistoryService(ControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setDisplayableProgramDao(DisplayableProgramDao displayableProgramDao) {
        this.displayableProgramDao = displayableProgramDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setProgramEnrollmentService(ProgramEnrollmentService programEnrollmentService) {
        this.programEnrollmentService = programEnrollmentService;
    }

}
