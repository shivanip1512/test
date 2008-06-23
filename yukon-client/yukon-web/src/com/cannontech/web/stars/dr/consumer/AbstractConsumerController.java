package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableInventoryDao;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableProgramDao;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableScheduledOptOutDao;

public abstract class AbstractConsumerController {
    protected YukonUserContextMessageSourceResolver messageSourceResolver;
    protected CustomerAccountDao customerAccountDao;
    protected ApplianceDao applianceDao;
    protected ProgramDao programDao;
    protected ProgramService programService;
    protected ProgramEnrollmentService programEnrollmentService;
    protected InventoryBaseDao inventoryBaseDao;
    protected DisplayableProgramDao displayableProgramDao;
    protected DisplayableScheduledOptOutDao displayableScheduledOptOutDao;
    protected DisplayableInventoryDao displayableInventoryDao;
    protected ControlHistoryDao controlHistoryDao;
    protected AccountCheckerService accountCheckerService;
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        List<CustomerAccount> accountList = customerAccountDao.getByUser(user);
        CustomerAccount account = accountList.get(0);
        return account;
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
    public void setDisplayableScheduledOptOutDao(
            DisplayableScheduledOptOutDao displayableScheduledOptOutDao) {
        this.displayableScheduledOptOutDao = displayableScheduledOptOutDao;
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
    
}
