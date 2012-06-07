package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.controlHistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;

public abstract class AbstractConsumerController {
    protected YukonUserContextMessageSourceResolver messageSourceResolver;
    protected CustomerAccountDao customerAccountDao;
    protected ApplianceDao applianceDao;
    protected ProgramDao programDao;
    protected ProgramService programService;
    protected ProgramEnrollmentService programEnrollmentService;
    protected InventoryBaseDao inventoryBaseDao;
    protected DisplayableProgramDao displayableProgramDao;
    protected DisplayableInventoryDao displayableInventoryDao;
    protected ControlHistoryDao controlHistoryDao;
    protected AccountCheckerService accountCheckerService;
    protected OptOutEventDao optOutEventDao;
    protected RolePropertyDao rolePropertyDao;
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        CustomerAccount customerAccount = customerAccountDao.getCustomerAccount(user);
        return customerAccount;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    @Autowired
    public void setProgramEnrollmentService(
            ProgramEnrollmentService programEnrollmentService) {
        this.programEnrollmentService = programEnrollmentService;
    }

    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryDao) {
        this.inventoryBaseDao = inventoryDao;
    }
    
    @Autowired
    public void setDisplayableProgramDao(DisplayableProgramDao displayableProgramDao) {
        this.displayableProgramDao = displayableProgramDao;
    }
    
    @Autowired
    public void setControlHistoryDao(ControlHistoryDao controlHistoryDao) {
        this.controlHistoryDao = controlHistoryDao;
    }
    
    @Autowired
    public void setAccountCheckerService(
            AccountCheckerService accountCheckerService) {
        this.accountCheckerService = accountCheckerService;
    }
    
    @Autowired
    public void setDisplayableInventoryDao(
            DisplayableInventoryDao displayableInventoryDao) {
        this.displayableInventoryDao = displayableInventoryDao;
    }
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
