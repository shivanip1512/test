package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import javax.mail.MessagingException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.service.CommandRequestHardwareExecutor;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.AlreadyOptedOutException;
import com.cannontech.stars.dr.optout.exception.NotOptedOutException;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsDto;
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
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.user.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class OptOutServiceImpl implements OptOutService {

	private static final DateTimeFormatter logFormatter = DateTimeFormat.forPattern("MM/dd/yy HH:mm");
    private StarsInventoryBaseDao starsInventoryBaseDao;
	private OptOutEventDao optOutEventDao;
	private OptOutAdditionalDao optOutAdditionalDao;
	private OptOutNotificationService optOutNotificationService;
	private ECMappingDao ecMappingDao;
	private CustomerAccountDao customerAccountDao;
	private AuthDao authDao;
	private RolePropertyDao rolePropertyDao;
	private CommandRequestHardwareExecutor commandRequestHardwareExecutor;
	private StarsDatabaseCache starsDatabaseCache;
	private OptOutStatusService optOutStatusService;
	private OptOutTemporaryOverrideDao optOutTemporaryOverrideDao;
	private LMHardwareControlInformationService lmHardwareControlInformationService;
	private CustomerDao customerDao;
	private ProgramService programService;
	private EnrollmentDao enrollmentDao;
	private StarsSearchDao starsSearchDao;
	private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
	private SystemDateFormattingService systemDateFormattingService;
	private YukonUserDao yukonUserDao;
	private Executor executor;
	
	private final Logger logger = YukonLogManager.getLogger(OptOutServiceImpl.class);
	

	@Override
	@Transactional
	public void optOut(final CustomerAccount customerAccount, final OptOutRequest request, 
			final LiteYukonUser user) throws CommandCompletionException {
		
		int customerAccountId = customerAccount.getAccountId();
		final LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
		List<Integer> inventoryIdList = request.getInventoryIdList();
		ReadableInstant startDate = request.getStartDate();
		boolean startNow = false;
		Instant now = new Instant();
		if(startDate == null) {
			// Start now
			request.setStartDate(now);
			startNow = true;
		}
		
		OptOutCountsDto defaultOptOutCountsSetting = optOutStatusService.getDefaultOptOutCounts(user);
		
		Map<Integer, OptOutCountsDto> optOutCountsSettingsByProgramIdMap = new HashMap<Integer, OptOutCountsDto>();
		List<OptOutCountsDto> programSpecificOptOutCounts = optOutStatusService.getProgramSpecificOptOutCounts(user);
		for (OptOutCountsDto setting : programSpecificOptOutCounts) {
			optOutCountsSettingsByProgramIdMap.put(setting.getProgramId(), setting);
		}
		
		// Send opt out command immediately for each inventory
    	for(Integer inventoryId : inventoryIdList) { 

			LiteStarsLMHardware inventory = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
			List<DisplayableInventoryEnrollment> programs = displayableInventoryEnrollmentDao.find(customerAccount.getAccountId(), inventoryId);
			
			List<OptOutCountsDto> optOutCountSettingsForPrograms = Lists.newArrayList();
			for (DisplayableInventoryEnrollment program : programs) {
				int programId = program.getAssignedProgramId();
				OptOutCountsDto oocs = optOutCountsSettingsByProgramIdMap.get(programId);
				if (oocs != null) {
					optOutCountSettingsForPrograms.add(oocs);
				}
			}
				
			OptOutCountsDto optOutCountsDtoKeeper;
			if (optOutCountSettingsForPrograms.size() == 0) {
				optOutCountsDtoKeeper = defaultOptOutCountsSetting;
			} else {
				Ordering<OptOutCountsDto> ordering = Ordering.from(OptOutCountsDto.getStartTimeComparator());
				optOutCountsDtoKeeper = ordering.max(optOutCountSettingsForPrograms); // tie breaker, keep the one with most RECENT StartDate (max)
			}
			OptOutCounts optOutCountsKeeper = optOutCountsDtoKeeper.getOptOutCounts();
			
			// Create and save opt out event for this inventory
			OptOutEvent event = new OptOutEvent();
			event.setEventId(request.getEventId());
			event.setEventCounts(optOutCountsKeeper);
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
							user);
				}

				// Schedule the opt out 
				event.setState(OptOutEventState.SCHEDULED);
				optOutEventDao.save(event, OptOutAction.SCHEDULE, user);
		    	
				
			} else {
				// Do the opt out now 
				
				// Already opted out exception, for requests from UI
			    // Ignore/Cancel scheduled requests from automated OptOut task, if already opted out
			    boolean alreadyOptedOut = optOutEventDao.isOptedOut(inventoryId, customerAccountId);
			    if (alreadyOptedOut && request.getEventId() == null) {
			        throw new AlreadyOptedOutException(inventoryId, customerAccountId);
			    }
			    if (request.getEventId() != null) {
			        // Get any overdue scheduled opt out - that is the event we should be starting				    
			        OptOutEvent overdueEvent = 
			            optOutEventDao.getOptOutEventById(request.getEventId());

			        if (alreadyOptedOut) {
			            if (overdueEvent.getState().equals(OptOutEventState.SCHEDULED)) {
			                // Already opted out, cancel this scheduled one
			                this.cancelOptOut(Collections.singletonList(request.getEventId()), user);                            
			            } 
			            return;
			        } else {
			            if (!overdueEvent.getState().equals(OptOutEventState.SCHEDULED)) {
			                return;
			            }
			            event.setScheduledDate(overdueEvent.getScheduledDate());
			        }
			    }
                event.setState(OptOutEventState.START_OPT_OUT_SENT);
                
				// Send the command to the field
				this.sendOptOutRequest(
						inventory, 
						energyCompany, 
						event.getDurationInHours(), 
						user);

				optOutEventDao.save(event, OptOutAction.START_OPT_OUT, user);
				
				// Update the LMHardwareControlGroup table
				lmHardwareControlInformationService.startOptOut(inventoryId, customerAccountId, user, event.getStartDate());
			}
    		
	    	// Log the event
	    	StringBuffer logMsg = new StringBuffer();

	    	DateTimeFormatter dateTimeFormatter = 
	    	    logFormatter.withZone(energyCompany.getDefaultDateTimeZone());
	    	logMsg.append("Start Date/Time:" + dateTimeFormatter.print(event.getStartDate()));
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
    	Runnable notificationRunner = new Runnable() {
    		@Override
    		public void run() {
    			try {
    				optOutNotificationService.sendOptOutNotification(
    						customerAccount, 
    						energyCompany, 
    						request, 
    						user);
    			} catch (MessagingException e) {
    				// Not much we can do - tried to send notification
    				logger.error(e);
    			}
    		}
    	};
    	executor.execute(notificationRunner);

	}

	@Transactional
	public void resendOptOut(int inventoryId, int customerAccountId, LiteYukonUser user) 
		throws CommandCompletionException {

		// Verify that this inventory is currently opted out
		boolean optedOut = optOutEventDao.isOptedOut(inventoryId, customerAccountId);

		if (optedOut) {
			
		    LiteStarsLMHardware inventory = 
				(LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
			LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);
			
			OptOutEvent lastEvent = optOutEventDao.findLastEvent(inventoryId, customerAccountId);
			
			Instant now = new Instant();
			Duration optOutDuration = new Duration(now, lastEvent.getStopDate());
			int newDuration = optOutDuration.toPeriod().toStandardHours().getHours();
			
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
			
			optOutEventDao.saveOptOutLog(log);
			
		} else {
			throw new NotOptedOutException(inventoryId, customerAccountId);
		}
		
	}
	
	@Override
	@Transactional
	public void cancelOptOut(List<Integer> eventIdList, LiteYukonUser user) 
		throws CommandCompletionException {
		
	    Instant now = new Instant();
	    
		for(Integer eventId : eventIdList) {
			
			OptOutEvent event = optOutEventDao.getOptOutEventById(eventId);
			Integer inventoryId = event.getInventoryId();
			LiteStarsLMHardware inventory = 
				(LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
			LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);
    		CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(inventoryId);
    		
			OptOutEventState state = event.getState();
            if (OptOutEventState.START_OPT_OUT_SENT == state && 
                event.getStopDate().isAfter(now)) {
				// The opt out is active and the stop date is after now
				
				this.sendCancelCommandAndNotification(
						inventory, energyCompany, user, event, customerAccount);
				
				// Update event state
				event.setState(OptOutEventState.CANCEL_SENT);
				event.setStopDate(now);
				optOutEventDao.save(event, OptOutAction.CANCEL, user);
				
				this.cancelLMHardwareControlGroupOptOut(inventoryId, customerAccount, event, user);
				
			} else if (OptOutEventState.SCHEDULED == state) {
				// The opt out is scheduled but not active
				
				// Cancel the scheduled opt out
				event.setState(OptOutEventState.SCHEDULE_CANCELED);
				event.setScheduledDate(now);
				// No need to update start/stop date;
				// SCHEDULE_CANCELED entries are ignored by OptOut Counts logic, OptOut history WS calls
				// Control history is calculated from actual LMHardwareControlGroup entries.
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
							user);
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
	public void cancelAllOptOuts(LiteYukonUser user) {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
		List<OptOutEvent> currentOptOuts = optOutEventDao.getAllCurrentOptOuts(energyCompany);
		
		for (OptOutEvent ooe : currentOptOuts) {
			cancelOptOutEvent(ooe, energyCompany, user);
		}
	}
	
	@Override
	public void cancelAllOptOutsByProgramName(String programName, LiteYukonUser user) throws ProgramNotFoundException {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	Program program = programService.getByProgramName(programName, energyCompany);
    	int programId = program.getProgramId();
    	
		List<OptOutEvent> currentOptOuts = optOutEventDao.getAllCurrentOptOutsByProgramId(programId, energyCompany);
		
		for (OptOutEvent ooe : currentOptOuts) {
			cancelOptOutEvent(ooe, energyCompany, user);
		}
	}
	
	private void cancelOptOutEvent(OptOutEvent ooe, LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {
			
		Integer inventoryId = ooe.getInventoryId();
		LiteStarsLMHardware inventory = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
		CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(inventoryId);
		
		cancelSingleOptOut(ooe, customerAccount, inventory, energyCompany, user);
	}
	
	private void cancelSingleOptOut(OptOutEvent event, CustomerAccount customerAccount, LiteStarsLMHardware inventory, LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {
		
		try {
			
			this.sendCancelCommandAndNotification(inventory, energyCompany, user, event, customerAccount);
			
			// Update event state
			event.setState(OptOutEventState.CANCEL_SENT);
			event.setStopDate(new Instant());
			
			// Update the count state to don't count since we force-canceled this opt out
			event.setEventCounts(OptOutCounts.DONT_COUNT);
			
			optOutEventDao.save(event, OptOutAction.CANCEL, user);
			
			this.cancelLMHardwareControlGroupOptOut(inventory.getInventoryID(), customerAccount, event, user);
			
		} catch (CommandCompletionException e) {
			// Can't do much - tried to cancel opt out.  Log the error and 
			// continue to cancel other opt outs
			logger.error(e);
		}
	}
	
	@Override
	public void changeOptOutCountStateForToday(LiteYukonUser user, boolean optOutCounts) {
		doChangeOptOutCountStateForToday(user, optOutCounts, null);
	}
	
	@Override
	public void changeOptOutCountStateForTodayByProgramName(LiteYukonUser user, boolean optOutCounts, String programName) throws ProgramNotFoundException {
		
		// enforce program name is not blank here so that private method doChangeOptOutCountStateForToday() does not treat it as a changeOptOutCountStateForToday() call.
		if (StringUtils.isBlank(programName)) {
			throw new ProgramNotFoundException("Blank program name.");
		}
		
		doChangeOptOutCountStateForToday(user, optOutCounts, programName);
	}
	
	private void doChangeOptOutCountStateForToday(LiteYukonUser user, boolean optOutCounts, String programName) {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
		Integer webpublishingProgramId = null;
		
		if (!StringUtils.isBlank(programName)) {
			Program program = programService.getByProgramName(programName, energyCompany);
			webpublishingProgramId = program.getProgramId();
		}
		
		TimeZone systemTimeZone = systemDateFormattingService.getSystemTimeZone();
		Date now = new Date();
    	Date stopDate = TimeUtil.getMidnightTonight(systemTimeZone);
		
    	if (webpublishingProgramId == null) {
    	
	    	// Temporarily update count state
	    	optOutTemporaryOverrideDao.setTemporaryOptOutCounts(user, now, stopDate, optOutCounts);
	    	
	    	// Update any currently active opt outs
			optOutEventDao.changeCurrentOptOutCountState(energyCompany, OptOutCounts.valueOf(optOutCounts));
    	
    	} else {
    		
    		// Temporarily update count state
	    	optOutTemporaryOverrideDao.setTemporaryOptOutCountsForProgramId(user, now, stopDate, optOutCounts, webpublishingProgramId);
	    	
	    	// Update any currently active opt outs
			optOutEventDao.changeCurrentOptOutCountStateForProgramId(energyCompany, OptOutCounts.valueOf(optOutCounts), webpublishingProgramId);
    	}
	}
	
	@Override
	public void changeOptOutEnabledStateForToday(LiteYukonUser user, boolean optOutsEnabled) {

		TimeZone systemTimeZone = systemDateFormattingService.getSystemTimeZone();
		Date now = new Date();
    	Date stopDate = TimeUtil.getMidnightTonight(systemTimeZone);
		optOutTemporaryOverrideDao.setTemporaryOptOutEnabled(user, now, stopDate, optOutsEnabled);
		
	}
	
	@Override
	public OptOutCountHolder getCurrentOptOutCount(int inventoryId, int customerAccountId) {

		DateTime dateTime = new DateTime();
		int currentMonth = dateTime.getMonthOfYear();
		
		// Get the Opt Out limits for the user
		CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
		LiteContact contact = customerDao.getPrimaryContact(customerAccount.getCustomerId());

		// Get the consumer user's time zone for date calculation
		int userId = contact.getLoginID();
		LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
		
		int optOutLimit = OptOutService.NO_OPT_OUT_LIMIT;
		Date startDate = new Date(0);

		// The account we are looking at doesn't have a login, therefore there are no limits
		if(user.getUserID() != UserUtils.USER_DEFAULT_ID) {
			TimeZone userTimeZone = authDao.getUserTimeZone(user);
			OptOutLimit currentOptOutLimit = this.getCurrentOptOutLimit(user);
			if(currentOptOutLimit != null) {
				optOutLimit = currentOptOutLimit.getLimit();
				startDate = currentOptOutLimit.getOptOutLimitStartDate(currentMonth, userTimeZone);
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
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId) {
		
		OptOutCountHolder currentOptOutCount = 
			this.getCurrentOptOutCount(inventoryId, accountId);

		int usedOptOuts = currentOptOutCount.getUsedOptOuts();
		int additionalOptOuts = optOutAdditionalDao.getAdditionalOptOuts(inventoryId, accountId);
		
		int optOutsToAdd = usedOptOuts - additionalOptOuts;
		if(optOutsToAdd > 0) {
			optOutAdditionalDao.addAdditonalOptOuts(inventoryId, accountId, optOutsToAdd);
		}
	}
	
	@Override
	public void resetOptOutLimitForInventory(String accountNumber, String serialNumber, LiteYukonUser user) throws InventoryNotFoundException, AccountNotFoundException, IllegalArgumentException {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
		
		// account
		CustomerAccount account = null;
		try {
			account = customerAccountDao.getByAccountNumber(accountNumber, user);
		} catch (NotFoundException e) {
			throw new AccountNotFoundException("Account not found: " + accountNumber, e);
		}
		
		// inventory
		LiteInventoryBase inventory;
		try {
			inventory = starsSearchDao.searchLMHardwareBySerialNumber(serialNumber, energyCompany);
		} catch (ObjectInOtherEnergyCompanyException e) {
			throw new InventoryNotFoundException("Inventory with serial number: " + serialNumber + " is in another energy company.", e);
		}
		if(inventory == null) {
			throw new InventoryNotFoundException("Inventory with serial number: " + serialNumber + " could not be found.");
		}
		if(inventory.getAccountID() != account.getAccountId()) {
			throw new IllegalArgumentException("The inventory with serial number: " + serialNumber + " is not associated with the account with account number: " + accountNumber);
		}
		
		// resetOptOutLimitForInventory
		this.resetOptOutLimitForInventory(inventory.getInventoryID(), account.getAccountId());
	}
	
	@Override
	public List<OverrideHistory> getOptOutHistoryForAccount(
			String accountNumber, Date startTime, Date stopTime,
			LiteYukonUser user, String programName) 
		throws AccountNotFoundException, ProgramNotFoundException {

		List<OverrideHistory> historyList = new ArrayList<OverrideHistory>();
		
		CustomerAccount account = null;
		try {
			account = customerAccountDao.getByAccountNumber(accountNumber, user);
		} catch (NotFoundException e) {
			throw new AccountNotFoundException("Account not found: " + accountNumber, e);
		}

		List<OverrideHistory> inventoryHistoryList = 
			optOutEventDao.getOptOutHistoryForAccount(account.getAccountId(), startTime, stopTime);
		
		// See if the optional programName parameter was set - if so, only create
		// history objects for that program
		if (programName != null) {
		    LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
			Program program = programService.getByProgramName(programName, energyCompany);
			
			List<Integer> optedOutInventory = 
				enrollmentDao.getOptedOutInventory(program, startTime, stopTime);
			for (OverrideHistory history : inventoryHistoryList) {
				
				// Only add the history for inventory that was opted out of the given program
				Integer inventoryId = history.getInventoryId();
				if(optedOutInventory.contains(inventoryId)) {
					history.setPrograms(Collections.singletonList(program));
					historyList.add(history);
				}
			}
		} else {

			// For each overrideHistory, get the programs the opted out device was in at the time and
			// create a history object for each program
			for(OverrideHistory history : inventoryHistoryList) {
				Integer inventoryId = history.getInventoryId();
				
				List<Program> programList = 
					enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId, startTime, stopTime);
				
				// Create a history entry for each enrolled program
				for(Program program : programList) {
					
					OverrideHistory copy = history.getACopy();
					copy.setPrograms(Collections.singletonList(program));
						
					historyList.add(copy);
				}
	
			}
		}
		
		
		return historyList;
	}
	
	@Override
	public List<OverrideHistory> getOptOutHistoryByProgram(String programName, Date startTime, Date stopTime, LiteYukonUser user) throws ProgramNotFoundException {

		Validate.isTrue(startTime.before(stopTime), "Start time must be before stop time.");
		
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        Program program = programService.getByProgramName(programName, energyCompany);
		
		List<OverrideHistory> historyList = new ArrayList<OverrideHistory>();
		
		// Get the opted out inventory by program and time period
		List<Integer> optedOutInventory = enrollmentDao.getOptedOutInventory(program, startTime, stopTime);
		
		// For each inventory, get the opt out event and create the override history object
		for(Integer inventoryId : optedOutInventory) {
			
			List<Program> programList = enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId, startTime, stopTime);
			
			List<OverrideHistory> inventoryHistoryList = optOutEventDao.getOptOutHistoryForInventory(inventoryId, startTime, stopTime);

			for(OverrideHistory history : inventoryHistoryList) {
				history.setPrograms(programList);
			}
			
			historyList.addAll(inventoryHistoryList);
		}
		
		return historyList;
	}
	
	@Override
	public int getOptOutDeviceCountForAccount(String accountNumber, Date startTime,
			Date stopTime, LiteYukonUser user, String programName)
		throws  AccountNotFoundException, ProgramNotFoundException {
		
		Validate.isTrue(startTime.before(stopTime), "Start time must be before stop time.");
		
		CustomerAccount account = null;
		try {
			account = customerAccountDao.getByAccountNumber(accountNumber, user);
		} catch (NotFoundException e) {
			throw new AccountNotFoundException("Account not found: " + accountNumber, e);
		}
		List<Integer> optedOutInventory = 
			optOutEventDao.getOptedOutDeviceIdsForAccount(account.getAccountId(), startTime, stopTime);
		
		int numberOfDevices = optedOutInventory.size();
		
		if(programName != null) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
            Program program = programService.getByProgramName(programName, energyCompany);
			List<Integer> programInventory = 
				enrollmentDao.getOptedOutInventory(program, startTime, stopTime);

			// Get the ids that are opted out for the account AND the program
			Set<Integer> accountIdSet = new HashSet<Integer>(optedOutInventory);
			Set<Integer> programIdSet = new HashSet<Integer>(programInventory);
			accountIdSet.retainAll(programIdSet);

			numberOfDevices = accountIdSet.size();
		}
		
		return numberOfDevices;
	}
	
	@Override
	public int getOptOutDeviceCountForProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user) throws ProgramNotFoundException{
		
		Validate.isTrue(startTime.before(stopTime), "Start time must be before stop time.");
		
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        Program program = programService.getByProgramName(programName, energyCompany);
		List<Integer> optedOutInventory = 
			enrollmentDao.getOptedOutInventory(program, startTime, stopTime);
		
		return optedOutInventory.size();
	}
	
	@Override
	public void allowAdditionalOptOuts(String accountNumber,
			String serialNumber, int additionalOptOuts, LiteYukonUser user) 
		throws InventoryNotFoundException, AccountNotFoundException {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
		
		CustomerAccount account = null;
		try {
			account = customerAccountDao.getByAccountNumber(accountNumber, user);
		} catch (NotFoundException e) {
			throw new AccountNotFoundException("Account not found: " + accountNumber, e);
		}
		
		LiteInventoryBase inventory;
		try {
			inventory = starsSearchDao.searchLMHardwareBySerialNumber(serialNumber, energyCompany);
		} catch (ObjectInOtherEnergyCompanyException e) {
			throw new InventoryNotFoundException("Inventory with serial number: " + serialNumber + 
			" is in another energy company.", e);
		}
		if(inventory == null) {
			throw new InventoryNotFoundException("Inventory with serial number: " + serialNumber + 
					" could not be found.");
		}
		
		if(inventory.getAccountID() != account.getAccountId()) {
			throw new IllegalArgumentException("The inventory with serial number: " + serialNumber + 
					" is not associated with the account with account number: " + accountNumber);
		}
		
		optOutAdditionalDao.addAdditonalOptOuts(
				inventory.getInventoryID(), account.getAccountId(), additionalOptOuts);
		
	}
	
	@Override
	@Transactional(propagation = Propagation.NEVER)
	public void cleanUpCancelledOptOut(LiteStarsLMHardware inventory,
			LiteStarsEnergyCompany energyCompany, OptOutEvent event,
			CustomerAccount customerAccount, LiteYukonUser user) 
		throws CommandCompletionException {

		this.cancelLMHardwareControlGroupOptOut(
				inventory.getInventoryID(), customerAccount, event, user);

		this.sendCancelCommandAndNotification(
				inventory, energyCompany, user, event, customerAccount);
		
		
	}
	
	@Override
	public OptOutLimit getCurrentOptOutLimit(int customerAccountId) {
		
		CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
		LiteContact contact = customerDao.getPrimaryContact(customerAccount.getCustomerId());

		// Get the consumer user's time zone for date calculation
		int userId = contact.getLoginID();
		LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
		
		return this.getCurrentOptOutLimit(user);
	};
	
	@Override
	public List<Integer> getAvailableOptOutPeriods(LiteYukonUser user) {
	    
        String optOutPeriodString = null;
        if (StarsUtils.isOperator(user)) {
            optOutPeriodString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_OPT_OUT_PERIOD,
                                                                        user);
        } else {
            optOutPeriodString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_PERIOD,
                                                                        user);
        }
        List<Integer> availOptOutPeriods = parseOptOutPeriodString(optOutPeriodString);
        return availOptOutPeriods;
    }
	
	private OptOutLimit getCurrentOptOutLimit(LiteYukonUser user) {
		
		// The account we are looking at doesn't have a login, therefore there are no limits
		if(user.getUserID() != UserUtils.USER_DEFAULT_ID) {
			DateTime dateTime = new DateTime();
			int currentMonth = dateTime.getMonthOfYear();
			
			String optOutLimitString = 
				rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_LIMITS, user);
			
			List<OptOutLimit> optOutLimits = this.parseOptOutLimitString(optOutLimitString);
			for (OptOutLimit limit : optOutLimits) {
	            if (limit.isMonthUnderLimit(currentMonth)) {
	                return limit;
	            }
	        }
		}
		
		return null;
	}
	
	/**
	 * Helper method to send the opt out cancel command and cancel notification message
	 * @param inventory - Inventory to cancel opt out on
	 * @param energyCompany - Inventory's energy company
	 * @param user - User requesting the cancel
	 * @param event - Event being canceled
	 * @param customerAccount - Inventory's account
	 * @throws CommandCompletionException
	 */
	private void sendCancelCommandAndNotification(
			LiteStarsLMHardware inventory, LiteStarsEnergyCompany energyCompany, LiteYukonUser user, 
			OptOutEvent event, CustomerAccount customerAccount) 
		throws CommandCompletionException {
		
		int inventoryId = inventory.getInventoryID();
		int accountId = customerAccount.getAccountId();
		
		// Send the command to cancel opt out to the field
		this.sendCancelRequest(inventory, energyCompany, user);
		
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
					user);
		} catch (MessagingException e) {
			// Not much we can do - tried to send notification
			logger.error(e);
		}
	}

	/**
	 * Helper method to update the LMHardwareControlGroup opt out row when an opt out is complete
	 * or canceled
	 * @param inventoryId - Inventory to cancel opt out for
	 * @param customerAccount - Intentory's account
	 * @param event - Event being canceled
	 * @param user - User requesting the cancel
	 */
	private void cancelLMHardwareControlGroupOptOut(
			int inventoryId, CustomerAccount customerAccount, OptOutEvent event, LiteYukonUser user) {
		
		// Update the LMHardwareControlGroup table
	    lmHardwareControlInformationService.stopOptOut(inventoryId, customerAccount.getAccountId(), user, event.getStopDate());
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
	
    private List<Integer> parseOptOutPeriodString(String optOutPeriodString) {

        List<Integer> optOutPeriodInts = new ArrayList<Integer>();
        try {
            if (!StringUtils.isBlank(optOutPeriodString)) {
                String[] optOutPeriodStrs = StringUtils.split(optOutPeriodString, ',');
                for (String optOutPeriodStr : optOutPeriodStrs) {
                    optOutPeriodInts.add(Integer.valueOf(optOutPeriodStr.trim()));
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Can't parse OptOutPeriod role property value [" + optOutPeriodString + "]", e);
        }
        
        // default to 1 day, if value not set
        if (optOutPeriodInts.isEmpty()) {
            optOutPeriodInts.add(1);
        }

        return optOutPeriodInts;
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
			boolean restoreFirst = rolePropertyDao.checkProperty(
					YukonRoleProperty.EXPRESSCOM_TOOS_RESTORE_FIRST, user);
			
			if (restoreFirst) {
				cmd.append(" control restore load 0");
			}
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
			//SA205
			cmd.append(" sa205 service out temp offhours ");
			cmd.append(durationString);
		} else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
			//SA305
			
			boolean trackHwAddr = rolePropertyDao.checkProperty(
					YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, user);
			
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
		commandRequestHardwareExecutor.execute(inventory, commandString, user);
		
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
			
			boolean trackHwAddr = rolePropertyDao.checkProperty(
					YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, user);
			
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
		commandRequestHardwareExecutor.execute(inventory, commandString, user);
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
	public void setCommandRequestHardwareExecutor(
			CommandRequestHardwareExecutor commandRequestHardwareExecutor) {
		this.commandRequestHardwareExecutor = commandRequestHardwareExecutor;
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
	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
	
	@Autowired
	public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}
	
	@Autowired
	public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
		this.starsSearchDao = starsSearchDao;
	}

	@Autowired
	public void setDisplayableInventoryEnrollmentDao(
            DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao) {
        this.displayableInventoryEnrollmentDao = displayableInventoryEnrollmentDao;
    }

    @Autowired
	public void setSystemDateFormattingService(
			SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
	
	@Autowired
	public void setYukonUserDao(YukonUserDao yukonUserDao) {
		this.yukonUserDao = yukonUserDao;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setExecutor(@Qualifier("main") Executor executor) {
		this.executor = executor;
	}
}
