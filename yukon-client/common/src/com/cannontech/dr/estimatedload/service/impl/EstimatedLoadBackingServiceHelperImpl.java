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
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.estimatedload.ApplianceCategoryInfoNotFoundException;
import com.cannontech.dr.estimatedload.ApplianceCategoryNotFoundException;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.GearNotFoundException;
import com.cannontech.dr.estimatedload.InputOutOfRangeException;
import com.cannontech.dr.estimatedload.InputOutOfRangeException.Type;
import com.cannontech.dr.estimatedload.InputValueNotFoundException;
import com.cannontech.dr.estimatedload.LmDataNotFoundExceptionException;
import com.cannontech.dr.estimatedload.LmServerNotConnectedException;
import com.cannontech.dr.estimatedload.NoAppCatFormulaException;
import com.cannontech.dr.estimatedload.NoGearFormulaException;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EstimatedLoadBackingServiceHelperImpl implements EstimatedLoadBackingServiceHelper {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadBackingServiceHelperImpl.class);

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private LoadControlClientConnection clientConnection;
    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private EstimatedLoadDao estimatedLoadDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private @Qualifier("estimatedLoad") Executor executor;

    private static final int CACHE_SECONDS_TO_LIVE = 120;
    private final Cache<EstimatedLoadResultKey, EstimatedLoadResult> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(CACHE_SECONDS_TO_LIVE, TimeUnit.SECONDS).build();

    @Override
    public EstimatedLoadResult findProgramValue(final int programId) {
        int currentGearId;
        try {
            currentGearId = findCurrentGearId(programId);
        } catch (EstimatedLoadException e) {
            return e;
        }
        return getProgramValue(programId, currentGearId);
    }

    /**
     * This method takes a program id and gear id and attempts to calculate the estimated load amounts for that
     * program/gear combination. It first checks to see if the cache holds a recently computed value. If it does,
     * that value is returned. If not, a new Runnable is created and executed which will insert the result into
     * the cache once calculation is complete.  The cache key is based on both program id and gear id, so a
     * single program may have multiple cache entries for multiple gears which is useful when considering
     * scenarios that have multiple start gears for the same program.
     */
    private EstimatedLoadResult getProgramValue(final int programId, final Integer gearId) {

        final EstimatedLoadResultKey resultKey = new EstimatedLoadResultKey(programId, gearId);
        EstimatedLoadResult amount = cache.getIfPresent(resultKey);
        
        if (null == amount) {
            if (log.isDebugEnabled()) {
                log.debug("Recalculating value for program id: " + programId + "\t gear id: " + gearId);
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        cache.get(resultKey, new Callable<EstimatedLoadResult>() {
                            @Override
                            public EstimatedLoadResult call() {
                                try {
                                    LMProgramBase programBase = getLmProgramBase(programId);
                                    return estimatedLoadService.calculateProgramLoadReductionAmounts(
                                            programBase.getPaoIdentifier(), gearId);
                                } catch (EstimatedLoadException e) {
                                    return e;
                                }
                            }
                        });
                    } catch (ExecutionException e) {
                        log.error("Unknown exception in estimated load calculation.", e);
                    }
                }
            });
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Cache hit for program id: " + programId + "\t gear id: " + gearId);
            }
        }
        
        return amount;
    }

    public EstimatedLoadSummary getControlAreaValue(PaoIdentifier paoId) {
        Set<Integer> programIdsForControlArea = controlAreaDao.getProgramIdsForControlArea(paoId.getPaoId());
        Set<EstimatedLoadResult> programResults = new HashSet<>();
        
        for (Integer programId : programIdsForControlArea) {
            programResults.add(findProgramValue(programId));
        }
        return sumEstimatedLoadAmounts(paoId, programResults);
    }

    public EstimatedLoadSummary getScenarioValue(PaoIdentifier paoId) {
        Map<Integer, ScenarioProgram> programsForScenario = scenarioDao.findScenarioProgramsForScenario(
                paoId.getPaoId());
        Set<EstimatedLoadResult> programAmounts = new HashSet<>();
        
        for (Integer programId : programsForScenario.keySet()) {
            int startGearId = programsForScenario.get(programId).getStartGear();
            programAmounts.add(getProgramValue(programId, startGearId)); 
        }
        return sumEstimatedLoadAmounts(paoId, programAmounts);
    }
    
    /** This method takes a Set of EstimatedLoadReductionAmount objects and sums up each of the four estimated load
     * values into one object.  Used by retrieveScenarioValue() and retrieveControlAreaValue() methods to add together
     * the estimated amounts of all of their component programs.
     * 
     * @param paoId The PaoIdentifier of the control area or scenario whose programs are summed.
     * @param programResults The estimated load amounts for each program in the control area/scenario.
     * @param errors The list of errors that occurred when evaluating each program.
     * @return The sum of all EstimatedLoadReductionAmount in the set of programAmounts 
     * as a single EstimatedLoadReductionAmount.
     * @throws EstimatedLoadException 
     */
    private EstimatedLoadSummary sumEstimatedLoadAmounts(PaoIdentifier paoId, Set<EstimatedLoadResult> programResults) {
        int totalPrograms = programResults.size();
        int contributing = 0;
        int calculating = 0;
        int error = 0;
        
        double sumConnectedLoad = 0.0;
        double sumDiversifiedLoad = 0.0;
        double sumMaxKwSavings = 0.0;
        double sumNowKwSavings = 0.0;
        for (EstimatedLoadResult result : programResults) {
            if (result == null) {
                calculating++;
            } else if (result instanceof EstimatedLoadAmount) {
                EstimatedLoadAmount amount = (EstimatedLoadAmount) result;
                sumConnectedLoad += amount.getConnectedLoad();
                sumDiversifiedLoad += amount.getDiversifiedLoad();
                sumMaxKwSavings += amount.getMaxKwSavings();
                sumNowKwSavings += amount.getNowKwSavings();
                contributing++;
            } else if (result instanceof EstimatedLoadException) {
                error++;
            }
        }
        
        return new EstimatedLoadSummary(totalPrograms, contributing, calculating, error,
                new EstimatedLoadAmount(sumConnectedLoad, sumDiversifiedLoad, sumMaxKwSavings, sumNowKwSavings));
    }

    @Override
    public int findCurrentGearId(int programId) throws EstimatedLoadException {
        IGearProgram program = (IGearProgram) getLmProgramBase(programId);
        int gearNumber = 1;
        if (program.getCurrentGear() != null) {
            gearNumber = program.getCurrentGearNumber();
        }
        return estimatedLoadDao.getCurrentGearIdForProgram(programId, gearNumber);
    }

    @Override
    public LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadException {
        LMProgramBase programBase = null;
        try {
            programBase = clientConnection.getProgramSafe(programId);
        } catch (ConnectionException e){
            throw new LmServerNotConnectedException();
        } catch (NotFoundException e) {
            throw new LmDataNotFoundExceptionException(programId);
        }
        if (programBase == null) {
            throw new LmDataNotFoundExceptionException(programId);
        }
        return programBase;
    }

    @Override
    public MessageSourceResolvable resolveException(EstimatedLoadException e, YukonUserContext userContext) {
        if (e instanceof ApplianceCategoryNotFoundException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.applianceCategoryNotFound");
        } else if (e instanceof ApplianceCategoryInfoNotFoundException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.applianceCategoryInfoNotFound");
        } else if (e instanceof GearNotFoundException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.gearNumberNotFound");
        } else if (e instanceof InputOutOfRangeException) {
            InputOutOfRangeException inputException = ((InputOutOfRangeException) e);
            Formula formula = inputException.getFormula();
            String formulaComponentName = null;
            if (inputException.getType() == Type.FUNCTION) {
                formulaComponentName = formula.getFunctionById(inputException.getId()).getName();
            } else if (inputException.getType() == Type.LOOKUP) {
                formulaComponentName = formula.getTableById(inputException.getId()).getName();
            } else if (inputException.getType() == Type.TIME_LOOKUP) {
                formulaComponentName = formula.getTimeTableById(inputException.getId()).getName();
            } else {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                formulaComponentName = accessor
                        .getMessage(new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.unknown"));
            }
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.inputOutOfRange",
                    inputException.getFormula().getName(), formulaComponentName);
        } else if (e instanceof InputValueNotFoundException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.inputValueNotFound",
                    ((InputValueNotFoundException) e).getFormulaName());
        } else if (e instanceof LmDataNotFoundExceptionException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.loadManagementDataNotFound");
        } else if (e instanceof LmServerNotConnectedException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.loadManagementServerNotConnected");
        } else if (e instanceof NoAppCatFormulaException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.acFormulaAssignmentNotFound");
        } else if (e instanceof NoGearFormulaException) {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.gearFormulaAssignmentNotFound");
        } else {
            return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.unknownError");
        }
    }

}
