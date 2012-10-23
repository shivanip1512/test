package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.service.OptOutService;

public abstract class OverrideRequestEndpointBase {
    
    private CustomerAccountDao customerAccountDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    protected AccountEventLogService accountEventLogService;
    protected OptOutService optOutService;
    protected LmHardwareBaseDao lmHardwareBaseDao;
    protected RolePropertyDao rolePropertyDao;

    protected CustomerAccount getCustomerAccount(String accountNumber, LiteYukonUser user) throws AccountNotFoundException {
        try {
            int energyCompanyId = yukonEnergyCompanyService.getEnergyCompanyIdByOperator(user);
            List<Integer> energyCompanyIds = yukonEnergyCompanyService.getChildEnergyCompanies(energyCompanyId);
            energyCompanyIds.add(energyCompanyId);
            return customerAccountDao.getByAccountNumber(accountNumber, energyCompanyIds);
        } catch (NotFoundException e) {
            throw new AccountNotFoundException("Account " + accountNumber+ " couldn't be found.");
        }
    }
    
    protected LMHardwareBase getLMHardwareBase(String serialNumber) throws InventoryNotFoundException {
        try {
            return lmHardwareBaseDao.getBySerialNumber(serialNumber);
        } catch (NotFoundException e) {
            throw new InventoryNotFoundException("Inventory " + serialNumber +" couldn't be found.");
        }
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LmHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
    
}
