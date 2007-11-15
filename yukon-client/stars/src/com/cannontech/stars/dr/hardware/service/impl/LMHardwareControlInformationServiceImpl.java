package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.CTILogger;
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
        List<LMHardwareControlGroup> controlInformationList;
        /*Shouldn't already be an entry, but this might be a repeat enrollment.  Check for existence*/
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            Date now = dateFormattingService.getCalendar(currentUser).getTime();
            /*Unexpected, there was at least one entry already there.  this may be a repeat enrollment?*/
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    /*entry in need of a start date -- this is an unlikely case*/
                    if(controlInformation.getGroupEnrollStart() == null && controlInformation.getGroupEnrollStop() == null) {
                        controlInformation.setGroupEnrollStart(now);
                        lmHardwareControlGroupDao.update(controlInformation);
                        return true;
                    }
                    /*enrollment is already current*/
                    else if(controlInformation.getGroupEnrollStart() != null && controlInformation.getGroupEnrollStop() == null)
                        return true;
                }
            }
            /*As expected, there was NOT an enrollment already for this group, inventory, account.*/
            LMHardwareControlGroup controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            controlInformation.setGroupEnrollStart(now);
            lmHardwareControlGroupDao.add(controlInformation);
            return true;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean stopEnrollment(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        List<LMHardwareControlGroup> controlInformationList;
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            Date now = dateFormattingService.getCalendar(currentUser).getTime();
            /*Should be an entry with a start date but no stop date.*/
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    if(controlInformation.getGroupEnrollStart() != null && controlInformation.getGroupEnrollStop() == null) {
                        controlInformation.setGroupEnrollStop(now);
                        lmHardwareControlGroupDao.update(controlInformation);
                        return true;
                    }
                    /*enrollment is already current*/
                    else if(controlInformation.getGroupEnrollStart() != null && controlInformation.getGroupEnrollStop() != null)
                        return true;
                }
            }
            /*
             * Shouldn't insert a stop without a start.
             * Assume we have handling existing enrollments on roll-out
             * TODO: make sure database script to handle existing enrollments is complete
             */
            return false;
        } catch (DataRetrievalFailureException e) {
            return false;
        }
    }
    
    public boolean startOptOut(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        try {
            Date now = dateFormattingService.getCalendar(currentUser).getTime();
            LMHardwareControlGroup controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
            controlInformation.setOptOutStart(now);
            lmHardwareControlGroupDao.add(controlInformation);
            return true;
        } catch (Exception e) {
            CTILogger.error("Opt out start failed to log", e);
            return false;
        }
    }
    
    public boolean stopOptOut(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(inventoryId, "InventoryID cannot be null");
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        List<LMHardwareControlGroup> controlInformationList;
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
            Date now = dateFormattingService.getCalendar(currentUser).getTime();
            /*Should be an entry with a start date but no stop date.*/
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    if(controlInformation.getOptOutStart() != null && controlInformation.getOptOutStop() == null) {
                        controlInformation.setOptOutStop(now);
                        lmHardwareControlGroupDao.update(controlInformation);
                        return true;
                    }
                }
            }
            /*
             * Shouldn't have a stop without a start.
             */
            return false;
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
