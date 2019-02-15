package com.cannontech.services.infrastructure.service.impl;

import static org.joda.time.Instant.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.events.loggers.InfrastructureEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningsRefreshRequest;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.cannontech.services.infrastructure.service.InfrastructureWarningsService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

public class InfrastructureWarningsServiceImpl implements InfrastructureWarningsService {
    private static final Logger log = YukonLogManager.getLogger(InfrastructureWarningsServiceImpl.class);
    private static int minimumTimeBetweenRuns = 5;
    private static int runFrequencyMinutes = 15;
    private static final List<PaoType> warnableTypes = new ImmutableList.Builder<PaoType>()
            .addAll(PaoType.getRfGatewayTypes())
            .addAll(PaoType.getRfRelayTypes())
            .addAll(PaoType.getCcuTypes())
            .addAll(PaoType.getRepeaterTypes())
            .build();
    private static AtomicBoolean isRunning = new AtomicBoolean();
    private MessageSourceAccessor systemMessageSourceAccessor;
    
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private List<InfrastructureWarningEvaluator> evaluators;
    @Autowired private InfrastructureEventLogService infrastructureEventLogService;
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private ConfigurationSource configurationSource;
    private JmsTemplate jmsTemplate;
    
    /**
     * The thread where the calculation is done.
     */
    private final Runnable calculationThread = this::calculateWarnings;
    
    /**
     * Schedule the calculation thread to run periodically.
     */
    @PostConstruct
    public void init() {
        systemMessageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        minimumTimeBetweenRuns = configurationSource.getInteger(
            MasterConfigInteger.INFRASTRUCTURE_WARNING_MINIMUM_TIME_BETWEEN_RUNS, minimumTimeBetweenRuns);
        runFrequencyMinutes = configurationSource.getInteger(
            MasterConfigInteger.INFRASTRUCTURE_WARNING_RUN_FREQUENCY_MINUTES, runFrequencyMinutes);
        log.info("Calculation for infrastructure warnings will start " + minimumTimeBetweenRuns
            + " minute(s) after the server is started and run every " + runFrequencyMinutes
            + " minute(s) with force refresh possible every " + minimumTimeBetweenRuns + " minute(s).");
        executor.scheduleAtFixedRate(calculationThread, minimumTimeBetweenRuns, runFrequencyMinutes, TimeUnit.MINUTES);
    }
    
