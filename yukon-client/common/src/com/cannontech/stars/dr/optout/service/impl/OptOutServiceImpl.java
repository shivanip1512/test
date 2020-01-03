package com.cannontech.stars.dr.optout.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Result;
import com.cannontech.common.util.OpenInterval;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.AlreadyOptedOutException;
import com.cannontech.stars.dr.optout.exception.InvalidOptOutDurationException;
import com.cannontech.stars.dr.optout.exception.InvalidOptOutStartDateException;
import com.cannontech.stars.dr.optout.exception.NotOptedOutException;
import com.cannontech.stars.dr.optout.exception.OptOutAlreadyScheduledException;
import com.cannontech.stars.dr.optout.exception.OptOutCountLimitException;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
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
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class OptOutServiceImpl implements OptOutService {

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private DisplayableInventoryDao displayableInventoryDao;
    @Autowired private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private EnergyCompanyService ecService;
    @Autowired @Qualifier("main") private Executor executor;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private LmHardwareCommandService lmHardwareCommandService;
    @Autowired private LMHardwareControlInformationService lmHardwareControlInformationService;
    @Autowired private OptOutSurveyDao optOutSurveyDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private OptOutAdditionalDao optOutAdditionalDao;
    @Autowired private OptOutNotificationService optOutNotificationService;
    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private OptOutTemporaryOverrideDao optOutTemporaryOverrideDao;
    @Autowired private ProgramService programService;
    @Autowired private RawExpressComCommandBuilder rawExpressComCommandBuilder;  
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private SurveyDao surveyDao;
    @Autowired private SystemDateFormattingService systemDateFormattingService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonUserDao yukonUserDao;

    private static final DateTimeFormatter logFormatter = DateTimeFormat.forPattern("MM/dd/yy HH:mm");
    private final ObjectMapper jsonObjectMapper = new ObjectMapper();
	
	private final Logger logger = YukonLogManager.getLogger(OptOutServiceImpl.class);
	
	public OptOutServiceImpl() {
	    // We made up our own form of JSON, so we need to support it:
        jsonObjectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jsonObjectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	@Override
    @Transactional
    public void optOut(final CustomerAccount customerAccount, final OptOutRequest request, 
            final LiteYukonUser user, final OptOutCounts optOutCounts) throws CommandCompletionException {
	    
	    int customerAccountId = customerAccount.getAccountId();
        final YukonEnergyCompany yukonEnergyCompany = 
            ecDao.getEnergyCompanyByAccountId(customerAccount.getAccountId());
        
        List<Integer> inventoryIdList = request.getInventoryIdList();
        ReadableInstant startDate = request.getStartDate();
        boolean startNow = false;
        Instant now = new Instant();
        if(startDate == null) {
            // Start now
            request.setStartDate(now);
            startNow = true;
        }
        
        Map<Integer, Integer> surveyResultIdsBySurveyId = Maps.newHashMap();
        if (request.getSurveyResults() != null) {
            for (Result result : request.getSurveyResults()) {
                surveyDao.saveResult(result);
                surveyResultIdsBySurveyId.put(result.getSurveyId(), result.getSurveyResultId());
            }
        }
        
        OptOutCountsTemporaryOverride defaultOptOutCountsSetting = optOutStatusService.getDefaultOptOutCounts(user); 
        Map<Integer, OptOutCountsTemporaryOverride> optOutCountsSettingsByProgramIdMap = Maps.newHashMap();
        List<OptOutCountsTemporaryOverride> programSpecificOptOutCounts = optOutStatusService.getProgramSpecificOptOutCounts(user);
        for (OptOutCountsTemporaryOverride setting : programSpecificOptOutCounts) {
            optOutCountsSettingsByProgramIdMap.put(setting.getAssignedProgramId(), setting);
        }
        
        for(Integer inventoryId : inventoryIdList) { 
            
            LiteLmHardwareBase inventory = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
            OptOutCounts optOutCountsKeeper = null;
            
            // No OptOutCounts settings were passed, use the default settings
            if(optOutCounts==null){
                
                List<DisplayableInventoryEnrollment> programs = displayableInventoryEnrollmentDao.find(customerAccount.getAccountId(), inventoryId);
                
                List<OptOutCountsTemporaryOverride> optOutCountSettingsForPrograms = Lists.newArrayList();
                for (DisplayableInventoryEnrollment program : programs) {
                    int programId = program.getAssignedProgramId();
                    OptOutCountsTemporaryOverride oocs = optOutCountsSettingsByProgramIdMap.get(programId);
                    if (oocs != null) {
                        optOutCountSettingsForPrograms.add(oocs);
                    }
                }
                    
                OptOutCountsTemporaryOverride optOutCountsDtoKeeper;
                if (optOutCountSettingsForPrograms.size() == 0) {
                    optOutCountsDtoKeeper = defaultOptOutCountsSetting;
                } else {
                    Ordering<OptOutCountsTemporaryOverride> ordering = Ordering.from(OptOutCountsTemporaryOverride.getStartTimeComparator());
                    optOutCountsDtoKeeper = ordering.max(optOutCountSettingsForPrograms); // tie breaker, keep the one with most RECENT StartDate (max)
                }
                optOutCountsKeeper = optOutCountsDtoKeeper.getOptOutCounts();
            } else {
                optOutCountsKeeper = optOutCounts;
            }
            
         // Create and save opt out event for this inventory
            OptOutEvent event = new OptOutEvent();
            event.setEventId(request.getEventId());
            event.setEventCounts(optOutCountsKeeper);
            event.setCustomerAccountId(customerAccountId);
            event.setInventoryId(inventoryId);
            event.setScheduledDate(now);
            event.setStartDate(request.getStartDate());
            event.setStopDate(request.getStopDate());

            OptOutLog optOutEventLog = null;
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
                optOutEventLog = optOutEventDao.save(event, OptOutAction.SCHEDULE, user);
            } else {
                // Do the opt out now 
                
                // Already opted out exception, for requests from UI
                // Ignore/Cancel scheduled requests from automated OptOut task, if already opted out
                boolean alreadyOptedOut = optOutEventDao.isOptedOut(inventoryId, customerAccountId);
                if (alreadyOptedOut && request.getEventId() == null) {
                        throw new AlreadyOptedOutException(inventoryId);
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
                    }
                    if (!overdueEvent.getState().equals(OptOutEventState.SCHEDULED)) {
                        return;
                    }
                    event.setScheduledDate(overdueEvent.getScheduledDate());
                }
                event.setState(OptOutEventState.START_OPT_OUT_SENT);
                
                // Send the command to the field
                sendOptOutRequest(inventory, event.getDurationInHours(), user);

                optOutEventLog = optOutEventDao.save(event, OptOutAction.START_OPT_OUT, user);

                // Update the LMHardwareControlGroup table
                lmHardwareControlInformationService.startOptOut(inventoryId, customerAccountId, user, event);
            }
            
            if (request.getSurveyIdsByInventoryId() != null &&
                    request.getSurveyIdsByInventoryId().get(inventoryId) != null) {
                    for (Integer surveyId : request.getSurveyIdsByInventoryId().get(inventoryId)) {
                        int surveyResultId = surveyResultIdsBySurveyId.get(surveyId);
                        optOutSurveyDao.saveResult(surveyResultId, optOutEventLog.getLogId());
                    }
                }

                // Log the event
                accountEventLogService.deviceOptedOut(user, customerAccount.getAccountNumber(), 
                                                      inventory.getManufacturerSerialNumber(),
                                                      request.getStartDate(), request.getStopDate());
                
                StringBuffer logMsg = new StringBuffer();

                TimeZone ecTimeZone = ecService.getDefaultTimeZone(yukonEnergyCompany.getEnergyCompanyId());
                DateTimeZone energyCompanyTimeZone = DateTimeZone.forTimeZone(ecTimeZone);
                DateTimeFormatter dateTimeFormatter = logFormatter.withZone(energyCompanyTimeZone);
                logMsg.append("Start Date/Time:" + dateTimeFormatter.print(event.getStartDate()));
                logMsg.append(", Duration:" + ServletUtils.getDurationFromHours(
                        event.getDurationInHours()));
                logMsg.append(", Serial #:" + inventory.getManufacturerSerialNumber());
                
                ActivityLogger.logEvent(
                        user.getUserID(), 
                        customerAccount.getAccountId(), 
                        yukonEnergyCompany.getEnergyCompanyId(), 
                        customerAccount.getCustomerId(),
                        ActivityLogActions.PROGRAM_OPT_OUT_ACTION, 
                        logMsg.toString());
            }

            // Send opt out notification
            Runnable notificationRunner = new Runnable() {
                @Override
                public void run() {
                    try {
                        EnergyCompany energyCompany = ecDao.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
                        optOutNotificationService.sendOptOutNotification(customerAccount, energyCompany, request, user);
                    } catch (MessagingException e) {
                        // Not much we can do - tried to send notification
                        logger.error(e);
                    }
                }
            };
            executor.execute(notificationRunner);

        }
	
	@Override
	@Transactional
	public void optOut(final CustomerAccount customerAccount, final OptOutRequest request, 
			final LiteYukonUser user) throws CommandCompletionException {
	    
	    optOut(customerAccount, request, user, null);
	}
	
    @Override
    @Transactional
    public void optOutWithValidation(final CustomerAccount customerAccount,
                                     final OptOutRequest request,
                                     final LiteYukonUser user, final OptOutCounts optOutCounts)
        throws CommandCompletionException, OptOutException {
        if (request.getStartDate() != null) {
            Instant currentTime = new Instant();
            Instant startTime = new Instant(request.getStartDate());

            if (startTime.isBefore(currentTime)) {
                throw new InvalidOptOutStartDateException();
            }
        }

        if (request.getDurationInHours() <= 0) {
            throw new InvalidOptOutDurationException();
        }

        for (Integer inventoryId : request.getInventoryIdList()) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            if (request.getStartDate() == null && 
                optOutEventDao.isOptedOut(lmHardwareBase.getInventoryId(), customerAccount.getAccountId())) {
                throw new AlreadyOptedOutException(lmHardwareBase.getManufacturerSerialNumber());
            }
            
            if(request.getStartDate()!=null) {
                OptOutEvent event = optOutEventDao.getScheduledOptOutEvent(lmHardwareBase.getInventoryId(),
                                                                           customerAccount.getAccountId());
                if (event != null) {
                    throw new OptOutAlreadyScheduledException(lmHardwareBase.getManufacturerSerialNumber());
                }   
            }
            
            if (optOutCounts == OptOutCounts.COUNT) {
                OptOutCountHolder optOutCountHolder = getCurrentOptOutCount(lmHardwareBase.getInventoryId(),
                                                                            customerAccount.getAccountId());
                if (!optOutCountHolder.isOptOutsRemaining()) {
                    throw new OptOutCountLimitException(lmHardwareBase.getManufacturerSerialNumber());
                }
            }
        }
        optOut(customerAccount, request, user, optOutCounts);
    }
    
	@Override
    @Transactional
	public void resendOptOut(int inventoryId, int customerAccountId, YukonUserContext userContext) 
		throws CommandCompletionException {

	    LiteYukonUser user = userContext.getYukonUser();
		// Verify that this inventory is currently opted out
		boolean optedOut = optOutEventDao.isOptedOut(inventoryId, customerAccountId);

		if (optedOut) {
			
		    LiteLmHardwareBase inventory = 
				(LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
		    CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
			
			OptOutEvent lastEvent = optOutEventDao.findLastEvent(inventoryId);
			DateTime now = new DateTime(userContext.getJodaTimeZone());
			Interval optOutInterval = new Interval(now, lastEvent.getStopDate());
			// Add today (1 day)
			int durationInDays = optOutInterval.toPeriod().toStandardDays().getDays()+1;
			LocalDate startDate = new LocalDate(now);
			Duration duration = calculateDuration(startDate, durationInDays, userContext);
            logger.info("Start date:" + startDate.toString("MM/dd/yy") + "  Duration in days:" + durationInDays
                        + " calculated duration in hours:" + duration.toStandardHours().getHours());

			// Send command out to the field
			sendOptOutRequest(inventory, duration.toStandardHours().getHours(), user);
			
			// Log this repeat event request
			accountEventLogService.optOutResent(user, customerAccount.getAccountNumber(), 
			                                    inventory.getDeviceLabel());
			
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
			LiteLmHardwareBase inventory = 
				(LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
			YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByInventoryId(inventoryId);
            CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(inventoryId);
    		
			OptOutEventState state = event.getState();
            if (OptOutEventState.START_OPT_OUT_SENT == state && 
                event.getStopDate().isAfter(now)) {
				
                // The opt out is active and the stop date is after now
				sendCancelCommandAndNotification(inventory, yukonEnergyCompany, user, event);
				
				// Update event state
				event.setState(OptOutEventState.CANCEL_SENT);
				event.setStopDate(now);
				optOutEventDao.save(event, OptOutAction.CANCEL, user);
				
				cancelLMHardwareControlGroupOptOut(inventoryId, event, user);
				
			} else if (OptOutEventState.SCHEDULED == state) {
				// The opt out is scheduled but not active
				
				// Cancel the scheduled opt out
				event.setState(OptOutEventState.SCHEDULE_CANCELED);
				event.setScheduledDate(now);
				// No need to update start/stop date;
				// SCHEDULE_CANCELED entries are ignored by OptOut Counts logic, OptOut history WS calls
				// Control history is calculated from actual LMHardwareControlGroup entries.
				optOutEventDao.save(event, OptOutAction.CANCEL_SCHEDULE, user);
				
				ActivityLogger.logEvent(user.getUserID(), customerAccount.getAccountId(), yukonEnergyCompany.getEnergyCompanyId(),
						customerAccount.getCustomerId(), ActivityLogActions.PROGRAM_CANCEL_SCHEDULED_ACTION, "");

				// Send cancel scheduled notification
				try {
					OptOutRequest request = new OptOutRequest();
					request.setStartDate(event.getStartDate());
					request.setInventoryIdList(Collections.singletonList(inventoryId));
					request.setDurationInHours(event.getDurationInHours());
					request.setQuestions(new ArrayList<ScheduledOptOutQuestion>());
					
					EnergyCompany energyCompany = ecDao.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
					optOutNotificationService.sendCancelScheduledNotification(customerAccount, energyCompany, request, user);
				} catch (MessagingException e) {
					// Not much we can do - tried to send notification
					logger.error(e);
				}
				
			} else {
				throw new NotOptedOutException(inventoryId, customerAccount.getAccountId());
			}
            
            accountEventLogService.optOutCanceled(user, customerAccount.getAccountNumber(), inventory.getDeviceLabel());
		}
	}

	@Override
	public void cancelAllOptOuts(LiteYukonUser user) {
	    logger.debug("Cancel all opt outs command initiated by user: " + user.getUsername());
	    
		EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
		List<OptOutEvent> currentOptOuts = optOutEventDao.getAllCurrentOptOuts(energyCompany);

        boolean broadCastSpidEnabled = energyCompanySettingDao.isEnabled(
            EnergyCompanySettingType.BROADCAST_OPT_OUT_CANCEL_SPID, energyCompany.getId());

		boolean validSpid;
		int broadcastSpid = 0;
		if (!broadCastSpidEnabled) {
		    validSpid = false;
		} else {
		    broadcastSpid = energyCompanySettingDao.getInteger(
		        EnergyCompanySettingType.BROADCAST_OPT_OUT_CANCEL_SPID, energyCompany.getId());
		    validSpid = rawExpressComCommandBuilder.isValidBroadcastSpid(broadcastSpid);
		}

		if (validSpid) {
		    // Valid SPID found, use broadcast messages.
		    broadcastCancelAllOptOuts(user, broadcastSpid, energyCompany, currentOptOuts);
		} else {
		    // No valid SPID found so use per-device messages.
		    logger.debug("Using per-device messaging for cancel all opt outs command.");
		    for (OptOutEvent ooe : currentOptOuts) {
		        cancelOptOutEvent(ooe, energyCompany, user);
		    }
		}
	}

	/**
     * This method will filter ExpressCom and non-ExpressCom devices and send broadcast 
     * messages for broadcast supported (ExpressCom) devices and handle other (non-ExpressCom)
     * individually.
     */
    private void broadcastCancelAllOptOuts(LiteYukonUser user, int broadcastSpid, YukonEnergyCompany energyCompany,
            List<OptOutEvent> currentOptOuts) {
        // Get all InventoryIdentifier based on currentOptOut
        List<Integer> inventoryIds = currentOptOuts.stream()
                                                   .map(currentOptOut -> currentOptOut.getInventoryId())
                                                   .collect(Collectors.toList());
        // InventoryIds for ExpressCom devices 
        List<Integer> filteredExpresscomInventoryIds = inventoryDao.getYukonInventory(inventoryIds)
                                                                   .stream()
                                                                   .filter(identifier -> (identifier.getHardwareType().isExpressCom()))
                                                                   .map(identifier -> identifier.getInventoryId())
                                                                   .collect(Collectors.toList());
        // List of OptOutEvents for ExpressCom devices
        List<OptOutEvent> currentOptOutsForExpresscom = currentOptOuts.stream()
                                                                      .filter(currentOptOut -> filteredExpresscomInventoryIds
                                                                                               .contains(currentOptOut.getInventoryId()))
                                                                      .collect(Collectors.toList());
        // List of OptOutEvents for non-ExpressCom devices
        List<OptOutEvent> currentOptOutsForNonExpresscom = currentOptOuts.stream()
                                                                         .filter(currentOptOut -> !filteredExpresscomInventoryIds
                                                                                                  .contains(currentOptOut.getInventoryId()))
                                                                         .collect(Collectors.toList());

        // Use broadcast messages for broadcast supported devices other will be handled individually.
        broadcastCancelAllOptOutsForExpresscom(user, broadcastSpid, energyCompany, currentOptOutsForExpresscom);
        // Cancel opt out individually for non-ExpressCom devices
        cancelAllOptOutsForNonExpresscom(user, energyCompany, currentOptOutsForNonExpresscom);
    }

    /**
     * This method cancels all active opt outs for non-ExpressCom devices.
     * non-ExpressCom devices do not accept broadcast messages.
     * This method will cancel all active opt outs individually.
     */
    private void cancelAllOptOutsForNonExpresscom(LiteYukonUser user, YukonEnergyCompany energyCompany,
            List<OptOutEvent> ooeForNonExpresscomInv) {
        for (OptOutEvent ooe : ooeForNonExpresscomInv) {
            cancelOptOutEvent(ooe, energyCompany, user);
        }

    }
	/**
	 * This method cancels all active opt outs using broadcast messaging.  
	 * One message is generated per communication protocol and is sent to
	 * all ExpressCom devices on the network using SPID addressing.  
	 * 
	 * @param user The user performing the broadcast cancel override.
	 * @param spid The SPID address to use in the ExpressCom messages.
	 * @param energyCompany The energy company used when looking up the SPID role property.
	 */
    private void broadcastCancelAllOptOutsForExpresscom(LiteYukonUser user, int spid, 
            YukonEnergyCompany energyCompany, List<OptOutEvent> currentOptOutsForExpresscom) {
        logger.debug("Using broadcast messaging for cancel all opt outs command.");
        LmCommand command = new LmCommand();

        command.setType(LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE);
        command.setUser(user);
        command.getParams().put(LmHardwareCommandParam.SPID, spid);
        
        // Send the broadcast command via all available strategies.
        lmHardwareCommandService.sendBroadcastCommand(command);
        // Perform logging.
        starsEventLogService.cancelCurrentOptOuts(user);
        Instant momentCancelled = new Instant();
        
        for (OptOutEvent ooe : currentOptOutsForExpresscom) {
            lmHardwareControlInformationService.stopOptOut(ooe.getInventoryId(), user, momentCancelled);

            LiteLmHardwareBase inventory = null;
            try {
                inventory = inventoryBaseDao.getHardwareByInventoryId(ooe.getInventoryId());
    
                // Record cancellation in OptOutEvent table.
                ooe.setState(OptOutEventState.CANCEL_SENT);
                ooe.setStopDate(momentCancelled);
                ooe.setEventCounts(OptOutCounts.DONT_COUNT);
                optOutEventDao.save(ooe, OptOutAction.CANCEL, user);
    
                int customerAccountId = ooe.getCustomerAccountId();
                CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
                
                // Log cancellations and send notifications.
                logCancelCommandAndSendNotification(inventory, energyCompany, user, customerAccount, ooe);
        
            } catch (NotFoundException e) {
                logger.warn("Inventory couldn't be found" + ooe.getInventoryId(), e);
            }
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
		
        // Log the successful cancellation of opt outs.
		if (StringUtils.isNotBlank(programName)) {
            starsEventLogService.cancelCurrentOptOutsByProgram(user, programName);
        } else {
            starsEventLogService.cancelCurrentOptOuts(user);
        }
	}
	
	private void cancelOptOutEvent(OptOutEvent ooe, YukonEnergyCompany energyCompany, LiteYukonUser user) {
			
		Integer inventoryId = ooe.getInventoryId();
		LiteLmHardwareBase inventory = null;
		try {
		    inventory = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
		} catch (NotFoundException e) {
		    // Inventory wasn't found, was probably deleted via web interface,
		    // In this case just change event to CANCEL_SENT and save it to DB
            ooe.setState(OptOutEventState.CANCEL_SENT);
            ooe.setStopDate(new Instant());
            ooe.setEventCounts(OptOutCounts.DONT_COUNT);
            optOutEventDao.save(ooe, OptOutAction.CANCEL, user);
            
            logger.warn("Unable to send cancel because inventory couldn't be found" + inventoryId, e);
            
            return;
		}
		
		try {
            sendCancelCommandAndNotification(inventory, energyCompany, user, ooe);
            
            // Update event state
            ooe.setState(OptOutEventState.CANCEL_SENT);
            ooe.setStopDate(new Instant());
            
            // Update the count state to don't count since we force-canceled this opt out
            ooe.setEventCounts(OptOutCounts.DONT_COUNT);
            
            optOutEventDao.save(ooe, OptOutAction.CANCEL, user);
            
            cancelLMHardwareControlGroupOptOut(inventory.getInventoryID(), ooe, user);
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
		
		if (StringUtils.isNotBlank(programName)) {
			Program program = programService.getByProgramName(programName, energyCompany);
			webpublishingProgramId = program.getProgramId();
		}
		
		TimeZone systemTimeZone = ecService.getDefaultTimeZone(energyCompany.getEnergyCompanyId());
		Date now = new Date();
    	Date stopDate = TimeUtil.getMidnightTonight(systemTimeZone);
		
    	if (webpublishingProgramId == null) {
    	
	    	// Temporarily update count state
	    	optOutTemporaryOverrideDao.setTemporaryOptOutCounts(user, now, stopDate, optOutCounts);
	    	
	    	// Update any currently active opt outs
			optOutEventDao.changeCurrentOptOutCountState(energyCompany, OptOutCounts.valueOf(optOutCounts));
			starsEventLogService.countTowardOptOutLimitToday(user, optOutCounts);
			
    	} else {
    		
    		// Temporarily update count state
	    	optOutTemporaryOverrideDao.setTemporaryOptOutCountsForProgramId(user, now, stopDate, optOutCounts, webpublishingProgramId);
	    	
	    	// Update any currently active opt outs
			optOutEventDao.changeCurrentOptOutCountStateForProgramId(energyCompany, OptOutCounts.valueOf(optOutCounts), webpublishingProgramId);
			starsEventLogService.countTowardOptOutLimitTodayForProgram(user, programName, optOutCounts);
    	}
	}
	
	@Override
	public void changeOptOutEnabledStateForToday(LiteYukonUser user, OptOutEnabled optOutsEnabled) {

		TimeZone systemTimeZone = systemDateFormattingService.getSystemTimeZone();
		Date now = new Date();
    	Date stopDate = TimeUtil.getMidnightTonight(systemTimeZone);

    	optOutTemporaryOverrideDao.setTemporaryOptOutEnabled(user, now, stopDate, optOutsEnabled);
        starsEventLogService.optOutUsageEnabledToday(user, optOutsEnabled.isOptOutEnabled(), optOutsEnabled.isCommunicationEnabled());
	}

    @Override
    public void changeOptOutEnabledStateForTodayByProgramName(LiteYukonUser user, OptOutEnabled optOutsEnabled,
                                                              String programName) throws ProgramNotFoundException {

        if (StringUtils.isBlank(programName)) {
            changeOptOutEnabledStateForToday(user, optOutsEnabled);
            return;
        }
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        Integer webpublishingProgramId = null;
        
        if (StringUtils.isNotBlank(programName)) {
            Program program = programService.getByProgramName(programName, energyCompany);
            webpublishingProgramId = program.getProgramId();
        }
        
        TimeZone systemTimeZone = systemDateFormattingService.getSystemTimeZone();
        Date now = new Date();
        Date stopDate = TimeUtil.getMidnightTonight(systemTimeZone);
        
        // Temporarily update enabled state
        optOutTemporaryOverrideDao.setTemporaryOptOutEnabled(user, now, stopDate, optOutsEnabled, webpublishingProgramId);
        starsEventLogService.optOutUsageEnabledTodayForProgram(user, programName, optOutsEnabled.isOptOutEnabled(), optOutsEnabled.isCommunicationEnabled());
        
    }
    
    @Override
    public List<OptOutLimit> findCurrentOptOutLimit(LiteYukonGroup residentialGroup) {
        String optOutLimitString = rolePropertyDao.getPropertyStringValue(residentialGroup, YukonRoleProperty.RESIDENTIAL_OPT_OUT_LIMITS);
        List<OptOutLimit> optOutLimits = this.parseOptOutLimitString(optOutLimitString);
        
        return optOutLimits;
    }
   
    @Override
    public OpenInterval findOptOutLimitInterval(ReadableInstant intersectingInstant, DateTimeZone dateTimeZone, LiteYukonGroup residentialGroup) {
        Validate.noNullElements(new Object[] {intersectingInstant, dateTimeZone, residentialGroup});
        
        List<OptOutLimit> optOutLimits = findCurrentOptOutLimit(residentialGroup);

        for (OptOutLimit optOutLimit : optOutLimits) {
            int stopDateMonth = intersectingInstant.get(DateTimeFieldType.monthOfYear());
            if  (optOutLimit.isReleventMonth(stopDateMonth)){
                DateTime optOutLimitStartDate = new DateTime(intersectingInstant).withMonthOfYear(optOutLimit.getStartMonth()).withDayOfMonth(1).toDateMidnight().toDateTime(dateTimeZone);
                DateTime optOutLimitStopDate = optOutLimitStartDate.withMonthOfYear(optOutLimit.getStopMonth()).plusMonths(1);

                // If the limit stop date is before the intersecting date, we need to add a year to the stop date.
                if (optOutLimitStopDate.isBefore(intersectingInstant) || optOutLimitStopDate.isEqual(intersectingInstant)) {
                    optOutLimitStopDate = optOutLimitStopDate.plusYears(1);
                }
                
                // If the limit start date is after the intersecting date, we need to subtract a year from the start date.
                if (optOutLimitStartDate.isAfter(intersectingInstant)) {
                    optOutLimitStartDate = optOutLimitStartDate.minusYears(1);
                }
                        
                OpenInterval optOutLimitInterval = OpenInterval.createClosed(optOutLimitStartDate, optOutLimitStopDate);
                return optOutLimitInterval;
            }
        }
        
        return null;
    }

	@Override
	public OptOutCountHolder getCurrentOptOutCount(int inventoryId, int customerAccountId) {

        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByAccountId(customerAccountId);
        TimeZone ecTimeZone = ecService.getDefaultTimeZone(yukonEnergyCompany.getEnergyCompanyId());
        DateTimeZone energyCompanyTimeZone = DateTimeZone.forTimeZone(ecTimeZone);

		// Get the Opt Out limits for the user
		CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
		LiteContact contact = customerDao.getPrimaryContact(customerAccount.getCustomerId());
		int userId = contact.getLoginID();
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
		
		int optOutLimit = OptOutService.NO_OPT_OUT_LIMIT;
		Instant startDate = new Instant(0);

        // If the account has a login and is a member of the Residential Customer Role, load its limits
        if(userId != UserUtils.USER_NONE_ID &&
           rolePropertyDao.checkRole(YukonRole.RESIDENTIAL_CUSTOMER, user)) {
            
            OptOutLimit currentOptOutLimit = getCurrentOptOutLimit(customerAccountId, energyCompanyTimeZone);
            if(currentOptOutLimit != null) {
                optOutLimit = currentOptOutLimit.getLimit();
                startDate = currentOptOutLimit.getOptOutLimitStartDate(energyCompanyTimeZone);
            }
        }
        
		// Get the number of opt outs used from the start of the limit (if there is a limit)
		// till now
		Integer usedOptOuts = optOutEventDao.getNumberOfOptOutsUsed(inventoryId, customerAccountId, startDate, new Instant());
		
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
		
		OptOutEvent scheduledOptOut = optOutEventDao.getScheduledOptOutEvent(inventoryId, customerAccountId);
		
		OptOutCountHolder holder = new OptOutCountHolder();
		holder.setScheduledOptOuts(scheduledOptOut==null ? 0 : 1);
		holder.setInventory(inventoryId);
		holder.setUsedOptOuts(usedOptOuts);
		holder.setRemainingOptOuts(remainingOptOuts);
		
		return holder;
	}
	
	@Override
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId, LiteYukonUser user) {
		
		OptOutCountHolder currentOptOutCount = 
			this.getCurrentOptOutCount(inventoryId, accountId);

		int usedOptOuts = currentOptOutCount.getUsedOptOuts();
		int additionalOptOuts = optOutAdditionalDao.getAdditionalOptOuts(inventoryId, accountId);
		
		int optOutsToAdd = usedOptOuts - additionalOptOuts;
		if(optOutsToAdd > 0) {
		    allowAdditionalOptOuts(accountId, inventoryId, optOutsToAdd, user);
		}
		
        // Log opt out reset
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        accountEventLogService.optOutLimitReset(user,
                                                customerAccount.getAccountNumber(),
                                                lmHardwareBase.getManufacturerSerialNumber());
        // Create and save opt out event for this inventory
        Instant now = new Instant();
        OptOutEvent event = new OptOutEvent();
        event.setEventCounts(OptOutCounts.DONT_COUNT);
        event.setCustomerAccountId(accountId);
        event.setInventoryId(inventoryId);
        event.setScheduledDate(now);
        event.setStartDate(now);
        event.setStopDate(now);
        event.setState(OptOutEventState.RESET_SENT);
        
        optOutEventDao.save(event, OptOutAction.RESET, user);

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
			inventory = starsSearchDao.searchLmHardwareBySerialNumber(serialNumber, energyCompany);
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
		this.resetOptOutLimitForInventory(inventory.getInventoryID(), account.getAccountId(), user);
	}
	
	@Override
    public List<OverrideHistory> getOptOutHistoryForAccount(String accountNumber, Date startTime, Date stopTime,
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
			optOutEventDao.getOptOutHistoryForAccount(account.getAccountId(), startTime, stopTime,
			                                          EnumSet.of(OptOutEventState.START_OPT_OUT_SENT,
			                                                     OptOutEventState.CANCEL_SENT,
			                                                     OptOutEventState.SCHEDULED));
		
		// See if the optional programName parameter was set - if so, only create
		// history objects for that program
		if (programName != null) {
		    LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
			Program program = programService.getByProgramName(programName, energyCompany);

		    Set<Integer> inventory = new HashSet<>();
	        inventory.addAll(enrollmentDao.getOptedOutInventory(program, startTime, stopTime));
	        inventory.addAll(optOutEventDao.getScheduledOptOutInventory(program, startTime, stopTime));
			for (OverrideHistory history : inventoryHistoryList) {
				
				// Only add the history for inventory that was opted out of the given program
				Integer inventoryId = history.getInventoryId();
				if(inventory.contains(inventoryId)) {
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
                  enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId, history.getStartDate(), 
                                                                 history.getStopDate());

				
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
	public List<OverrideHistory> getOptOutHistoryByProgram(String programName, Date startTime, Date stopTime,
	                                                       LiteYukonUser user) throws ProgramNotFoundException {
		Validate.isTrue(startTime.before(stopTime), "Start time must be before stop time.");

        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        Program program = programService.getByProgramName(programName, energyCompany);
		
		List<OverrideHistory> historyList = new ArrayList<>();
		
		// Get the enrolled inventory by program and time period
		Set<Integer> inventory = new HashSet<>();
		inventory.addAll(enrollmentDao.getOptedOutInventory(program, startTime, stopTime));
		inventory.addAll(optOutEventDao.getScheduledOptOutInventory(program, startTime, stopTime));

		// For each inventory, get the opt out event and create the override history object
		for(Integer inventoryId : inventory) {

			Set<Program> programList = new HashSet<>();
			programList.addAll(enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId, startTime, stopTime));

			List<OverrideHistory> inventoryHistoryList
			    = optOutEventDao.getOptOutHistoryForInventory(inventoryId, startTime, stopTime, 
			                                                 EnumSet.of(OptOutEventState.START_OPT_OUT_SENT, 
			                                                            OptOutEventState.CANCEL_SENT, 
			                                                            OptOutEventState.SCHEDULED));

			for(OverrideHistory history : inventoryHistoryList) {
				history.setPrograms(new ArrayList<>(programList));
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
	public void allowAdditionalOptOuts(int accountId, int inventoryId, 
	                                   int additionalOptOuts, LiteYukonUser user) {
	    
        optOutAdditionalDao.addAdditonalOptOuts(inventoryId, accountId, additionalOptOuts);

        CustomerAccount account = customerAccountDao.getById(accountId);
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        accountEventLogService.optOutLimitIncreased(user, account.getAccountNumber(), 
                                                    lmHardwareBase.getManufacturerSerialNumber(),
                                                    additionalOptOuts);
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
			inventory = starsSearchDao.searchLmHardwareBySerialNumber(serialNumber, energyCompany);
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
		
		allowAdditionalOptOuts(account.getAccountId(), inventory.getInventoryID(), 
		                       additionalOptOuts, user);
		
	}
	
	@Override
	@Transactional(propagation = Propagation.NEVER)
	public void cleanUpCancelledOptOut(LiteLmHardwareBase inventory, YukonEnergyCompany yukonEnergyCompany,
	                                   OptOutEvent event, LiteYukonUser user) 
    throws CommandCompletionException {

		cancelLMHardwareControlGroupOptOut(inventory.getInventoryID(), event, user);
		sendCancelCommandAndNotification(inventory, yukonEnergyCompany, user, event);
		
	}
	
	@Override
	public OptOutLimit getCurrentOptOutLimit(int customerAccountId) {
		
	    // Get the energy company time zone to figure out how many opt outs a user has left.
	    YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByAccountId(customerAccountId);
        TimeZone ecTimeZone = ecService.getDefaultTimeZone(yukonEnergyCompany.getEnergyCompanyId());
        DateTimeZone energyCompanyTimeZone = DateTimeZone.forTimeZone(ecTimeZone);

	    return this.getCurrentOptOutLimit(customerAccountId, energyCompanyTimeZone);
	}
	
	private OptOutLimit getCurrentOptOutLimit(int customerAccountId, DateTimeZone energyCompanyTimeZone) {
		
	    CustomerAccount customerAccount = customerAccountDao.getById(customerAccountId);
        LiteContact contact = customerDao.getPrimaryContact(customerAccount.getCustomerId());
        
        // Get the consumer user to get their opt out limit.
        int userId = contact.getLoginID();
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
	    
        // If the account has a login and is a member of the Residential Customer Role, load its limits
        if(user.getUserID() != UserUtils.USER_NONE_ID &&
           rolePropertyDao.checkRole(YukonRole.RESIDENTIAL_CUSTOMER, user)) {
		    
		    DateTime dateTime = new DateTime(energyCompanyTimeZone);
			int currentMonth = dateTime.getMonthOfYear();
			
			String optOutLimitString = 
				rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_LIMITS, user);
			
			List<OptOutLimit> optOutLimits = this.parseOptOutLimitString(optOutLimitString);
			for (OptOutLimit limit : optOutLimits) {
	            if (limit.isReleventMonth(currentMonth)) {
	                return limit;
	            }
	        }
		}
		
		return null;
	}
	
	/**
	 * Helper method to send the opt out cancel command and cancel notification message
	 * @param inventory - Inventory to cancel opt out on
	 * @param yukonEnergyCompany - Inventory's energy company
	 * @param user - User requesting the cancel
	 * @param event - Event being canceled
	 * @throws CommandCompletionException
	 */
	private void sendCancelCommandAndNotification(LiteLmHardwareBase inventory, YukonEnergyCompany yukonEnergyCompany, LiteYukonUser user, OptOutEvent event) 
	throws CommandCompletionException {
		
	    CustomerAccount customerAccount = customerAccountDao.getById(event.getCustomerAccountId());
		
		// Send the command to cancel opt out to the field
		this.sendCancelRequest(inventory, user);
		
		logCancelCommandAndSendNotification(inventory, yukonEnergyCompany, user, customerAccount, event);
	}

    private void logCancelCommandAndSendNotification(LiteLmHardwareBase inventory,
            YukonEnergyCompany yukonEnergyCompany, LiteYukonUser user,
            CustomerAccount customerAccount, OptOutEvent ooe) {
        accountEventLogService.optOutCanceled(user, customerAccount.getAccountNumber(), inventory.getDeviceLabel());
        
        ActivityLogger.logEvent(
				user.getUserID(), 
				customerAccount.getAccountId(), 
				yukonEnergyCompany.getEnergyCompanyId(), 
				customerAccount.getCustomerId(),
				ActivityLogActions.PROGRAM_REENABLE_ACTION, 
				"Serial #:" + inventory.getManufacturerSerialNumber());
        
        // Send re-enable (cancel opt out) notification
        try {
            OptOutRequest request = new OptOutRequest();
            request.setStartDate(ooe.getStartDate());
            request.setInventoryIdList(Collections.singletonList(ooe.getInventoryId()));
            request.setDurationInHours(ooe.getDurationInHours());
            request.setQuestions(new ArrayList<ScheduledOptOutQuestion>());

            EnergyCompany energyCompany = ecDao.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
            optOutNotificationService.sendReenableNotification(customerAccount, energyCompany, request, user);

        } catch (MessagingException e) {
            // Not much we can do - tried to send notification
            logger.error(e);
        }
    }

	/**
	 * Helper method to update the LMHardwareControlGroup opt out row when an opt out is complete
	 * or canceled
	 * @param inventoryId - Inventory to cancel opt out for
	 * @param event - Event being canceled
	 * @param user - User requesting the cancel
	 */
	private void cancelLMHardwareControlGroupOptOut(int inventoryId, OptOutEvent event, LiteYukonUser user) {
		
		// Update the LMHardwareControlGroup table
	    lmHardwareControlInformationService.stopOptOut(inventoryId, user, event.getStopDate());
	}
	
	/**
	 * Helper method to parse a string into a list of opt out limits
	 * @param optOutLimitString - String containing opt out limit data
	 * @return List of limits or empty list if no limits set
	 */
	private List<OptOutLimit> parseOptOutLimitString(String optOutLimitString) {
		
		List<OptOutLimit> optOutLimits = new ArrayList<>();
		
		if(StringUtils.isBlank(optOutLimitString)) {
			return optOutLimits;
		}
		
		TypeReference<List<Map<String, Integer>>> listOfMapType
		    = new TypeReference<List<Map<String, Integer>>>() {/*empty*/};
		    
		List<Map<String, Integer>> limits;
        try {
            limits = jsonObjectMapper.readValue(optOutLimitString, listOfMapType);
    		for(Map<String, Integer> limit : limits) {
    		    if (!limit.containsKey("start")) {
    		        throw new IOException("Missing 'start' value. Invalid JSON for Opt Out Limit");
    		    }
                if (!limit.containsKey("stop")) {
                    throw new IOException("Missing 'stop' value. Invalid JSON for Opt Out Limit");
                }
                if (!limit.containsKey("limit")) {
                    throw new IOException("Missing 'limit' value. Invalid JSON for Opt Out Limit");
                }
    			OptOutLimit optOutLimit = new OptOutLimit();
    			optOutLimit.setStartMonth(limit.get("start"));
    			optOutLimit.setStopMonth(limit.get("stop"));
    			optOutLimit.setLimit(limit.get("limit"));
    			optOutLimits.add(optOutLimit);
    		}
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse json (" + optOutLimitString + ")", e);
        }
		
		return optOutLimits;
	}
	
    
	/**
	 * Helper method to send the opt out command out to the device
	 * 
	 * @throws PaoAuthorizationException - If user is not authorized to send 
	 * 		opt out to the device
	 * @throws CommandCompletionException - If request is interrupted or times out
	 */
	private void sendOptOutRequest(LiteLmHardwareBase inventory, int durationInHours, LiteYukonUser user) 
		throws CommandCompletionException {
		
	    LmHardwareCommand lmhc = new LmHardwareCommand();
        lmhc.setDevice(inventory);
        lmhc.setType(LmHardwareCommandType.TEMP_OUT_OF_SERVICE);
        lmhc.setUser(user);
        lmhc.getParams().put(LmHardwareCommandParam.DURATION, Duration.standardHours(durationInHours));
		
		lmHardwareCommandService.sendOptOutCommand(lmhc);
	}
	
	/**
	 * Helper method to send the cancel opt out command out to the device
	 * 
	 * @throws PaoAuthorizationException - If user is not authorized to send 
	 * 		cancel opt out to the device
	 * @throws CommandCompletionException - If request is interrupted or times out
	 */
	private void sendCancelRequest(LiteLmHardwareBase inventory,  LiteYukonUser user) 
	throws CommandCompletionException {
	    
	    LmHardwareCommand lmhc = new LmHardwareCommand();

	    lmhc.setDevice(inventory);
	    lmhc.setType(LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE);
	    lmhc.setUser(user);
	    
        lmHardwareCommandService.sendOptOutCommand(lmhc);
	}
	
    @Override
    public String checkOptOutStartDate(int accountId, LocalDate startDate, 
                                       YukonUserContext userContext, boolean isOperator) {

        // Opt Out Start Date
        final LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        final LocalDate yearFromToday = today.plusYears(1);
        
        if (startDate == null) {
            return "invalidStartDate";
        }
        if (startDate.isBefore(today)) {
            return "startDateTooEarly";
        }
        if (startDate.isAfter(yearFromToday)) {
            return "startDateTooLate";
        }

        // Check if all opt out devices are already opted out for today.
        if (startDate.equals(today)) {
            
            boolean hasADeviceAvailableForOptOut = false;
            
            List<DisplayableInventory> displayableInventories =
                displayableInventoryDao.getDisplayableInventory(accountId);
            
            for (DisplayableInventory displayableInventory : displayableInventories) {
                if (!displayableInventory.isCurrentlyOptedOut()) {
                    hasADeviceAvailableForOptOut = true;
                    break;
                }
            }

            if (!hasADeviceAvailableForOptOut) {
                return "allDevicesCurrentlyOptedOut";
            }
        }

        boolean optOutTodayOnly;
        if (isOperator) {
            optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        } else {
            optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        }
        if(optOutTodayOnly) {
            if (startDate.isAfter(today)) {
                return "startDateToday";
            }
        }
        
        return null;
    }
    
    @Override
    public Duration calculateDuration(LocalDate startDate, int durationInDays, YukonUserContext userContext){
        
        Duration duration;
        LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        boolean isSameDay = today.isEqual(startDate);
        if (isSameDay) {
            //duration to midnight
            duration =
                new Duration(startDate.toDateTimeAtCurrentTime(), startDate.plusDays(1).toDateTimeAtStartOfDay());
            if (durationInDays > 1) {
                DateTime startDateTime = startDate.toDateTimeAtStartOfDay(userContext.getJodaTimeZone());
                DateTime endDate = startDateTime.plusDays(durationInDays - 1);
                duration = duration.plus(new Duration(startDateTime, endDate));
            }
        } else {
            DateTime startDateTime = startDate.toDateTimeAtStartOfDay(userContext.getJodaTimeZone());
            DateTime endDate = startDateTime.plusDays(durationInDays);
            duration = new Duration(startDateTime, endDate);
        }
        return duration;
     }
}