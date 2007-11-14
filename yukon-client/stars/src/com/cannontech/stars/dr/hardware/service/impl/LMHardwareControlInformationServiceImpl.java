package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;

public class LMHardwareControlInformationServiceImpl implements LMHardwareControlInformationService {
    
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private DateFormattingService dateFormattingService;

    /*@SuppressWarnings("unused")*/
    public boolean startEnrollment(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        LMHardwareControlGroup controlInformation;
        try {
            controlInformation = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountId(inventoryId, loadGroupId, accountId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId);
            controlInformation.setGroupEnrollStart(dateFormattingService.getCalendar(currentUser).getTime());
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean stopEnrollment(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            LMHardwareControlGroup controlInformation = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountId(inventoryId, loadGroupId, accountId);
            
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean startOptOut(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean stopOptOut(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public long calculateControlHistory(int loadGroupId, int accountId, Date start, Date stop, LiteYukonUser currentUser) {
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        Validate.notNull(start, "Start Date cannot be null");
        Validate.notNull(stop, "Stop Date cannot be null");
        
        try {
            return new Date().getTime();
        } catch (DataRetrievalFailureException e) {
            return 0;
        }
    }

    public LMHardwareControlGroupDao getLmHardwareControlGroupDao() {
        return lmHardwareControlGroupDao;
    }

    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    public DateFormattingService getDateFormattingService() {
        return dateFormattingService;
    }

    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

}
