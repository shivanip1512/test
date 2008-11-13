package com.cannontech.stars.dr.hardware.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
       
public class LMHardwareControlInformationServiceImpl implements LMHardwareControlInformationService {
    
    private Logger logger = YukonLogManager.getLogger(LMHardwareControlInformationServiceImpl.class);
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ProgramDao programDao;
    
    /*@SuppressWarnings("unused")*/
    public boolean startEnrollment(int inventoryId, int loadGroupId, int accountId, int relay, LiteYukonUser currentUser) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        List<LMHardwareControlGroup> controlInformationList;
        /*Shouldn't already be an entry, but this might be a repeat enrollment.  Check for existence*/
        try {
            controlInformationList = lmHardwareControlGroupDao.getByInventoryIdAndAccountIdAndType(inventoryId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            Date now = new Date();

            /*If there is an existing enrollment that is using this same device, load group, and potentially, the same relay
             * we need to then stop enrollment for that before starting the existing.
             * We need to allow this method to do stops as well as starts to help migrate from legacy STARS systems and to
             * properly log repetitive enrollments.
             */
            List<Integer> newProgramIds = programDao.getDistinctProgramIdsByGroupIds(Collections.singleton(loadGroupId));
            for(LMHardwareControlGroup existingEnrollment : controlInformationList) {
                
                List<Integer> existingProgramIds = programDao.getDistinctProgramIdsByGroupIds(Collections.singleton(existingEnrollment.getLmGroupId()));
                if(existingProgramIds.contains(newProgramIds.get(0)) &&
                   existingEnrollment.getLmGroupId() != loadGroupId &&
                   existingEnrollment.getRelay() == relay && 
                   existingEnrollment.getGroupEnrollStop() == null) {
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
    
    public List<Integer> getInventoryNotOptedOutForThisLoadGroup(int loadGroupId, int accountId) {
        Validate.notNull(loadGroupId, "LoadGroupID cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        List<LMHardwareControlGroup> currentlyOptedOutList;
        List<LMHardwareControlGroup> enrolledForThisGroup;
        List<Integer> inventoryIds = new ArrayList<Integer>();
        try {
            currentlyOptedOutList = lmHardwareControlGroupDao.getCurrentOptOutByGroupIdAndAccountId(loadGroupId, accountId);
            enrolledForThisGroup = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
            /*Look for current enrollments for this load group, and check to see if there is an inventoryID that is enrolled in
             * program (by lmgroup in this case obviously) and isn't in the opt out list.*/
            if(enrolledForThisGroup.size() > 0) {
                for(LMHardwareControlGroup enrolledEntry : enrolledForThisGroup) {
                    //check to see if enrollment is current, opt outs already are current due to the query so we don't need to worry about them
                    if(enrolledEntry.getGroupEnrollStop() == null && !currentlyOptedOutList.contains(enrolledEntry)) {
                        boolean optedOut = false;
                        for(LMHardwareControlGroup optOutEntry : currentlyOptedOutList) {
                            if(enrolledEntry.getInventoryId() == optOutEntry.getInventoryId()) {
                                optedOut = true;
                                break;
                            }
                        }
                        
                        if(!optedOut && ! inventoryIds.contains(enrolledEntry.getInventoryId()))
                            inventoryIds.add(enrolledEntry.getInventoryId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve opted-out inventory for LMGroupId: " + loadGroupId + " AccountId: " + accountId, e );
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
    
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

}
