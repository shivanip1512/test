package com.cannontech.web.stars.dr.operator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)
@Controller
public class OperatorProgramControlHistoryController {
	
    private static final String viewName = "operator/program/controlHistory/controlHistory.jsp";
    private AccountCheckerService accountCheckerService;
    private DisplayableProgramDao displayableProgramDao;
    private ProgramDao programDao;
    private EnrollmentDao enrollmentDao;
    
    @RequestMapping(value = "/operator/program/controlHistory")
    public String controlHistory(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        int accountId = accountInfoFragment.getAccountId();
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        boolean isNotEnrolled = !enrollmentDao.isAccountEnrolled(accountId);
        modelMap.addAttribute("isNotEnrolled", isNotEnrolled);
        
        /* Get control history for previous enrollments */
        List<DisplayableProgram> previousControlHistory = 
            displayableProgramDao.getAllControlHistorySummary(accountId, userContext, ControlPeriod.PAST_YEAR, true);
        modelMap.addAttribute("previousControlHistory", previousControlHistory);
        
        if (isNotEnrolled) {
        	return viewName; /* If there are no programs enrolled, skip retrieving the current enrollment control history. */
        }

        List<DisplayableProgram> currentControlHistory = 
            displayableProgramDao.getAllControlHistorySummary(accountId, userContext, ControlPeriod.PAST_YEAR, false);
        modelMap.addAttribute("currentControlHistory", currentControlHistory);
        
        return viewName;
    }

    @RequestMapping(value = "/operator/program/controlHistory/completeHistoryView")
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
    
    @RequestMapping(value = "/operator/program/controlHistory/innerCompleteHistoryView")
    public String innerCompleteHistoryView(int programId, 
    		                               String controlPeriod, 
    		                               YukonUserContext yukonUserContext,
                                           ModelMap modelMap,
                                           AccountInfoFragment accountInfoFragment,
                                           boolean past) {
    	
        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkProgram(user, programId);
        Program program = programDao.getByProgramId(programId);
        
        ControlPeriod controlPeriodEnum = ControlPeriod.valueOf(controlPeriod);
        
        DisplayableProgram displayableProgram = displayableProgramDao.getDisplayableProgram(accountInfoFragment.getAccountId(), yukonUserContext, program, controlPeriodEnum, past);
        modelMap.addAttribute("displayableControlHistoryMap", displayableProgram.getDisplayableControlHistoryList());
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/controlHistory/innerCompleteControlHistory.jsp";
    }
    
    @Autowired
    public void setAccountCheckerService(AccountCheckerService accountCheckerService) {
        this.accountCheckerService = accountCheckerService;
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
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

}