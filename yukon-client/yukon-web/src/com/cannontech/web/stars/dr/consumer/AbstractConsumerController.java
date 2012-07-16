package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.controlHistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;

public abstract class AbstractConsumerController {
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired protected CustomerAccountDao customerAccountDao;
    @Autowired protected ApplianceDao applianceDao;
    @Autowired protected ProgramDao programDao;
    @Autowired protected ProgramService programService;
    @Autowired protected ProgramEnrollmentService programEnrollmentService;
    @Autowired protected InventoryBaseDao inventoryBaseDao;
    @Autowired protected DisplayableProgramDao displayableProgramDao;
    @Autowired protected DisplayableInventoryDao displayableInventoryDao;
    @Autowired protected ControlHistoryDao controlHistoryDao;
    @Autowired protected AccountCheckerService accountCheckerService;
    @Autowired protected OptOutEventDao optOutEventDao;
    @Autowired protected RolePropertyDao rolePropertyDao;
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        CustomerAccount customerAccount = customerAccountDao.getCustomerAccount(user);
        return customerAccount;
    }
    
}
