package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public abstract class OverrideRequestEndpointBase {
    
    private CustomerAccountDao customerAccountDao;
    private EnergyCompanyDao ecDao;
    
    protected AccountEventLogService accountEventLogService;
    protected OptOutService optOutService;
    protected LmHardwareBaseDao lmHardwareBaseDao;
    protected RolePropertyDao rolePropertyDao;

    protected CustomerAccount getCustomerAccount(String accountNumber, LiteYukonUser user) throws AccountNotFoundException {
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        List<EnergyCompany> energyCompanies = energyCompany.getDescendants(true);

        try {
            return customerAccountDao.getByAccountNumber(accountNumber, energyCompanies);
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
    public void setEnergyCompanyDao(EnergyCompanyDao ecDao) {
        this.ecDao = ecDao;
    }
    
    
}
