package com.cannontech.stars.util.task;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.jobs.support.YukonTask;
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
    	
        logger.info("Starting opt out task.");
        
        LiteYukonUser user = this.userContext.getYukonUser();

        // Start scheduled opt outs
        
        List<ScheduledOptOutQuestion> questionList = Collections.emptyList();

        // Get scheduled opt outs to start
        List<OptOutEvent> optOutsToStart = optOutEventDao.getScheduledOptOutsToBeStarted();
        
        // Start opt outs
        for(OptOutEvent event : optOutsToStart) {
        	
        	CustomerAccount account = customerAccountDao.getById(event.getCustomerAccountId());
        	
        	OptOutRequest optOutRequest = new OptOutRequest();
        	optOutRequest.setStartDate(null); // Null start means start now
        	optOutRequest.setInventoryIdList(Collections.singletonList(event.getInventoryId()));
        	optOutRequest.setQuestions(questionList);
        	
        	// Update event start to get duration (in case we missed this opt out by more than an hour)
        	event.setStartDate(new Date());
        	optOutRequest.setDurationInHours(event.getDurationInHours());
        	
        	try {
				optOutService.optOut(account, optOutRequest, user);
			} catch (CommandCompletionException e) {
				logger.error("Could not start scheduled opt out", e);
			}
        	
        }

        // Clean up any opt outs that have completed
        
        // Get a list of all currently opted out inventory (according to the LMHardwareControlGroup
        // table)
        List<Integer> optedOutInventory = enrollmentDao.getCurrentlyOptedOutInventory();
        for(Integer inventoryId : optedOutInventory) {
        
        	LiteStarsLMHardware inventory = 
        		(LiteStarsLMHardware) starsInventoryBaseDao.getById(inventoryId);
        	int accountId = inventory.getAccountID();

        	// Check the OptOutEvent table to see if this inventory is REALLY opted out
        	// or if it has come out of the opted out state
        	boolean optedOut = optOutEventDao.isOptedOut(inventoryId, accountId);
			
        	// If the OptOutEvent table says the inventory is NOT currently opted out,
        	// cleanup the LMHardwareControlGroup and send a reenable command just to be
        	// sure the inventory is no longer opted out
        	if(!optedOut) {
	        	OptOutEvent lastEvent = 
	        		optOutEventDao.getLastEvent(inventoryId, accountId);
	        	
	        	CustomerAccount account = customerAccountDao.getById(accountId);
	        	LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);
	        	
		        try {
					optOutService.cleanUpCancelledOptOut(
							inventory, energyCompany, lastEvent, account, user);
				} catch (CommandCompletionException e) {
					logger.error("Attempt to reenable device failed", e);
				}
        	}
        }
        
    }

    public void stop() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot stop this task.");
    }
    
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