    /**
     * Limits how often the calculation can run.
     * Sets the last run time in persisted system values.
     * Delegates all warning calculations to the InfrastructureWarningEvaluators, but only invokes an evaluator if
     * there are devices in the system that it knows how to analyze.
     */
    @Override
    public void calculateWarnings() {
        try {

            // if the calculation is running, exit, otherwise set isRunning to "true" and continue
            if (!isRunning.compareAndSet(false, true)) {
                log.debug("Prevented start of calculation thread - task is already running.");
                return;
            }
            
            Instant lastRun =
                    persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.INFRASTRUCTURE_WARNINGS_LAST_RUN_TIME);
            if (lastRun == null || now().isAfter(lastRun.plus(minimumTimeBetweenRuns))) { 
                log.info("Calculating infrastructure warnings");
                lastRun = Instant.now();
                persistedSystemValueDao.setValue(PersistedSystemValueKey.INFRASTRUCTURE_WARNINGS_LAST_RUN_TIME, lastRun);
                
                List<InfrastructureWarning> oldWarnings = infrastructureWarningsDao.getWarnings();
                
                List<PaoType> currentTypes = getCurrentWarnableTypes();
                
                List<InfrastructureWarning> warnings = evaluators.stream()
                                                                 .filter(evaluator -> evaluator.supportsAny(currentTypes))
                                                                 .map(evaluator -> evaluator.evaluate())
                                                                 .flatMap(List::stream)
                                                                 .collect(Collectors.toList());
                
                // Add event log entries only for warnings that are new in this calculation. Warning arguments checked for equality.
                List<InfrastructureWarning> eventLogWarnings = getAndLogEventLogWarnings(oldWarnings, warnings);
                
                // Used for smart notifications. Warning arguments NOT checked for equality.
                List<InfrastructureWarning> smartNotificationWarnings = getSmartNotificationWarnings(oldWarnings, eventLogWarnings);
                
                // Get the old warnings and non-repeated warnings that we want to put in the Infrastructure
                // Warnings table, then insert warnings into DB (overwriting previous warnings in the table).
                // This process carries over the start time for warnings that have been repeatedly calculated.
                List<InfrastructureWarning> infrastructureWarnings = getInfrastructureWarnings(oldWarnings, warnings);
                infrastructureWarningsDao.insert(infrastructureWarnings);

                log.info("Infrastructure warnings calculation complete");
                
                sendSmartNotifications(smartNotificationWarnings);
                sendCacheRefreshRequest(lastRun);
                
                isRunning.set(false);
            }
        } catch (Exception e) {
            log.error("Unexpected exception: ", e);
            isRunning.set(false);
        }   
    }
    
    private List<InfrastructureWarning> getInfrastructureWarnings(List<InfrastructureWarning> oldWarnings, List<InfrastructureWarning> currentWarnings) {
        //if warning is an existing warning, maintain timestamp from existing
        List<InfrastructureWarning> warnings = new ArrayList<InfrastructureWarning>();
        for (InfrastructureWarning currentWarning : currentWarnings) {
            Optional<InfrastructureWarning> optional = oldWarnings.stream()
                                                                  .filter(old -> old.equals(currentWarning))
                                                                  .findFirst();
            if (optional.isPresent()) {
                //update warning but maintain date/time
                InfrastructureWarning existingWarning = optional.get();
                warnings.add(new InfrastructureWarning(currentWarning.getPaoIdentifier(), currentWarning.getWarningType(), 
                                                       currentWarning.getSeverity(), existingWarning.getTimestamp(), currentWarning.getArguments()));
            } else {
                warnings.add(currentWarning);
            }
        }
        return warnings;
    }

    /**
     * Notify WS that warnings were recalculated and warnings cache can be updated
     */
    private void sendCacheRefreshRequest(Instant lastRun) {
        log.info("Notifying WS that Smart Infrastructure Warnings were recalculated.");
        InfrastructureWarningsRefreshRequest refreshRequest = new InfrastructureWarningsRefreshRequest();
        //lastRun - time the warning calculation started
        refreshRequest.setLastRunTime(lastRun);
        //nextRun - time the warning calculation ended + minimumTimeBetweenRuns
        refreshRequest.setNextRunTime(new DateTime().plusMinutes(minimumTimeBetweenRuns).toInstant());
        jmsTemplate.convertAndSend(JmsApiDirectory.INFRASTRUCTURE_WARNINGS_CACHE_REFRESH.getQueue().getName(), refreshRequest);
    }
    
    /**
     * Send smart notification events for warnings. Events will only be sent for warnings that weren't in the old list,
     * but are in the new list.
     */
    private void sendSmartNotifications(List<InfrastructureWarning> smartNotificationWarnings) {
        Instant now = Instant.now();
        List<SmartNotificationEvent> events = getNotificationEventsForNewWarnings(smartNotificationWarnings, now);
        smartNotificationEventCreationService.send(SmartNotificationEventType.INFRASTRUCTURE_WARNING, events);
    }
    
    private List<SmartNotificationEvent> getNotificationEventsForNewWarnings(List<InfrastructureWarning> smartNotificationWarnings,
                                                                             Instant now) {
        
        return smartNotificationWarnings.stream()
                                        .map(warning -> InfrastructureWarningsEventAssembler.assemble(now, warning))
                                        .collect(Collectors.toList());
    }
    
    private List<PaoType> getCurrentWarnableTypes() {
        return serverDatabaseCache.getAllPaoTypes()
                                  .stream()
                                  .filter(warnableTypes::contains)
                                  .collect(Collectors.toList());
    }
    
    /**
     * Get the list of warnings that are not repeats from the previous calculation (without comparing arguments)
     */
    private List<InfrastructureWarning> getSmartNotificationWarnings(List<InfrastructureWarning> oldWarnings, List<InfrastructureWarning> newWarnings) {
        return newWarnings.stream()
                          .filter(warning -> !oldWarnings.contains(warning))
                          .collect(Collectors.toList());
    }
    
    /**
     * Get the list of warnings that are not repeats from the previous calculation (comparing arguments),
     * log them in the event log, and return the list of non-repeated warnings.
     */
    private List<InfrastructureWarning> getAndLogEventLogWarnings(List<InfrastructureWarning> oldWarnings, List<InfrastructureWarning> newWarnings) {
        return newWarnings.stream()
                          .filter(warning -> oldWarnings.stream()
                                                        .noneMatch(oldWarning -> oldWarning.equalsWithArgs(warning)))
                          .peek(warning -> {
                              String warningMessage = systemMessageSourceAccessor.getMessage(warning);
                              infrastructureEventLogService.warningGenerated(serverDatabaseCache.getAllPaosMap().get(warning.getPaoIdentifier().getPaoId()).getPaoName(),
                                                                             warning.getWarningType().toString(),
                                                                             warning.getSeverity().toString(),
                                                                             warningMessage);
                          })
                          .collect(Collectors.toList());
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setPubSubDomain(false);
    }
}
