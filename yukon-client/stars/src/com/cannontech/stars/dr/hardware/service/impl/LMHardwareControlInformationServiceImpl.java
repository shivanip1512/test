package com.cannontech.stars.dr.hardware.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.service.OptOutService;
       
public class LMHardwareControlInformationServiceImpl implements LMHardwareControlInformationService {
    
    private Logger logger = YukonLogManager.getLogger(LMHardwareControlInformationServiceImpl.class);

	private LMHardwareControlGroupDao lmHardwareControlGroupDao;
	private OptOutEventDao optOutEventDao;
	private OptOutService optOutService;
    
    /*@SuppressWarnings("unused")*/
    public boolean startEnrollment(int inventoryId, int loadGroupId, int accountId,
            int relay, int programId, LiteYukonUser currentUser, boolean useHardwardAddressing) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        /*Shouldn't already be an entry, but this might be a repeat enrollment.  Check for existence*/
        try {
            LMHardwareControlGroup existingEnrollment =
                lmHardwareControlGroupDao
                    .findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(inventoryId, 
                                                                                programId, 
                                                                                accountId);
            Instant now = new Instant();

            // Clear all the opt outs for the enrolled inventory
            if (optOutEventDao.isOptedOut(inventoryId, accountId)) {
            	OptOutEvent findLastEvent = optOutEventDao.findLastEvent(inventoryId, accountId);
            	List<Integer> lastEventIdList = Collections.singletonList(findLastEvent.getEventId());
            	optOutService.cancelOptOut(lastEventIdList, currentUser);
            }
            
            /*If there is an existing enrollment that is using this same inventory and program
             * we need to then stop enrollment for that before starting the existing.
             * We need to allow this method to do stops as well as starts to help migrate from legacy STARS systems and to
             * properly log repetitive enrollments.
             */
            if (existingEnrollment != null &&
                (existingEnrollment.getLmGroupId() != loadGroupId || existingEnrollment.getRelay() != relay)) {
                existingEnrollment.setGroupEnrollStop(now);
                existingEnrollment.setUserIdSecondAction(currentUser.getUserID());
                lmHardwareControlGroupDao.update(existingEnrollment);
            }

            /*Do the start*/
            LMHardwareControlGroup controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY, relay, programId, currentUser.getUserID());
            controlInformation.setGroupEnrollStart(now);
            lmHardwareControlGroupDao.add(controlInformation);
            
            if (!useHardwardAddressing) {
                adjustLoadGroupsForExistingEnrollments(inventoryId, loadGroupId, accountId, programId, currentUser);
            }

