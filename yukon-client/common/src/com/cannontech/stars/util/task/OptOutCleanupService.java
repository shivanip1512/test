package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.UserUtils;

/**
 * Service used to start scheduled opt outs and clean up any opt outs that have just
 * completed
 */
public class OptOutCleanupService {

    private Logger logger = YukonLogManager.getLogger(OptOutCleanupService.class);
	
	private OptOutEventDao optOutEventDao;
	private OptOutService optOutService;
	private CustomerAccountDao customerAccountDao;
	private EnrollmentDao enrollmentDao;
	private InventoryBaseDao inventoryBaseDao;
	private EnergyCompanyDao ecDao;
	private ScheduledExecutor executor;
	
	@PostConstruct
	public void init() throws Exception {
	    executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.debug("Starting opt out task.");

                LiteYukonUser user = UserUtils.getAdminYukonUser();

                List<OptOutEvent> optOutsToStart = optOutEventDao.getScheduledOptOutsToBeStarted();
                startOptOuts(optOutsToStart, user);

                // Get a list of all currently opted out inventory (according to the LMHardwareControlGroup
                // table)
                List<Integer> inventoryIds = enrollmentDao.getCurrentlyOptedOutInventory();
                List<LiteLmHardwareBase> completedOptOuts = getCompletedOptOuts(inventoryIds);
                cleanUpCompletedOptOuts(completedOptOuts, user);
                logger.debug("Finished opt out task.");
        
            }
        }, 1, 5, TimeUnit.MINUTES);
        
    }

    private void startOptOuts(List<OptOutEvent> optOutsToStart, LiteYukonUser user) {
    	
    	for(OptOutEvent event : optOutsToStart) {
        	
        	OptOutRequest optOutRequest = createOptOutRequest(event);
        	
        	logger.debug("Starting scheduled opt out event for inventory: " + event.getInventoryId() 
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
    
    private OptOutRequest createOptOutRequest(OptOutEvent event) {
    	
    	List<ScheduledOptOutQuestion> questionList = Collections.emptyList();
    	
    	OptOutRequest optOutRequest = new OptOutRequest();
    	optOutRequest.setStartDate(null); // Null start means start now
    	optOutRequest.setInventoryIdList(Collections.singletonList(event.getInventoryId()));
    	optOutRequest.setQuestions(questionList);
    	
    	// Update event start to get duration (in case we missed this opt out by more than an hour)
    	event.setStartDate(new Instant());
    	optOutRequest.setDurationInHours(event.getDurationInHours());
    	optOutRequest.setEventId(event.getEventId());
    	
    	return optOutRequest;
    }
    
    private List<LiteLmHardwareBase> getCompletedOptOuts(List<Integer> inventoryIds) {
    	
    	List<LiteLmHardwareBase> inventoryList = new ArrayList<LiteLmHardwareBase>();
    	for(Integer inventoryId : inventoryIds) {
    	    try {
        		LiteLmHardwareBase inventory = 
            		(LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
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
    
    private void cleanUpCompletedOptOuts(List<LiteLmHardwareBase> optedOutInventory, LiteYukonUser user) {
    	
    	for(LiteLmHardwareBase inventory : optedOutInventory) {
        	OptOutEvent lastEvent = optOutEventDao.findLastEvent(inventory.getInventoryID());
        	
        	try {
        		cancelOptOutEvent(inventory, lastEvent, user);
        	} catch (IllegalArgumentException e) {
        		logger.debug("Could not find last opt out event for inventory: " + inventory.getInventoryID());
        	}
        }
    }
    
    private void cancelOptOutEvent(LiteLmHardwareBase inventory, OptOutEvent event, LiteYukonUser user) {
    	Validate.notNull(event, "Event must not be null");
    	
    	int inventoryId = inventory.getInventoryID();
    	YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByInventoryId(inventoryId);
    	
    	logger.debug("Cleaning up opt out event for inventory: " + event.getInventoryId() + " and account: " + event.getCustomerAccountId());
    	
        try {
			optOutService.cleanUpCancelledOptOut(inventory, yukonEnergyCompany, event, user);
		} catch (CommandCompletionException e) {
			logger.error("Attempt to reenable inventory: " + inventoryId + " failed", e);
		} catch (ConnectionException e) {
			logger.error("Attempt to reenable inventory: " + inventoryId + " failed", e);
		}
    }
    
    // DI Setters
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}

    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao ecDao) {
        this.ecDao = ecDao;
    }
    
    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}

    @Autowired
    public void setInventoryBaseDao(
			InventoryBaseDao inventoryBaseDao) {
		this.inventoryBaseDao = inventoryBaseDao;
	}
    
    @Resource(name="globalScheduledExecutor")
    public void setExecutor(ScheduledExecutor executor) {
        this.executor = executor;
    }
    
}
