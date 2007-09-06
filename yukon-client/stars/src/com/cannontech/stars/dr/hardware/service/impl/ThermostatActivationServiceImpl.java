package com.cannontech.stars.dr.hardware.service.impl;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.ThermostatActivationService;

public class ThermostatActivationServiceImpl implements ThermostatActivationService {
    private CustomerAccountDao customerAccountDao;
    private InventoryBaseDao inventoryBaseDao;
    private LMHardwareBaseDao hardwareBaseDao;
    
    public boolean activate(String accountNumber, String serialNumber) {
        Validate.notNull(accountNumber, "AccountNumber cannot be null");
        Validate.notNull(serialNumber, "SerialNumber cannot be null");
        
        try {
            CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber);
            LMHardwareBase hardware = hardwareBaseDao.getBySerialNumber(serialNumber);
        
            InventoryBase inventoryBase = inventoryBaseDao.getById(hardware.getInventoryId());
            inventoryBase.setAccountId(account.getAccountId());
        
            boolean updateResult = inventoryBaseDao.update(inventoryBase);
            return updateResult;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }

    public void setHardwareBaseDao(LMHardwareBaseDao hardwareBaseDao) {
        this.hardwareBaseDao = hardwareBaseDao;
    }
    
}