            return true;
        } catch (Exception e) {
            logger.error("Enrollment start occurred for InventoryId: " + inventoryId + " ProgramId: " + programId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
            return false;
        }
    }
    
    public boolean stopEnrollment(int inventoryId, int loadGroupId, int accountId, int relay, int programId, LiteYukonUser currentUser) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        boolean enrollmentEnded = false;
        try {
            Instant now = new Instant();
            
            List<LMHardwareControlGroup> currentEnrollmentList = 
            	lmHardwareControlGroupDao.getCurrentEnrollmentByInventoryIdAndAccountId(inventoryId, accountId);

            // Clean up the opt out for the given inventory.
            if (currentEnrollmentList.size() > 1){
                if (optOutEventDao.isOptedOut(inventoryId, accountId)) {
                    stopOptOut(inventoryId, loadGroupId, accountId, programId, currentUser, now);
                }
            } else {
                List<OptOutEventDto> currentOptOuts = optOutEventDao.getCurrentOptOuts(accountId, inventoryId);
                for (OptOutEventDto optOutEvent : currentOptOuts) {
                    List<Integer> optOutEventList = Collections.singletonList(optOutEvent.getEventId());
                    optOutService.cancelOptOut(optOutEventList, currentUser);
				}
            }
            /*Should be an entry with a start date but no stop date.*/
            for(LMHardwareControlGroup currentEnrollment : currentEnrollmentList) {
                if(currentEnrollment.getProgramId() == programId && currentEnrollment.getRelay() == relay) {
                    endEnrollment(currentEnrollment, currentUser);
                    enrollmentEnded = true;
                }
            }
            /*
             * Shouldn't insert a stop without a start.
             * Assume we have handled existing enrollments on deployment of this functionality
             * TODO: make sure database script to handle existing enrollments is complete
             */
            return enrollmentEnded;
        } catch (Exception e) {
            logger.error("Enrollment stop occurred for InventoryId: " + inventoryId + " ProgramId: " + programId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
            return false;
        }
    }
    
    @Override
    public void startOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, 
                              Instant startDate) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        List<LMHardwareControlGroup> currentEnrollmentList = 
            lmHardwareControlGroupDao.getCurrentEnrollmentByInventoryIdAndAccountId(inventoryId, accountId);
        try {
            for(LMHardwareControlGroup enroll : currentEnrollmentList) {            
                startOptOut(inventoryId, enroll.getLmGroupId(), accountId, enroll.getProgramId(), currentUser, startDate);
            }
        } catch (Exception e) {
            logger.error("Opt out was started/scheduled for InventoryId: " + inventoryId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
        }
    }    
    
    @Override
    public boolean startOptOut(int inventoryId, int loadGroupId, int accountId, int programId, 
                                 LiteYukonUser currentUser, Instant startDate) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        
        try {
            LMHardwareControlGroup controlInformation = new LMHardwareControlGroup(inventoryId, loadGroupId, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY, programId, currentUser.getUserID());
            controlInformation.setOptOutStart(startDate);
            lmHardwareControlGroupDao.add(controlInformation);
            return true;
        } catch (Exception e) {
            logger.error("Opt out was started/scheduled for InventoryId: " + inventoryId + " ProgramId: " + programId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
        }
        return false;
    }
    
    @Override
    public void stopOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, Instant stopDate) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        
        try {
            lmHardwareControlGroupDao.stopOptOut(inventoryId, accountId, currentUser, stopDate);
        } catch (Exception e) {
            logger.error("Opt out was started/scheduled for InventoryId: " + inventoryId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
        }        
    }
    
    @Override
    public boolean stopOptOut(int inventoryId, int loadGroupId, int accountId, int programId, 
                                LiteYukonUser currentUser, Instant stopDate) {
        Validate.notNull(currentUser, "CurrentUser cannot be null");
        
        List<LMHardwareControlGroup> controlInformationList;
        try {
            controlInformationList = lmHardwareControlGroupDao.getCurrentOptOutByInventoryIdProgramIdAndAccountId(inventoryId, programId, accountId);
            /*Should be an entry with a start date but no stop date.*/
            if(controlInformationList.size() > 0) {
                for(LMHardwareControlGroup controlInformation : controlInformationList) {
                    controlInformation.setOptOutStop(stopDate);
                    controlInformation.setUserIdSecondAction(currentUser.getUserID());
                    lmHardwareControlGroupDao.update(controlInformation);
                }
                return true;
            }
            /*
             * Shouldn't have a stop without a start.
             */
        } catch (Exception e) {
            logger.error("Opt out was stopped for InventoryId: " + inventoryId + " ProgramId: " + programId + " LMGroupId: " + loadGroupId + " AccountId: " + accountId + "done by user: " + currentUser.getUsername() + " but could NOT be recorded in the LMHardwareControlGroup table.", e );
        }
        return false;
    }
    
    public List<Integer> getInventoryNotOptedOutForThisProgram(int programId, int accountId) {
        Validate.notNull(programId, "ProgramId cannot be null");
        Validate.notNull(accountId, "AccountID cannot be null");
        
        List<LMHardwareControlGroup> currentlyOptedOutList;
        List<LMHardwareControlGroup> enrolledForThisGroup;
        List<Integer> inventoryIds = new ArrayList<Integer>();
        try {
            currentlyOptedOutList = lmHardwareControlGroupDao.getCurrentOptOutByProgramIdAndAccountId(programId, accountId);
            enrolledForThisGroup = lmHardwareControlGroupDao.getCurrentEnrollmentByProgramIdAndAccountId(programId, accountId);
            /*Look for current enrollments for this program, and check to see if there is an inventoryID that is enrolled in
             * program and isn't in the opt out list.*/
            if(enrolledForThisGroup.size() > 0) {
                for(LMHardwareControlGroup enrolledEntry : enrolledForThisGroup) {
                    //both enrollment and opt outs already are current due to the query
                    if(!currentlyOptedOutList.contains(enrolledEntry)) {
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
            logger.error("Unable to retrieve opted-out inventory for ProgramId: " + programId + " AccountId: " + accountId, e );
        }
        return inventoryIds;
    }
    
    
    /** Checks to see if there are any other enrollments that use the same program that are not the same piece of hardware
     * and updates the address group for those enrollment as well.
     * @throws Exception 
     */
    private void adjustLoadGroupsForExistingEnrollments(int inventoryId, int loadGroupId, int accountId, int programId, LiteYukonUser currentUser) throws Exception {

        List<LMHardwareControlGroup> controlInformationList = 
            lmHardwareControlGroupDao.getCurrentEnrollmentByProgramIdAndAccountId(programId, accountId); 

        for(LMHardwareControlGroup existingEnrollment : controlInformationList) {
            if (inventoryId != existingEnrollment.getInventoryId() && existingEnrollment.getLmGroupId() != loadGroupId) {
                LMHardwareControlGroup newEnrollment = existingEnrollment.clone();
                endEnrollment(existingEnrollment, currentUser);
                newEnrollment.setControlEntryId(null);
                newEnrollment.setLmGroupId(loadGroupId);
                startEnrollment(newEnrollment, currentUser);
            }
        }
    }
    
    private void endEnrollment(LMHardwareControlGroup existingEnrollment, LiteYukonUser currentUser){
        existingEnrollment.setGroupEnrollStop(new Instant());
		existingEnrollment.setUserIdSecondAction(currentUser.getUserID());
		lmHardwareControlGroupDao.update(existingEnrollment);
    }
    
    private void startEnrollment(LMHardwareControlGroup newEnrollment, LiteYukonUser currentUser){
        newEnrollment.setGroupEnrollStart(new Instant());
    	newEnrollment.setUserIdFirstAction(currentUser.getUserID());
		lmHardwareControlGroupDao.add(newEnrollment);
		
    }
    
    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
    	this.optOutEventDao = optOutEventDao;
    }

    @Autowired
    public void setOptOutService(OptOutService optOutService) {
    	this.optOutService = optOutService;
    }
}
