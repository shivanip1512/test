package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.InventoryRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.AlreadyOptedOutException;
import com.cannontech.stars.dr.optout.exception.NotOptedOutException;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OptOutLog;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutNotificationService;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.user.YukonUserContext;

public class OptOutServiceImpl implements OptOutService {

	private StarsInventoryBaseDao starsInventoryBaseDao;
	private OptOutEventDao optOutEventDao;
	private OptOutAdditionalDao optOutAdditionalDao;
	private OptOutNotificationService optOutNotificationService;
	private ECMappingDao ecMappingDao;
	private CustomerAccountDao customerAccountDao;
	private AuthDao authDao;
	private CommandRequestRouteExecutor commandRequestRouteExecutor;
	private StarsDatabaseCache starsDatabaseCache;
	private OptOutStatusService optOutStatusService;
	private OptOutTemporaryOverrideDao optOutTemporaryOverrideDao;
	private LMHardwareControlInformationService lmHardwareControlInformationService;
	private LMHardwareControlGroupDao lmHardwareControlGroupDao;
	private CustomerDao customerDao;
	private RoleDao roleDao;
	private ProgramDao programDao;
	private EnergyCompanyDao energyCompanyDao;
	private EnrollmentDao enrollmentDao;
	private StarsSearchDao starsSearchDao;
	
	private final Logger logger = YukonLogManager.getLogger(OptOutServiceImpl.class);
	

	@Override
	@Transactional
	public void optOut(CustomerAccount customerAccount, OptOutRequest request, 
			YukonUserContext userContext) throws CommandCompletionException {
		
		int customerAccountId = customerAccount.getAccountId();
		LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
		List<Integer> inventoryIdList = request.getInventoryIdList();
		LiteYukonUser user = userContext.getYukonUser();
		Date startDate = request.getStartDate();
		boolean startNow = false;
		if(startDate == null) {
			// Start now
			request.setStartDate(new Date());
			startNow = true;
		}
		
		
		Date now = new Date();
		boolean optOutCountsBoolean = optOutStatusService.getOptOutCounts(user);
		OptOutCounts optOutCounts = OptOutCounts.valueOf(optOutCountsBoolean);
		
		// Send opt out command immediately for each inventory
    	for(Integer inventoryId : inventoryIdList) { 

			LiteStarsLMHardware inventory = 
    			(LiteStarsLMHardware) starsInventoryBaseDao.getById(inventoryId);
    		
			// Create and save opt out event for this inventory
			OptOutEvent event = new OptOutEvent();
			
			event.setEventCounts(optOutCounts);
			event.setCustomerAccountId(customerAccountId);
			event.setInventoryId(inventoryId);
			event.setScheduledDate(now);
			event.setStartDate(request.getStartDate());
			event.setStopDate(request.getStopDate());

			if(!startNow) {
				
				// Cancel any existing scheduled opt out
				OptOutEvent scheduledEvent = 
					optOutEventDao.getScheduledOptOutEvent(inventoryId, customerAccountId);
				if(scheduledEvent != null) {
					this.cancelOptOut(
							Collections.singletonList(scheduledEvent.getEventId()), 
							userContext);
				}

				// Schedule the opt out 
				event.setState(OptOutEventState.SCHEDULED);
				optOutEventDao.save(event, OptOutAction.SCHEDULE, user);
		    	
				
			} else {
				// Do the opt out now 
				
				// Make sure the device is not already opted out
				if(optOutEventDao.isOptedOut(inventoryId, customerAccountId)) {
					throw new AlreadyOptedOutException(inventoryId, customerAccountId);
				}
				
				event.setState(OptOutEventState.START_OPT_OUT_SENT);
			
	    		// Send the command to the field
	    		this.sendOptOutRequest(
	    				inventory, 
	    				energyCompany, 
	    				request.getDurationInHours(), 
	    				user);

	    		optOutEventDao.save(event, OptOutAction.START_OPT_OUT, user);
	    		
	    		// Update the LMHardwareControlGroup table
	    		List<LMHardwareConfiguration> configurationList = 
	    			lmHardwareControlGroupDao.getOldConfigDataByInventoryId(inventoryId);
	    		
	    		for(LMHardwareConfiguration configuration : configurationList) {
		    		
	    			int loadGroupId = configuration.getAddressingGroupId();
	    			lmHardwareControlInformationService.startOptOut(
		    				inventoryId, 
		    				loadGroupId, 
		    				customerAccountId, 
		    				user, 
		    				event.getStartDate());
	    		}
	    		
			}
    		
	    	// Log the event
	    	StringBuffer logMsg = new StringBuffer();
	    	logMsg.append("Start Date/Time:" + StarsUtils.formatDate(
																event.getStartDate(), 
																energyCompany.getDefaultTimeZone()));
	    	logMsg.append(", Duration:" + ServletUtils.getDurationFromHours(
	    			event.getDurationInHours()));
	    	logMsg.append(", Serial #:" + inventory.getManufacturerSerialNumber());
	    	
	    	ActivityLogger.logEvent(
	    			user.getUserID(), 
	    			customerAccount.getAccountId(), 
	    			energyCompany.getEnergyCompanyID(), 
	    			customerAccount.getCustomerId(),
                    ActivityLogActions.PROGRAM_OPT_OUT_ACTION, 
                    logMsg.toString());
    	}
    	
    	// Send opt out notification
    	try {
			optOutNotificationService.sendOptOutNotification(
					customerAccount, 
					energyCompany, 
					request, 
					userContext);
		} catch (MessagingException e) {
			// Not much we can do - tried to send notification
			logger.error(e);
		}
		
	}

