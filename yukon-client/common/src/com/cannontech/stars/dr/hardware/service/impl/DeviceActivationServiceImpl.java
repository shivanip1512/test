package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.model.LMCustomerEventBase;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.DeviceActivationService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;

public class DeviceActivationServiceImpl implements DeviceActivationService {
    
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareBaseDao hardwareBaseDao;
    @Autowired private LMCustomerEventBaseDao customerEventBaseDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private SelectionListService selectionListService;
    
    @Override
    public boolean isValidActivation(String accountNumber, String serialNumber, LiteYukonUser user) {
        Validate.notNull(accountNumber, "AccountNumber cannot be null");
        Validate.notNull(serialNumber, "SerialNumber cannot be null");
        
        try {
            customerAccountDao.getByAccountNumber(accountNumber, user);
            LMHardwareBase hardware = hardwareBaseDao.getBySerialNumber(serialNumber);
            inventoryBaseDao.getByInventoryId(hardware.getInventoryId());
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        } catch (NotFoundException nfe){
            return false;
        }
    }
    
    @Override
    public boolean activate(LiteStarsEnergyCompany energyCompany, String accountNumber, 
    		String serialNumber, LiteYukonUser user) {
        Validate.notNull(energyCompany, "EnergyCompany cannot be null");
        Validate.notNull(accountNumber, "AccountNumber cannot be null");
        Validate.notNull(serialNumber, "SerialNumber cannot be null");
        
        try {
            CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, user);
            LMHardwareBase hardware = hardwareBaseDao.getBySerialNumber(serialNumber);
        
            LiteInventoryBase inventoryBase = inventoryBaseDao.getByInventoryId(hardware.getInventoryId());
            inventoryBase.setAccountID(account.getAccountId());
        
            inventoryBaseDao.saveInventoryBase(inventoryBase, energyCompany.getLiteID());
            dbChangeManager.processDbChange(inventoryBase.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);
            
            int eventTypeId = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
            int installActID = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL ).getEntryID();
            
            LMCustomerEventBase eventBase = new LMCustomerEventBase();
            eventBase.setActionId(installActID);
            eventBase.setEventTypeId(eventTypeId);
            eventBase.setEventDateTime(new Date());
            eventBase.setAuthorizedBy("");
            eventBase.setNotes("Install Event generated by onsite installer");
            customerEventBaseDao.addHardwareEvent(eventBase, energyCompany.getEnergyCompanyId(), inventoryBase.getInventoryID());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

}