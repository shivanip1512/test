package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;

public class LMHardwareControlInformationServiceImpl implements LMHardwareControlInformationService {
    
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;

    /*@SuppressWarnings("unused")*/
    public boolean startEnrollment(int inventoryId, int loadGroupId, int accountId) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean stopEnrollment(int inventoryId, int loadGroupId, int accountId) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean startOptOut(int inventoryId, int loadGroupId, int accountId) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean stopOptOut(int inventoryId, int loadGroupId, int accountId) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        try {
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public long calculateControlHistory(int loadGroupId, int accountId, Date start, Date stop) {
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

}
