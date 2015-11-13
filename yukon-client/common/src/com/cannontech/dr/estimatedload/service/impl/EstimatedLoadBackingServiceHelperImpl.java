package com.cannontech.dr.estimatedload.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.keyvalue.MultiKey;
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
import com.cannontech.dr.estimatedload.LmDataNotFoundException;
import com.cannontech.dr.estimatedload.LmServerNotConnectedException;
import com.cannontech.dr.estimatedload.NoAppCatFormulaException;
import com.cannontech.dr.estimatedload.NoGearFormulaException;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class EstimatedLoadBackingServiceHelperImpl implements EstimatedLoadBackingServiceHelper {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadBackingServiceHelperImpl.class);

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private LoadControlClientConnection clientConnection;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private EstimatedLoadDao estimatedLoadDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private @Qualifier("estimatedLoad") Executor executor;
    @Autowired private ProgramDao programDao;

    private static final int CACHE_SECONDS_TO_LIVE = 120;
    private final Cache<MultiKey, EstimatedLoadResult> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(CACHE_SECONDS_TO_LIVE, TimeUnit.SECONDS).build();
    private final static Map<Class<? extends EstimatedLoadException>, ButtonInfo> exceptionToButtonInfo;
    private final static String baseKey = "yukon.web.modules.dr.estimatedLoad.";
    
    static {
        Builder<Class<? extends EstimatedLoadException>, ButtonInfo> builder = ImmutableMap.builder();
        builder.put(ApplianceCategoryInfoNotFoundException.class, ButtonInfo.INFO);
        builder.put(ApplianceCategoryNotFoundException.class, ButtonInfo.INFO);
        builder.put(NoAppCatFormulaException.class, ButtonInfo.INFO);
        builder.put(NoGearFormulaException.class, ButtonInfo.INFO);
        builder.put(InputOutOfRangeException.class, ButtonInfo.WARN);
        builder.put(InputValueNotFoundException.class, ButtonInfo.WARN);
        builder.put(GearNotFoundException.class, ButtonInfo.ERROR);
        builder.put(LmDataNotFoundException.class, ButtonInfo.ERROR);
        builder.put(LmServerNotConnectedException.class, ButtonInfo.ERROR);
        exceptionToButtonInfo = builder.build();
    }
    
    public enum ButtonInfo {
        INFO(1, "icon-information", baseKey + "button.invalidConfig"),
        WARN(2, "icon-error", baseKey + "button.invalidInput"),
        ERROR(3, "icon-exclamation", baseKey + "button.commsError");
        
        int severity;
        String icon;
        String buttonKey;
        
        ButtonInfo(int level, String iconText, String description) {
            this.severity = level;
            this.icon = iconText;
            this.buttonKey = description;
        };
        
        public int getSeverityLevel() {
            return severity;
        }
        public String getIcon() {
            return icon;
        }
        public String getButtonKey() {
            return buttonKey;
        }
    }

    @Override
    public EstimatedLoadResult findProgramValue(final int programId, boolean blocking) {
        int currentGearId;
        try {
            currentGearId = findCurrentGearId(programId);
        } catch (EstimatedLoadException e) {
            return e;
        }
        return getProgramValue(programId, currentGearId, blocking);
    }

    @Override
    public EstimatedLoadResult findScenarioProgramValue(final int programId, final int scenarioId, boolean blocking) throws GearNotFoundException {
        Map<Integer, ScenarioProgram> scenarioPrograms = scenarioDao.findScenarioProgramsForScenario(scenarioId);
        ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);
        int gearId = estimatedLoadDao.getGearIdForProgramAndGearNumber(programId, scenarioProgram.getStartGear());
        return getProgramValue(programId, gearId, blocking);
    }

    /**
     * Takes a program id and gear id and attempts to calculate the estimated load amounts for that
     * program/gear combination. First checks to see if the cache holds a recently computed value. If so,
     * that value is returned. If not, a new Runnable is created and executed that will insert the value into cache.  
     */
    private EstimatedLoadResult getProgramValue(final int programId, final int gearId, boolean blocking) {
        final MultiKey resultKey = new MultiKey(programId, gearId); 
        EstimatedLoadResult amount = null;
        
        if (blocking) {
            // Block by waiting for the calculation result before continuing.
            if (log.isDebugEnabled()) {
                log.debug("Calculating estimated load value for program id: " + programId + "\t gear id: " + gearId);
            }
            amount = getProgramValueBlocking(programId, gearId);
        } else {
            // If not blocking, first check cache.  
            amount = cache.getIfPresent(resultKey);
            if (amount != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Estimated load cache hit for program id: " + programId + "\t gear id: " + gearId);
                }
            } else {
                // If not found, spawn new thread to calculate and insert into cache.
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (log.isDebugEnabled()) {
                            log.debug("Calculating estimated load value for program id: " + programId + "\t gear id: " + gearId);
                        }
                        getProgramValueBlocking(programId, gearId);
                    }
                });
            }
        }
        return amount;
    }
    /** 
     * Inserts computed program load reduction amounts into a Guava cache.
     * The cache key is based on both program id and gear id, so a single program may have multiple cache entries
     * for multiple gears which is useful for scenarios that use multiple gears for the same program.
     * Multiple calls to get() with an identical resultKey do not spawn multiple Callable objects.  Instead, each
     * duplicate caller will block until the first thread's calculation is complete.  
     * Then the value will be returned to all waiting callers and placed in the cache.
    **/
    private EstimatedLoadResult getProgramValueBlocking(final int programId, final int gearId) {
        final MultiKey resultKey = new MultiKey(programId, gearId); 
        EstimatedLoadResult result = null;
        try {
            result = cache.get(resultKey, new Callable<EstimatedLoadResult>() {
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
        return result;
    }

    @Override
    public EstimatedLoadSummary getControlAreaValue(PaoIdentifier paoId, boolean blocking) {
        Set<Integer> programIdsForControlArea = controlAreaDao.getProgramIdsForControlArea(paoId.getPaoId());
        List<EstimatedLoadResult> programResults = new ArrayList<>();
        Set<Integer> programsInError = new HashSet<>();
        ButtonInfo buttonInfo = ButtonInfo.INFO;
        int highestSeverity = 0;
        
        for (Integer programId : programIdsForControlArea) {
            EstimatedLoadResult result = findProgramValue(programId, blocking);
            if (result instanceof EstimatedLoadException) {
                programsInError.add(programId);
                ButtonInfo info = exceptionToButtonInfo.get(((EstimatedLoadException) result).getClass());
                if (info.getSeverityLevel() > highestSeverity) {
                    highestSeverity = info.getSeverityLevel();
                    buttonInfo = info;
                }
            }
            programResults.add(result);
        }
        return createSummary(paoId, programResults, programsInError, buttonInfo);
    }

    @Override
    public EstimatedLoadSummary getScenarioValue(PaoIdentifier paoId, boolean blocking) {
        Map<Integer, ScenarioProgram> programsForScenario = scenarioDao.findScenarioProgramsForScenario(
                paoId.getPaoId());
        List<EstimatedLoadResult> programAmounts = new ArrayList<>();
        Set<Integer> programsInError = new HashSet<>();
        ButtonInfo buttonInfo = ButtonInfo.INFO;  // Start with the lowest severity.
        int highestSeverity = 0;
        
        for (Integer programId : programsForScenario.keySet()) {
            int startGearNumber = programsForScenario.get(programId).getStartGear();
            EstimatedLoadResult result = null;
            Integer startGearId = null;
            try {
                startGearId = estimatedLoadDao.getGearIdForProgramAndGearNumber(programId, startGearNumber);
            } catch (GearNotFoundException e) {
                // Could not look up the start gear for this scenario program.  This precludes being able to 
                // calculate estimated load values but should still be reported as an error in the summary.
                result = e;
            }
            if (startGearId != null) {
                // Found a startGearId, calculate estimated load levels for this scenario program.
                result = getProgramValue(programId, startGearId, blocking);
            }
            if (result instanceof EstimatedLoadException) {
                // Need to know the most severe error among the constituent programs to display it at the top level.
                programsInError.add(programId);
                ButtonInfo info = exceptionToButtonInfo.get(result.getClass());
                if (info.getSeverityLevel() > highestSeverity) {
                    highestSeverity = info.getSeverityLevel();
                    buttonInfo = info;
                }            
            }
            programAmounts.add(result); 
        }
        return createSummary(paoId, programAmounts, programsInError, buttonInfo);
    }
    
    /** This method takes a Set of EstimatedLoadReductionAmount objects and sums up each of the four estimated load
     * values into one object.  Used by retrieveScenarioValue() and retrieveControlAreaValue() methods to add together
     * the estimated amounts of all of their component programs.
     * 
     * @param paoId The PaoIdentifier of the control area or scenario whose programs are summed.
     * @param programResults The estimated load amounts for each program in the control area/scenario.
     * @param programsInError A set of ProgramId values for the programs in an error state, used for error info popup.
     * @param buttonInfo The button information needed for displaying correct icon depending on the highest error severity.
     * @return The sum of all EstimatedLoadReductionAmount in the set of programAmounts as a single EstimatedLoadReductionAmount.
     */
    private EstimatedLoadSummary createSummary(PaoIdentifier paoId, List<EstimatedLoadResult> programResults, 
            Set<Integer> programsInError, ButtonInfo buttonInfo) {
        
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
        
        return new EstimatedLoadSummary(totalPrograms, contributing, calculating, error, programsInError, buttonInfo,
                new EstimatedLoadAmount(sumConnectedLoad, sumDiversifiedLoad, sumMaxKwSavings, sumNowKwSavings));
    }

    @Override
    public int findCurrentGearId(int programId) throws EstimatedLoadException {
        IGearProgram program = (IGearProgram) getLmProgramBase(programId);
        int gearNumber = 1;
        if (program.getCurrentGear() != null) {
            gearNumber = program.getCurrentGearNumber();
        }
        return estimatedLoadDao.getGearIdForProgramAndGearNumber(programId, gearNumber);
    }

    @Override
    public LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadException {
        LMProgramBase programBase = null;
        try {
            programBase = clientConnection.getProgramSafe(programId);
        } catch (ConnectionException e){
            throw new LmServerNotConnectedException();
        } catch (NotFoundException e) {
            throw new LmDataNotFoundException(programId);
        }
        if (programBase == null) {
            throw new LmDataNotFoundException(programId);
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
        } else if (e instanceof LmDataNotFoundException) {
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

    
    @Override
    public String findIconStringForException(EstimatedLoadException e) {
        return exceptionToButtonInfo.get(e.getClass()).getIcon();
    }

    @Override
    public String findButtonTextForException(EstimatedLoadException e, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return accessor.getMessage(exceptionToButtonInfo.get(e.getClass()).getButtonKey());
    }

}
