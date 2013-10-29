package com.cannontech.dr.estimatedload.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException.Type;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.message.util.ConnectionException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EstimatedLoadBackingServiceHelperImpl implements EstimatedLoadBackingServiceHelper {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadBackingServiceHelperImpl.class);
    
    @Autowired private LoadControlClientConnection clientConnection;
    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private EstimatedLoadDao estimatedLoadDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired @Qualifier("longRunningExecutor") private Executor executor;

    private static final int CACHE_SECONDS_TO_LIVE = 180;
    private final Cache<Integer, EstimatedLoadReductionAmount> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(CACHE_SECONDS_TO_LIVE, TimeUnit.SECONDS).build();

    public Cache<Integer, EstimatedLoadReductionAmount> getCache() {
        return cache;
    }

    @Override
    public EstimatedLoadReductionAmount getProgramValue(final PaoIdentifier program)
            throws EstimatedLoadCalculationException {
        EstimatedLoadReductionAmount amount = cache.getIfPresent(program.getPaoId());
        
        if (null == amount) {
            if (log.isDebugEnabled()) {
                log.debug("Recalculating value for program id: " + program.getPaoId());
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        cache.get(program.getPaoId(), new Callable<EstimatedLoadReductionAmount>() {
                            @Override
                            public EstimatedLoadReductionAmount call() throws Exception {
                                return estimatedLoadService.calculateProgramLoadReductionAmounts(program);
                            }
                        });
                    } catch (ExecutionException e) {
                        if (e.getCause() instanceof EstimatedLoadCalculationException) {
                            EstimatedLoadReductionAmount error = createErrorObject(
                                    (EstimatedLoadCalculationException) e.getCause());
                            cache.put(program.getPaoId(), error);
                        } else {
                           log.error("Unknown exception in estimated load calculation: " + e);
                        }
                    }
                }
            });
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Cache hit for program id: " + program.getPaoId());
            }
        }
        
        return amount;
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM control area.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * @throws EstimatedLoadCalculationException 
     */
    public EstimatedLoadReductionAmount getControlAreaValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        Set<Integer> programIdsForControlArea = controlAreaDao.getProgramIdsForControlArea(paoId.getPaoId());
        Set<EstimatedLoadReductionAmount> programAmounts = new HashSet<>();
        Set<EstimatedLoadReductionAmount> errors = new HashSet<>();
        
        for (Integer programId : programIdsForControlArea) {
            LMProgramBase programBase = getLmProgramBase(programId);
            int gearId = findCurrentGearId(programBase.getPaoIdentifier());
            programAmounts.add(estimatedLoadService.calculateProgramLoadReductionAmounts(
                    programBase.getPaoIdentifier(), gearId));
        }
        return sumEstimatedLoadAmounts(paoId, programAmounts, errors);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM scenario.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * @throws EstimatedLoadCalculationException 
     */
    public EstimatedLoadReductionAmount getScenarioValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        Map<Integer, ScenarioProgram> programsForScenario = scenarioDao.findScenarioProgramsForScenario(
                paoId.getPaoId());
        Set<EstimatedLoadReductionAmount> programAmounts = new HashSet<>();
        Set<EstimatedLoadReductionAmount> errors = new HashSet<>();
        
        for (Integer programId : programsForScenario.keySet()) {
            LMProgramBase programBase = getLmProgramBase(programId);
            int gearId = estimatedLoadDao.getCurrentGearIdForProgram(programId,
                    programsForScenario.get(programId).getStartGear());
            programAmounts.add(estimatedLoadService.calculateProgramLoadReductionAmounts(
                    programBase.getPaoIdentifier(), gearId)); 
        }
        return sumEstimatedLoadAmounts(paoId, programAmounts, errors);
    }
    
    /** This method takes a Set of EstimatedLoadReductionAmount objects and sums up each of the four estimated load
     * values into one object.  Used by retrieveScenarioValue() and retrieveControlAreaValue() methods to add together
     * the estimated amounts of all of their component programs.
     * 
     * @param paoId The PaoIdentifier of the control area or scenario whose programs are summed.
     * @param programAmounts The estimated load amounts for each program in the control area/scenario.
     * @param errors The list of errors that occurred when evaluating each program.
     * @return The sum of all EstimatedLoadReductionAmount in the set of programAmounts 
     * as a single EstimatedLoadReductionAmount.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount sumEstimatedLoadAmounts(PaoIdentifier paoId,
            Set<EstimatedLoadReductionAmount> programAmounts, Set<EstimatedLoadReductionAmount> errors)
            throws EstimatedLoadCalculationException {
        int contributingPrograms = 0;
        double sumConnectedLoad = 0.0;
        double sumDiversifiedLoad = 0.0;
        double sumMaxKwSavings = 0.0;
        double sumNowKwSavings = 0.0;
        for (EstimatedLoadReductionAmount elra : programAmounts) {
            if (!elra.isError()) {
                sumConnectedLoad += elra.getConnectedLoad();
                sumDiversifiedLoad += elra.getDiversifiedLoad();
                sumMaxKwSavings += elra.getMaxKwSavings();
                sumNowKwSavings += elra.getNowKwSavings();
                contributingPrograms++;
            } else {
                errors.add(elra);
            }
        }
        if (contributingPrograms == 0) {
                throw new EstimatedLoadCalculationException(Type.NO_CONTRIBUTING_PROGRAMS);
        }
        if (errors.size() > 0) {
            if (paoId.getPaoType() == PaoType.LM_SCENARIO) {
                return new EstimatedLoadReductionAmount(sumConnectedLoad, sumDiversifiedLoad, sumMaxKwSavings, sumNowKwSavings,
                        true, new EstimatedLoadCalculationException(Type.SCENARIO_HAS_ERRORS));
            } else if (paoId.getPaoType() == PaoType.LM_CONTROL_AREA) {
                return new EstimatedLoadReductionAmount(sumConnectedLoad, sumDiversifiedLoad, sumMaxKwSavings, sumNowKwSavings,
                        true, new EstimatedLoadCalculationException(Type.CONTROL_AREA_HAS_ERRORS));
            }
        }
        return new EstimatedLoadReductionAmount(sumConnectedLoad, sumDiversifiedLoad, sumMaxKwSavings, sumNowKwSavings,
                false, null);
    }

    public int findCurrentGearId(PaoIdentifier program) throws EstimatedLoadCalculationException {
        LMProgramBase programBase = getLmProgramBase(program.getPaoId());
        int gearNumber;
        if (((IGearProgram) programBase).getCurrentGear() == null) {
            gearNumber = 1;
        } else {
            gearNumber = ((IGearProgram) programBase).getCurrentGearNumber();
        }
        return estimatedLoadDao.getCurrentGearIdForProgram(program.getPaoId(), gearNumber);
    }

    public LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadCalculationException {
        LMProgramBase programBase = null;
        try {
            programBase = clientConnection.getProgramSafe(programId);
        } catch (ConnectionException e){
            throw new EstimatedLoadCalculationException(Type.LOAD_MANAGEMENT_SERVER_NOT_CONNECTED);
        } catch (NotFoundException e) {
            throw new EstimatedLoadCalculationException(Type.LOAD_MANAGEMENT_DATA_NOT_FOUND);
        }
        if (programBase == null) {
            throw new EstimatedLoadCalculationException(Type.LOAD_MANAGEMENT_DATA_NOT_FOUND);
        }
        return programBase;
    }

    private EstimatedLoadReductionAmount createErrorObject(EstimatedLoadCalculationException e) {
        return new EstimatedLoadReductionAmount(0.0, 0.0, 0.0, 0.0, true, e);
    }
}
