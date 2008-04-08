package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.display.DisplayableProgramDao;

public abstract class AbstractConsumerController {
    protected CustomerAccountDao customerAccountDao;
    protected ApplianceDao applianceDao;
    protected ProgramDao programDao;
    protected InventoryBaseDao inventoryDao;
    protected DisplayableProgramDao displayableProgramDao;
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        List<CustomerAccount> accountList = customerAccountDao.getByUser(user);
        CustomerAccount account = accountList.get(0);
        return account;
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
    public void setInventoryDao(InventoryBaseDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setDisplayableProgramDao(
            DisplayableProgramDao displayableProgramDao) {
        this.displayableProgramDao = displayableProgramDao;
    }
    
}
