package com.cannontech.services.infrastructure.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.cannontech.services.infrastructure.service.InfrastructureWarningsService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

public class InfrastructureWarningsServiceImpl implements InfrastructureWarningsService {
    private static final Logger log = YukonLogManager.getLogger(InfrastructureWarningsServiceImpl.class);
    private static final int initialDelayMinutes = 1;
    private static final int minimumMinutesBetweenRuns = 15;
    private static final int runFrequencyMinutes = 60;
    private static AtomicBoolean isRunning = new AtomicBoolean();
    private List<PaoType> warnableTypes = new ImmutableList.Builder<PaoType>()
            .addAll(PaoType.getRfGatewayTypes())
            .addAll(PaoType.getRfRelayTypes())
            .addAll(PaoType.getCcuTypes())
            .addAll(PaoType.getRepeaterTypes())
            .build();
    
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private List<InfrastructureWarningEvaluator> evaluators;
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    
    /**
     * The thread where the calculation is done.
     */
    private final Runnable calculationThread = () -> calculateWarnings();
    
    /**
     * Schedule the calculation thread to run periodically.
     */
    @PostConstruct
    public void init() {
        executor.scheduleAtFixedRate(calculationThread, initialDelayMinutes, runFrequencyMinutes, TimeUnit.MINUTES);
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
            if (!minimumTimeBetweenRunsExceeded()) {
                log.debug("Prevented start of calculation thread - insufficient time between runs.");
                return;
            }
            
            // if the calculation is running, exit, otherwise set isRunning to "true" and continue
            if (!isRunning.compareAndSet(false, true)) {
                log.debug("Prevented start of calculation thread - task is already running.");
                return;
            }
            
            log.info("Calculating infrastructure warnings");
            
            List<PaoType> currentTypes = getCurrentWarnableTypes();
            
            List<InfrastructureWarning> warnings = evaluators.stream()
                                                             .filter(evaluator -> evaluator.supportsAny(currentTypes))
                                                             .map(evaluator -> evaluator.evaluate())
                                                             .flatMap(List::stream)
                                                             .collect(Collectors.toList());
            
            infrastructureWarningsDao.insert(warnings);
            
            persistedSystemValueDao.setValue(PersistedSystemValueKey.INFRASTRUCTURE_WARNINGS_LAST_RUN_TIME, Instant.now());
            isRunning.set(false);
            log.info("Infrastructure warnings calculation complete");
        } catch (Exception e) {
            log.error("Unexpected exception: ", e);
            isRunning.set(false);
        }
    }
    
    private List<PaoType> getCurrentWarnableTypes() {
        return serverDatabaseCache.getAllPaoTypes()
                                  .stream()
                                  .filter(type -> warnableTypes.contains(type))
                                  .collect(Collectors.toList());
    }
    
    private boolean minimumTimeBetweenRunsExceeded() {
        Instant lastRun = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.INFRASTRUCTURE_WARNINGS_LAST_RUN_TIME);
        Duration minTimeBetweenRuns = Duration.standardMinutes(minimumMinutesBetweenRuns);
        
        if (lastRun.plus(minTimeBetweenRuns).isBeforeNow()) {
            return true;
        }
        return false;
    }
}
