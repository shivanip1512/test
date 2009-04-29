package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.user.YukonUserContext;

/**
 * Task used to start scheduled opt outs and clean up any opt outs that have just
 * completed
 */
public class OptOutTask implements YukonTask {

    private Logger logger = YukonLogManager.getLogger(OptOutTask.class);
	private YukonUserContext userContext;
	
	private OptOutEventDao optOutEventDao;
	private OptOutService optOutService;
	private CustomerAccountDao customerAccountDao;
	private EnrollmentDao enrollmentDao;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	private ECMappingDao ecMappingDao;
	
    @Override
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
    
	public void start() {
    	
        logger.debug("Starting opt out task.");
        
        LiteYukonUser user = this.userContext.getYukonUser();

        List<OptOutEvent> optOutsToStart = optOutEventDao.getScheduledOptOutsToBeStarted();
        this.startOptOuts(optOutsToStart, user);

        // Get a list of all currently opted out inventory (according to the LMHardwareControlGroup
        // table)
        List<Integer> inventoryIds = enrollmentDao.getCurrentlyOptedOutInventory();
        List<LiteStarsLMHardware> optedOutInventory = getOptedOutInventory(inventoryIds);
        this.cleanUpCompletedOptOuts(optedOutInventory, user);
        
    }

    public void stop() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot stop this task.");
    }
    
    public void startOptOuts(List<OptOutEvent> optOutsToStart, LiteYukonUser user) {
    	
    	for(OptOutEvent event : optOutsToStart) {
        	
        	OptOutRequest optOutRequest = this.createOptOutRequest(event);
        	
        	logger.info("Starting scheduled opt out event for inventory: " + event.getInventoryId() 
        			+ " and account: " + event.getCustomerAccountId());
        	try {
        		CustomerAccount account = customerAccountDao.getById(event.getCustomerAccountId());
				optOutService.optOut(account, optOutRequest, user);
			} catch (CommandCompletionException e) {
				logger.error("Could not start scheduled opt out event with id: " 
						+ event.getEventId(), e);
			}
        	
        }
    }
    
    public OptOutRequest createOptOutRequest(OptOutEvent event) {
    	
    	List<ScheduledOptOutQuestion> questionList = Collections.emptyList();
    	
    	OptOutRequest optOutRequest = new OptOutRequest();
    	optOutRequest.setStartDate(null); // Null start means start now
    	optOutRequest.setInventoryIdList(Collections.singletonList(event.getInventoryId()));
    	optOutRequest.setQuestions(questionList);
    	
    	// Update event start to get duration (in case we missed this opt out by more than an hour)
    	event.setStartDate(new Date());
    	optOutRequest.setDurationInHours(event.getDurationInHours());
    	
    	return optOutRequest;
    }
    
    public List<LiteStarsLMHardware> getOptedOutInventory(List<Integer> inventoryIds) {
    	
    	List<LiteStarsLMHardware> inventoryList = new ArrayList<LiteStarsLMHardware>();
    	for(Integer inventoryId : inventoryIds) {
    	    try {
        		LiteStarsLMHardware inventory = 
            		(LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
                // Only add inventory that exists and is supposed to be done opting out according
                // to the OptOutEvent table
                if(!optOutEventDao.isOptedOut(inventoryId, inventory.getAccountID())) {
                    inventoryList.add(inventory);
                }        		
    	    }
    	    catch (NotFoundException e) {
    	        //ignore if inventory not found, and continue
    	    }
    	}
    	
    	return inventoryList;
    }
    
    public void cleanUpCompletedOptOuts(
    		List<LiteStarsLMHardware> optedOutInventory, 
    		LiteYukonUser user) 
    {
    	
    	for(LiteStarsLMHardware inventory : optedOutInventory) {
            
        	int accountId = inventory.getAccountID();
        	int inventoryId = inventory.getInventoryID();

        	OptOutEvent lastEvent = 
        		optOutEventDao.findLastEvent(inventoryId, accountId);
        	
        	try {
        		cancelOptOutEvent(inventory, lastEvent, user);
        	} catch (IllegalArgumentException e) {
        		logger.debug("Could not find last opt out event for inventory: " + inventoryId);
        	}
        }
    }
    
    public void cancelOptOutEvent(
    		LiteStarsLMHardware inventory, 
    		OptOutEvent event, 
    		LiteYukonUser user) 
    {
    	Validate.notNull(event, "Event must not be null");
    	
    	int inventoryId = inventory.getInventoryID();
    	
    	CustomerAccount account = customerAccountDao.getById(event.getCustomerAccountId());
    	LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);
    	
    	logger.info("Cleaning up opt out event for inventory: " + event.getInventoryId() 
    			+ " and account: " + event.getCustomerAccountId());
    	
        try {
			optOutService.cleanUpCancelledOptOut(
					inventory, energyCompany, event, account, user);
		} catch (CommandCompletionException e) {
			logger.error("Attempt to reenable inventory: " + inventoryId + " failed", e);
		} catch (ConnectionException e) {
			logger.error("Attempt to reenable inventory: " + inventoryId + " failed", e);
		}
    }
    
    // Injected Dependencies
    
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}

    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}

    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
    
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
    
}
