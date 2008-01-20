package com.cannontech.stars.dr.hardware.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;

public class LMHardwareControlInformationServiceImpl implements LMHardwareControlInformationService {
    
    private Logger logger = YukonLogManager.getLogger(LMHardwareControlInformationServiceImpl.class);
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;

    /*@SuppressWarnings("unused")*/
    public boolean startEnrollment(int inventoryId, int loadGroupId, int accountId, int relay, LiteYukonUser currentUser) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        List<LMHardwareControlGroup> controlInformationList;
        /*Shouldn't already be an entry, but this might be a repeat enrollment.  Check for existence*/
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            Date now = new Date();
            /*If there is an existing enrollment that is using this same device, load group, and potentially, the same relay
             * we need to then stop enrollment for that before starting the existing.
             * We need to allow this method to do stops as well as starts to help migrate from legacy STARS systems and to
             * properly log repetitive enrollments.
             */
            for(LMHardwareControlGroup existingEnrollment : controlInformationList) {
                if(existingEnrollment.getRelay() == relay && existingEnrollment.getGroupEnrollStop() == null) {
                    /*This entry already has a start date, has no stop date, and is on the same relay: better register a stop.*/
                    existingEnrollment.setGroupEnrollStop(now);
                    existingEnrollment.setUserIdSecondAction(currentUser.getUserID());
                    lmHardwareControlGroupDao.update(existingEnrollment);
                    
                    /*Unenrolling also means a current opt out needs to be marked as complete*/
                    stopOptOut(inventoryId, loadGroupId, accountId, currentUser);
                }
            }
            /*Do the start*/
            LMHardwareControlGroup controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY, relay, currentUser.getUserID());
            controlInformation.setGroupEnrollStart(now);
            lmHardwareControlGroupDao.add(controlInformation);
            return true;
        } catch (Exception e) {
            logger.error("Enrollment start occurred for InventoryId: " + inventoryId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
            return false;
        }
    }
    
    public boolean stopEnrollment(int inventoryId, int loadGroupId, int accountId, int relay, LiteYukonUser currentUser) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        List<LMHardwareControlGroup> controlInformationList;
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            Date now = new Date();
            /*Should be an entry with a start date but no stop date.*/
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    if(controlInformation.getGroupEnrollStart() != null && controlInformation.getGroupEnrollStop() == null 
                            && controlInformation.getRelay() == relay) {
                        controlInformation.setGroupEnrollStop(now);
                        controlInformation.setUserIdSecondAction(currentUser.getUserID());
                        lmHardwareControlGroupDao.update(controlInformation);
                        
                        /*Unenrolling also means a current opt out needs to be marked as complete*/
                        stopOptOut(inventoryId, loadGroupId, accountId, currentUser);
                    }
                    /*else enrollment is already current*/
                    //else if(controlInformation.getGroupEnrollStart() != null && controlInformation.getGroupEnrollStop() != null)
                };
                return true;
            }
            /*
             * Shouldn't insert a stop without a start.
             * Assume we have handled existing enrollments on deployment of this functionality
             * TODO: make sure database script to handle existing enrollments is complete
             */
            return false;
        } catch (Exception e) {
            logger.error("Enrollment stop occurred for InventoryId: " + inventoryId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
            return false;
        }
    }
    
    public boolean startOptOut(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        
        try {
            Date now = new Date();
            LMHardwareControlGroup controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY, currentUser.getUserID());
            controlInformation.setOptOutStart(now);
            lmHardwareControlGroupDao.add(controlInformation);
            return true;
        } catch (Exception e) {
            logger.error("Opt out was started/scheduled for InventoryId: " + inventoryId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
        }
        return false;
    }
    
    public boolean stopOptOut(int inventoryId, int loadGroupId, int accountId, LiteYukonUser currentUser) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        
        List<LMHardwareControlGroup> controlInformationList;
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
            Date now = new Date();
            /*Should be an entry with a start date but no stop date.*/
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    if(controlInformation.getOptOutStart() != null && controlInformation.getOptOutStop() == null) {
                        controlInformation.setOptOutStop(now);
                        controlInformation.setUserIdSecondAction(currentUser.getUserID());
                        lmHardwareControlGroupDao.update(controlInformation);
                    }
                }
                return true;
            }
            /*
             * Shouldn't have a stop without a start.
             */
        } catch (Exception e) {
            logger.error("Opt out was stopped for InventoryId: " + inventoryId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
        }
        return false;
    }
    
    public List<Integer> getInventoryNotOptedOut(int inventoryId, int loadGroupId, int accountId) {
        List<LMHardwareControlGroup> controlInformationList;
        List<Integer> inventoryIds = new ArrayList<Integer>();
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    //currently opted out
                    if(controlInformation.getOptOutStart() != null && controlInformation.getOptOutStop() == null) 
                        continue;
                    //not opted out, return it
                    inventoryIds.add(controlInformation.getInventoryId());
                }
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve non-opted-out inventory for InventoryId: " + inventoryId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId, e );
        }
        return inventoryIds;
    }
    
    public LMHardwareControlGroupDao getLmHardwareControlGroupDao() {
        return lmHardwareControlGroupDao;
    }

    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

}