	@Transactional
	public void resendOptOut(int inventoryId, int customerAccountId, LiteYukonUser user) 
		throws CommandCompletionException {

		// Verify that this inventory is currently opted out
		boolean optedOut = optOutEventDao.isOptedOut(inventoryId, customerAccountId);

		if (optedOut) {
			
			LiteStarsLMHardware inventory = 
				(LiteStarsLMHardware) starsInventoryBaseDao.getById(inventoryId);
			LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);
			
			OptOutEvent lastEvent = optOutEventDao.getLastEvent(inventoryId, customerAccountId);
			
			int newDuration = TimeUtil.differenceInHours(new Date(), lastEvent.getStopDate());
			
			OptOutRequest request = new OptOutRequest();
			request.setInventoryIdList(Collections.singletonList(inventoryId));
			request.setStartDate(lastEvent.getStartDate());
			request.setDurationInHours(newDuration);

			// Send command out to the field
			this.sendOptOutRequest(inventory, energyCompany, newDuration, user);
			
			// Log this repeat event request
			OptOutLog log = new OptOutLog();
			log.setAction(OptOutAction.REPEAT_START_OPT_OUT);
			log.setCustomerAccountId(customerAccountId);
			log.setEventCounts(lastEvent.getEventCounts());
			log.setEventId(lastEvent.getEventId());
			log.setInventoryId(inventoryId);
			log.setStartDate(lastEvent.getStartDate());
			log.setStopDate(lastEvent.getStopDate());
			log.setUserId(user.getUserID());
			
			optOutEventDao.logOptOutRequest(log);
			
		} else {
			throw new NotOptedOutException(inventoryId, customerAccountId);
		}
		
	}

	@Override
	@Transactional
	public void cancelOptOut(List<Integer> eventIdList, YukonUserContext userContext) 
		throws CommandCompletionException {
		
		for(Integer eventId : eventIdList) {
			
			OptOutEvent event = optOutEventDao.getOptOutEventById(eventId);
			Integer inventoryId = event.getInventoryId();
			LiteStarsLMHardware inventory = 
				(LiteStarsLMHardware) starsInventoryBaseDao.getById(inventoryId);
			LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);
    		CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(inventoryId);
    		LiteYukonUser user = userContext.getYukonUser();
    		
			OptOutEventState state = event.getState();
			if(OptOutEventState.START_OPT_OUT_SENT == state && event.getStopDate().after(new Date())) {
				// The opt out is active and the stop date is after now
				
				this.cancelOptOutNow(
						inventory, 
						energyCompany, 
						event, 
						customerAccount, 
						userContext);
				
			} else if (OptOutEventState.SCHEDULED == state) {
				// The opt out is scheduled but not active
				
				// Cancel the scheduled opt out
				event.setState(OptOutEventState.SCHEDULE_CANCELED);
				// Set the stop date to the start date - keep track of when it was scheduled
				// to happen but duration will be 0 hours
				event.setStopDate(event.getStopDate());
				optOutEventDao.save(event, OptOutAction.CANCEL_SCHEDULE, user);
				
				ActivityLogger.logEvent(
						user.getUserID(), 
						customerAccount.getAccountId(), 
						energyCompany.getLiteID(),
						customerAccount.getCustomerId(), 
						ActivityLogActions.PROGRAM_CANCEL_SCHEDULED_ACTION, 
						"");

				// Send cancel scheduled notification
				try {
					OptOutRequest request = new OptOutRequest();
					request.setStartDate(event.getStartDate());
					request.setInventoryIdList(Collections.singletonList(inventoryId));
					request.setDurationInHours(event.getDurationInHours());
					request.setQuestions(new ArrayList<ScheduledOptOutQuestion>());
					
					optOutNotificationService.sendCancelScheduledNotification(
							customerAccount, 
							energyCompany, 
							request, 
							userContext);
				} catch (MessagingException e) {
					// Not much we can do - tried to send notification
					logger.error(e);
				}
				
			} else {
				throw new NotOptedOutException(inventoryId, customerAccount.getAccountId());
			}
		}
	}

	@Override
	public void cancelAllOptOuts(YukonUserContext userContext) {

		LiteYukonUser user = userContext.getYukonUser();
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
		List<OptOutEvent> currentOptOuts = optOutEventDao.getAllCurrentOptOuts(energyCompany);
		
		for(OptOutEvent event : currentOptOuts) {
			
			Integer inventoryId = event.getInventoryId();
			LiteStarsLMHardware inventory = 
				(LiteStarsLMHardware) starsInventoryBaseDao.getById(inventoryId);
			CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(inventoryId);
			
			try {
				this.cancelOptOutNow(
						inventory, 
						energyCompany, 
						event, 
						customerAccount, 
						userContext);
			} catch (CommandCompletionException e) {
				// Can't do much - tried to cancel opt out.  Log the error and 
				// continue to cancel other opt outs
				logger.error(e);
			}
		}
	}
	
	@Override
	public void changeOptOutCountStateForToday(LiteYukonUser user, boolean optOutCounts) {

		Date now = new Date();
    	Date stopDate = TimeUtil.getMidnightTonight();
    	
    	// Temporarily update count state
    	optOutTemporaryOverrideDao.setTemporaryOptOutCounts(user, now, stopDate, optOutCounts);
    	
    	// Update any currently active opt outs
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	OptOutCounts counts = OptOutCounts.valueOf(optOutCounts);
		optOutEventDao.changeCurrentOptOutCountState(energyCompany, counts);
		
	}
	
	@Override
	public void changeOptOutEnabledStateForToday(LiteYukonUser user, boolean optOutsEnabled) {

		Date now = new Date();
    	Date stopDate = TimeUtil.getMidnightTonight();
		optOutTemporaryOverrideDao.setTemporaryOptOutEnabled(user, now, stopDate, optOutsEnabled);
		
	}
	
	@Override
	public OptOutCountHolder getCurrentOptOutCount(int inventoryId, int customerAccountId) {

		DateTime dateTime = new DateTime();
		int currentMonth = dateTime.getMonthOfYear();
		

		// Get the Opt Out limits for the user
		CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
		LiteContact contact = customerDao.getPrimaryContact(customerAccount.getCustomerId());
		
		String optOutLimitString = 
			authDao.getRolePropertyValue(contact.getLoginID(), ResidentialCustomerRole.OPT_OUT_LIMITS);
		
		List<OptOutLimit> optOutLimits = this.parseOptOutLimitString(optOutLimitString);
		int optOutLimit = OptOutService.NO_OPT_OUT_LIMIT;
		Date startDate = new Date(0);
		for(OptOutLimit limit : optOutLimits) {
			if(limit.getStartMonth() <= currentMonth && limit.getStopMonth() >= currentMonth) {
				int limitStartMonth = limit.getStartMonth();
				optOutLimit = limit.getLimit();

				// Get the first day of the start month of the limit at midnight
				DateTime startDateTime = new DateTime();
				startDateTime = startDateTime.withMonthOfYear(limitStartMonth);
				startDateTime = startDateTime.withDayOfMonth(1);
				startDateTime = startDateTime.withTime(0, 0, 0, 0);
				startDate = startDateTime.toDate();
				break;
			}
		}

		// Get the number of opt outs used from the start of the limit (if there is a limit)
		// till now
		Integer usedOptOuts = optOutEventDao.getNumberOfOptOutsUsed(
				inventoryId, customerAccountId, startDate, new Date());
		
		// Get opt out limit for group
		int remainingOptOuts = 0;
		if (optOutLimit == OptOutService.NO_OPT_OUT_LIMIT) {
			remainingOptOuts = optOutLimit;
		} else {
			int additionalOptOuts = optOutAdditionalDao.getAdditionalOptOuts(
					inventoryId, customerAccountId);

			remainingOptOuts = optOutLimit + additionalOptOuts - usedOptOuts;
			if (remainingOptOuts < 0) {
				remainingOptOuts = 0;
			}
		}
		
		OptOutCountHolder holder = new OptOutCountHolder();
		holder.setInventory(inventoryId);
		holder.setUsedOptOuts(usedOptOuts);
		holder.setRemainingOptOuts(remainingOptOuts);
		
		return holder;
	}
	

	@Override
	public List<OptOutLimit> getAllOptOutLimits(LiteYukonGroup group) {

		String optOutLimitString = 
			roleDao.getRolePropValueGroup(group, ResidentialCustomerRole.OPT_OUT_LIMITS, "");
		
		List<OptOutLimit> optOutLimits = this.parseOptOutLimitString(optOutLimitString);
		
		return optOutLimits;
	}
	
	@Override
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId) {
		
		OptOutCountHolder currentOptOutCount = 
			this.getCurrentOptOutCount(inventoryId, accountId);

		// Add additional opt outs to 'erase' any used opt outs
		int usedOptOuts = currentOptOutCount.getUsedOptOuts();
		optOutAdditionalDao.addAdditonalOptOuts(inventoryId, accountId, usedOptOuts);
		
	}
	
	@Override
	public List<OverrideHistory> getOptOutHistoryForAccount(
			String accountNumber, Date startTime, Date stopTime,
			LiteYukonUser user) {

		List<OverrideHistory> historyList = new ArrayList<OverrideHistory>();
		
		CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, user);

		List<OverrideHistory> inventoryHistoryList = 
			optOutEventDao.getOptOutHistoryForInventory(account.getAccountId(), startTime, stopTime);
		
		for(OverrideHistory history : inventoryHistoryList) {
			Integer inventoryId = history.getInventoryId();
			
			List<Program> programList = 
				enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId, startTime, stopTime);
			
			// Create a history entry for each enrolled program
			for(Program program : programList) {
				OverrideHistory copy = history.getACopy();
				copy.setProgramName(program.getProgramName());
				
				historyList.add(copy);
			}
		}
		
		
		return historyList;
	}
	
	@Override
	public List<OverrideHistory> getOptOutHistoryByProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
		Program program = programDao.getByProgramName(programName, 
									Collections.singletonList(energyCompany.getEnergyCompanyID()));
		
		
		List<OverrideHistory> historyList = new ArrayList<OverrideHistory>();
		
		List<Integer> optedOutInventory = 
			enrollmentDao.getOptedOutInventory(program, startTime, stopTime);
		
		for(Integer inventoryId : optedOutInventory) {
			
			List<OverrideHistory> inventoryHistoryList = 
				optOutEventDao.getOptOutHistoryForInventory(inventoryId, startTime, stopTime);

			for(OverrideHistory history : inventoryHistoryList) {
				history.setProgramName(programName);
			}
			
			historyList.addAll(inventoryHistoryList);
			
		}
		
		return historyList;
	}
	
	@Override
	public int getOptOutDeviceCountForAccount(String accountNumber, Date startTime,
			Date stopTime, LiteYukonUser user) {

		CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, user);
		return optOutEventDao.getOptOutDeviceCountForAccount(account.getAccountId(), startTime, stopTime);
	}
	
	@Override
	public int getOptOutDeviceCountForProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user) {
		
		
		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
		Program program = programDao.getByProgramName(programName, 
									Collections.singletonList(energyCompany.getEnergyCompanyID()));
		List<Integer> optedOutInventory = 
			enrollmentDao.getOptedOutInventory(program, startTime, stopTime);
		
		return optedOutInventory.size();
	}
	
	@Override
	public void allowAdditionalOptOuts(String accountNumber,
			String serialNumber, int additionalOptOuts, LiteYukonUser user) 
		throws ObjectInOtherEnergyCompanyException {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
		CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, user);
		LiteInventoryBase inventory = 
			starsSearchDao.searchLMHardwareBySerialNumber(serialNumber, energyCompany);
		
		optOutAdditionalDao.addAdditonalOptOuts(
				inventory.getInventoryID(), account.getAccountId(), additionalOptOuts);
		
	}
	
	/**
	 * Helper method to parse a string into a list of opt out limits
	 * @param optOutLimitString - String containing opt out limit data
	 * @return List of limits or empty list if no limits set
	 */
	private List<OptOutLimit> parseOptOutLimitString(String optOutLimitString) {
		
		List<OptOutLimit> optOutLimits = new ArrayList<OptOutLimit>();
		
		if(StringUtils.isBlank(optOutLimitString)) {
			return optOutLimits;
		}
		
		JSONArray limitArray = new JSONArray(optOutLimitString);
		for(int i = 0; i < limitArray.length(); i++) {
			JSONObject limitObject = limitArray.getJSONObject(i);
			OptOutLimit limit = new OptOutLimit();
			limit.setStartMonth(limitObject.getInt("start"));
			limit.setStopMonth(limitObject.getInt("stop"));
			limit.setLimit(limitObject.getInt("limit"));
			optOutLimits.add(limit);
		}
		
		return optOutLimits;
		
	}
	
	/**
	 * Helper method to cancel a current opt out right now
	 * @param inventory - Inventory to cancel opt out for
	 * @param energyCompany - Inventory's energy company
	 * @param event - Opt out event being canceled
	 * @param customerAccount - Customer account for inventory
	 * @param userContext - User canceling opt out 
	 */
	private void cancelOptOutNow(
			LiteStarsLMHardware inventory, 
			LiteStarsEnergyCompany energyCompany, 
			OptOutEvent event, 
			CustomerAccount customerAccount, 
			YukonUserContext userContext) 
		throws CommandCompletionException {
		
		int inventoryId = inventory.getInventoryID();
		LiteYukonUser user = userContext.getYukonUser();
		
		// Send the command to cancel opt out to the field
		this.sendCancelRequest(inventory, energyCompany, user);
		
		event.setState(OptOutEventState.CANCEL_SENT);
		event.setStopDate(new Date());
		optOutEventDao.save(event, OptOutAction.CANCEL, user);
		
		// Update the LMHardwareControlGroup table
		List<LMHardwareConfiguration> configurationList = 
			lmHardwareControlGroupDao.getOldConfigDataByInventoryId(inventoryId);
		
		int accountId = customerAccount.getAccountId();
		for(LMHardwareConfiguration configuration : configurationList) {
    		
			int loadGroupId = configuration.getAddressingGroupId();
			
			lmHardwareControlInformationService.stopOptOut(
					inventoryId, 
					loadGroupId, 
					accountId, 
					user,
					event.getStopDate());
		}
		
		ActivityLogger.logEvent(
				user.getUserID(), 
				accountId, 
				energyCompany.getLiteID(), 
				customerAccount.getCustomerId(),
				ActivityLogActions.PROGRAM_REENABLE_ACTION, 
				"Serial #:" + inventory.getManufacturerSerialNumber());
		
		// Send re-enable (cancel opt out) notification
		try {
			OptOutRequest request = new OptOutRequest();
			request.setStartDate(event.getStartDate());
			request.setInventoryIdList(Collections.singletonList(inventoryId));
			request.setDurationInHours(event.getDurationInHours());
			request.setQuestions(new ArrayList<ScheduledOptOutQuestion>());
			
			optOutNotificationService.sendReenableNotification(
					customerAccount, 
					energyCompany, 
					request, 
					userContext);
		} catch (MessagingException e) {
			// Not much we can do - tried to send notification
			logger.error(e);
		}
		
	}

	/**
	 * Helper method to send the opt out command out to the device
	 * 
	 * @throws PaoAuthorizationException - If user is not authorized to send 
	 * 		opt out to the device
	 * @throws CommandCompletionException - If request is interrupted or times out
	 */
	private void sendOptOutRequest(LiteStarsLMHardware inventory,
			LiteStarsEnergyCompany energyCompany, int durationInHours, LiteYukonUser user) 
		throws CommandCompletionException {
		
		
		String serialNumber = inventory.getManufacturerSerialNumber();
		String durationString = String.valueOf(durationInHours);

		if (StringUtils.isBlank(serialNumber)) {
			throw new IllegalArgumentException("Cannot send opt out command. " +
					"The serial # of inventory with id: " + 
					inventory.getInventoryID() + " is empty." );
		}
        
		// Build the command
		
		StringBuffer cmd = new StringBuffer();
		cmd.append("putconfig serial ");
		cmd.append(inventory.getManufacturerSerialNumber());
		
		int hwConfigType = InventoryUtils.getHardwareConfigType( inventory.getLmHardwareTypeID() );
		if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
			// Versacom
			cmd.append(" vcom service out temp offhours ");
			cmd.append(durationString);
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
			// Expresscom
			cmd.append(" xcom service out temp offhours ");
			cmd.append(durationString);
			
			//if true, the opt out also includes a restore command so the switch gets both at once
			boolean restoreFirst = authDao.checkRoleProperty(
													user, 
													InventoryRole.EXPRESSCOM_TOOS_RESTORE_FIRST);
			if (restoreFirst) {
				cmd.append(" control restore load 0");
			}
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
			//SA205
			cmd.append(" sa205 service out temp offhours ");
			cmd.append(durationString);
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
			//SA305
			
			boolean trackHwAddr = authDao.checkRoleProperty(
														user, 
														EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
			if (!trackHwAddr) {
				throw new IllegalStateException("The utility ID of the SA305 switch is unknown");
			}
			
			int utilityId = inventory.getLMConfiguration().getSA305().getUtility();
			cmd.append(" sa305 utility ");
			cmd.append(utilityId);
			cmd.append(" override ");
			cmd.append(durationString);
		}
		
		// Send the command
		String commandString = cmd.toString();
		commandRequestRouteExecutor.execute(inventory.getRouteID(), commandString, user);
		
	}
	
	/**
	 * Helper method to send the cancel opt out command out to the device
	 * 
	 * @throws PaoAuthorizationException - If user is not authorized to send 
	 * 		cancel opt out to the device
	 * @throws CommandCompletionException - If request is interrupted or times out
	 */
	private void sendCancelRequest(LiteStarsLMHardware inventory,
			LiteStarsEnergyCompany energyCompany, LiteYukonUser user) 
		throws CommandCompletionException{

		String serialNumber = inventory.getManufacturerSerialNumber();

		if (StringUtils.isBlank(serialNumber)) {
			throw new IllegalArgumentException("Cannot send cancel opt out command. " +
					"The serial # of inventory with id: " + 
					inventory.getInventoryID() + " is empty." );
		}
		
		StringBuffer cmd = new StringBuffer();
		cmd.append("putconfig serial ");
		cmd.append(inventory.getManufacturerSerialNumber());
		
		int hwConfigType = InventoryUtils.getHardwareConfigType( inventory.getLmHardwareTypeID() );
		if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
			// Versacom
			cmd.append(" vcom service in temp");
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
			// Expresscom
			cmd.append(" xcom service in temp");
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
			// SA205
			cmd.append(" sa205 service out temp offhours 0");
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
			// SA305
			
			boolean trackHwAddr = authDao.checkRoleProperty(user,
					EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
			if (!trackHwAddr) {
				throw new IllegalStateException(
						"The utility ID of the SA305 switch is unknown");
			}
			
			int utilityId = inventory.getLMConfiguration().getSA305().getUtility();
			cmd.append(" sa305 utility ");
			cmd.append(utilityId);
			cmd.append(" override 0");
			
		}
		
		// Send the command
		String commandString = cmd.toString();
		commandRequestRouteExecutor.execute(inventory.getRouteID(), commandString, user);
		
	}
	
	@Autowired
	public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
	
	@Autowired
	public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}
	
	@Autowired
	public void setOptOutAdditionalDao(OptOutAdditionalDao optOutAdditionalDao) {
		this.optOutAdditionalDao = optOutAdditionalDao;
	}
	
	@Autowired
	public void setOptOutNotificationService(
			OptOutNotificationService optOutNotificationService) {
		this.optOutNotificationService = optOutNotificationService;
	}
	
	@Autowired
	public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
	@Autowired
	public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}
	
	@Autowired
	public void setCommandRequestRouteExecutor(
			CommandRequestRouteExecutor commandRequestRouteExecutor) {
		this.commandRequestRouteExecutor = commandRequestRouteExecutor;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
	
	@Autowired
	public void setOptOutStatusService(OptOutStatusService optOutStatusService) {
		this.optOutStatusService = optOutStatusService;
	}
	
	@Autowired
	public void setOptOutTemporaryOverrideDao(
			OptOutTemporaryOverrideDao optOutTemporaryOverrideDao) {
		this.optOutTemporaryOverrideDao = optOutTemporaryOverrideDao;
	}
	
	@Autowired
	public void setLmHardwareControlInformationService(
			LMHardwareControlInformationService lmHardwareControlInformationService) {
		this.lmHardwareControlInformationService = lmHardwareControlInformationService;
	}
	
	@Autowired
	public void setLmHardwareControlGroupDao(
			LMHardwareControlGroupDao lmHardwareControlGroupDao) {
		this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
	}
	
	@Autowired
	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
	@Autowired
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
	
	@Autowired
	public void setProgramDao(ProgramDao programDao) {
		this.programDao = programDao;
	}
	
	@Autowired
	public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
		this.energyCompanyDao = energyCompanyDao;
	}
	
	@Autowired
	public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}
	
	@Autowired
	public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
		this.starsSearchDao = starsSearchDao;
	}
	
}
