package com.cannontech.dr.estimatedload.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException.Type;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInputHolder;
import com.cannontech.dr.estimatedload.PartialEstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.estimatedload.service.FormulaService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class EstimatedLoadServiceImpl implements EstimatedLoadService {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadServiceImpl.class);
    
    @Autowired private LoadControlClientConnection clientConnection;
    @Autowired private DRGroupDeviceMappingDao groupDeviceMappingDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private EstimatedLoadDao estimatedLoadDao;
    @Autowired private FormulaService formulaService;
    @Autowired private FormulaDao formulaDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private ScenarioDao scenarioDao;

    // Time in seconds that estimated load values should be cached.
    private static final int CACHE_TIME_TO_LIVE = 15; 

    private final Map<Integer, Map<Integer, DatedObject<EstimatedLoadReductionAmount>>> loadReductionAmountCache = new HashMap<>();

    @Override
    public EstimatedLoadReductionAmount retrieveEstimatedLoadValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        if (paoId.getPaoType() == PaoType.LM_SCENARIO) {
            return getScenarioValue(paoId);
        } else if (paoId.getPaoType() == PaoType.LM_CONTROL_AREA) {
            return getControlAreaValue(paoId);
        } else if (paoId.getPaoType().isDirectProgram()) {
            return getProgramValue(paoId);
        }
        throw new EstimatedLoadCalculationException(Type.INVALID_PAO_TYPE, paoId);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM program.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount getProgramValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        return getProgramReductionAmountFromCache(paoId);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM control area.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount getControlAreaValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        Set<Integer> programIdsForControlArea = controlAreaDao.getProgramIdsForControlArea(paoId.getPaoId());
        Set<EstimatedLoadReductionAmount> programAmounts = new HashSet<>();
        Set<EstimatedLoadReductionAmount> errors = new HashSet<>();
        
        for (Integer programId : programIdsForControlArea) {
            LMProgramBase programBase = getLmProgramBase(programId);
            programAmounts.add(getProgramReductionAmountFromCache(programBase.getPaoIdentifier()));
        }
        return sumEstimatedLoadAmounts(paoId, programAmounts, errors);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM scenario.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount getScenarioValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        Map<Integer, ScenarioProgram> programsForScenario = scenarioDao.findScenarioProgramsForScenario(
                paoId.getPaoId());
        Set<EstimatedLoadReductionAmount> programAmounts = new HashSet<>();
        Set<EstimatedLoadReductionAmount> errors = new HashSet<>();
        
        for (Integer programId : programsForScenario.keySet()) {
            LMProgramBase programBase = getLmProgramBase(programId);
            int gearId = estimatedLoadDao.getCurrentGearIdForProgram(programId,
                    programsForScenario.get(programId).getStartGear());
            programAmounts.add(getProgramReductionAmountFromCache(programBase.getPaoIdentifier(), gearId)); 
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

    /**
     * This method attempts to retrieve the estimated load reduction amounts for a given program by pao id.
     * Since no gearId is passed in, the method will assume the default gear is to be used unless the 
     * load control client supplies a different current gear number for the program.
     * If the estimated load amount is not found in the cache, or the cached values are too old, they are
     * recalculated.  Otherwise, the cached amount is returned.
     * 
     * @param programId The pao id of the program for which values are being requested.
     * @return The estimated load reduction amounts: connected load, diversified load, max kW savings, now kW savings.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount getProgramReductionAmountFromCache(PaoIdentifier programId)
            throws EstimatedLoadCalculationException {
        return getProgramReductionAmountFromCache(programId, null);
    }

    /**
     * This method attempts to retrieve the estimated load reduction amounts for a given program by pao id.
     * A gearId can be passed in and will be used to look for reduction amounts within the cache.
     * If gearId is not supplied, then either the default gear or current gear given by the load control server is used. 
     * If the estimated load amount is not found in the cache, or the cached values are too old, they are
     * recalculated.  Otherwise, the cached amount is returned.
     * 
     * @param programId The pao id of the program for which values are being requested.
     * @return The estimated load reduction amounts: connected load, diversified load, max kW savings, now kW savings.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount getProgramReductionAmountFromCache(PaoIdentifier programId, Integer gearId)
            throws EstimatedLoadCalculationException {
        // Look for the gear cache within the program cache.  If not found, create the gear cache map.
        Map<Integer, DatedObject<EstimatedLoadReductionAmount>> gearMap = loadReductionAmountCache.get(programId.getPaoId());
        if (gearMap == null) {
            gearMap = new HashMap<>();
        }
        if (gearId == null) {
            gearId = findCurrentGearId(programId);
        }
        DatedObject<EstimatedLoadReductionAmount> datedObject = gearMap.get(gearId);
        // If the cached object is not in cache or older than its time-to-live, recalculate it.
        if (datedObject == null ||
                new LocalTime(datedObject.getDate()).plusSeconds(CACHE_TIME_TO_LIVE).isBefore(new LocalTime())) {
            if(log.isDebugEnabled()) {
                log.debug("Recalculating estimated load for LM program Id: " + programId.getPaoId());
            } 
            try {
                EstimatedLoadReductionAmount amount = calculateProgramLoadReductionAmounts(programId, gearId);
                gearMap.put(gearId, new DatedObject<>(amount));
                loadReductionAmountCache.put(programId.getPaoId(), gearMap);
                return amount;
            } catch (EstimatedLoadCalculationException e) {
                EstimatedLoadReductionAmount error = createEstimatedLoadAmountError(e); 
                gearMap.put(gearId, new DatedObject<>(error));
                loadReductionAmountCache.put(programId.getPaoId(), gearMap);
                return error;
            }
        }
        // The cached object has not exceeded its time-to-live value so return it.
        if(log.isDebugEnabled()) {
            log.debug("Cache hit for LM program Id: " + programId.getPaoId());
        } 
        return datedObject.getObject();
    }

    /** This method takes a PaoIdentifier of a LM program and calculates its estimated load reduction amounts.
     * The program must be an assigned program (STARS program) in addition to being an LM program, as it must
     * belong to an appliance category and that appliance category must have a per-appliance average load amount.
     * The appliance category must also have an estimated load formula assigned to it, and the LM program's current 
     * gear must also have an assigned estimated load formula for the calculation to be able to be performed.
     * 
     * Assuming these appliance category and gear requirements are met, the method will attempt to look up all
     * necessary input values, check that they fall within the valid ranges as specified by each formula, and then
     * calculated the four estimated load reduction amounts: Connected Load, Diversified Load, Max kW savings, and
     * kW Savings Now.  A more detailed description of these terms can be found in the new feature documentation 
     * found on YUK-12301. 
     * 
     * @param program The pao identifier of the LM program being calculated
     * @return An object which specifies all four estimated load reduction amounts, or else an error message indicating
     * why an error occurred during calculation.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount calculateProgramLoadReductionAmounts(PaoIdentifier program, int gearId)
            throws EstimatedLoadCalculationException {

        ApplianceAssetAvailabilitySummary summary = assetAvailabilityService
                .getApplianceAssetAvailability(program);

        PartialEstimatedLoadReductionAmount partialReductionAmount = 
                calculatePartialProgramLoadReductionAmount(program, gearId, summary);

        double kWSavingsNow = calculateKwSavingsNow(program, partialReductionAmount.getMaxKwSavings());

        return new EstimatedLoadReductionAmount(partialReductionAmount.getConnectedLoad(),
                partialReductionAmount.getDiversifiedLoad(), 
                partialReductionAmount.getMaxKwSavings(),
                kWSavingsNow,
                false, null);
        
    }

    /** This method calculates the first three estimated load values: Connected Load, Diversified Load,
     * and Max kW Savings.  This method exists because these partial values need to be computed for every program
     * that shares enrolled devices with the LM program passed to calculateProgramLoadReductionAmounts() 
     * in order to compute kW Savings Now.
     * 
     * @param program The program id for which the partial estimated load values are being calculated.
     * @param gearId The gear id to be used in looking up and calculating the gear formula output. 
     * @return An object holding the Connected Load, Diversified Load, and Max kW Savings of the LM program.
     * @throws EstimatedLoadCalculationException
     */
    private PartialEstimatedLoadReductionAmount calculatePartialProgramLoadReductionAmount(PaoIdentifier program,
            int gearId, ApplianceAssetAvailabilitySummary summary) throws EstimatedLoadCalculationException {
        // Look up all information necessary to begin calculation: appliance category id, app cat per-appliance load,
        // and both app cat and gear formulas.
        EstimatedLoadCalculationInfo loadCalcInfo = gatherCalculationInfo(program, gearId);
        
        // Gather all current input values and build an object which holds the current, max, and min values.
        FormulaInputHolder appCatFormulaInputHolder = formulaService.buildFormulaInputHolder(loadCalcInfo
                .getApplianceCategoryFormula(), loadCalcInfo);
        FormulaInputHolder gearFormulaInputHolder = formulaService.buildFormulaInputHolder(loadCalcInfo
                .getGearFormula(), loadCalcInfo);
        
        // Check validity of all inputs for both appliance category and gear formulas.
        // If inputs are valid, checkValidity returns and we proceed.  If an input is not valid it throws an exception.
        formulaService.checkAllInputsValid(loadCalcInfo.getApplianceCategoryFormula(), appCatFormulaInputHolder);
        formulaService.checkAllInputsValid(loadCalcInfo.getGearFormula(), gearFormulaInputHolder);
        
        // Evaluate formulas and obtain their output values.
        double applianceCategoryFormulaOutput = formulaService.calculateFormulaOutput(
                loadCalcInfo.getApplianceCategoryFormula(), appCatFormulaInputHolder);
        double gearFormulaOutput = formulaService.calculateFormulaOutput(
                loadCalcInfo.getGearFormula(), gearFormulaInputHolder);
        
        // Calculate actual ELRA values: connected load, diversified load, max kW savings
        // by multiplying connected load amount with the formula output values.
        double connectedLoad = loadCalcInfo.getAverageKwLoad() * summary.getActiveSize();
        double diversifiedLoad = connectedLoad * applianceCategoryFormulaOutput;
        double maxKwSavings = diversifiedLoad * gearFormulaOutput;
        return new PartialEstimatedLoadReductionAmount(connectedLoad, diversifiedLoad, maxKwSavings);
        
    }

    private double calculateKwSavingsNow(PaoIdentifier program, double maxKwSavings)
            throws EstimatedLoadCalculationException {
        double reductionFromControllingPrograms = 0.0;
        LMProgramBase calculationProgramBase = getLmProgramBase(program.getPaoId());
//        if (calculationProgramBase.isActive()) {
//            return 0.0; // If the calculation program is 
//        }
        // How many other programs are there that share enrollments with the program being calculated?
        List<Integer> programsWithSharedInventory = estimatedLoadDao.findOtherEnrolledProgramsForDevicesInProgram(
                program.getPaoId());
        if (programsWithSharedInventory.size() > 0) {
            // Find all of the devices in the program being calculated.
            Set<Integer> loadGroupsInCalculationProgram = groupDeviceMappingDao.getLoadGroupIdsForDrGroup(program);
            Set<Integer> inventoryIdsInCalculationProgram = groupDeviceMappingDao.getInventoryAndDeviceIdsForLoadGroups(
                    loadGroupsInCalculationProgram).keySet();
            
            for (Integer controllingProgramId : programsWithSharedInventory) {
                // Is this shared-enrollment program controlling?
                LMProgramBase controllingProgramBase = getLmProgramBase(controllingProgramId);
                if (controllingProgramBase.isActive()) {
                    // Find which gear the currently controlling program is using.
                    int gearId = findCurrentGearId(controllingProgramBase.getPaoIdentifier());
                    
                    PartialEstimatedLoadReductionAmount controllingProgramPartialAmount;
                    ApplianceAssetAvailabilitySummary controllingProgramSummary;
                    try {
                        // Find asset availability summary for the controlling program.
                        controllingProgramSummary = assetAvailabilityService
                                .getApplianceAssetAvailability(controllingProgramBase.getPaoIdentifier());
                        // Find the controlling program's Max kW Savings.
                        controllingProgramPartialAmount = calculatePartialProgramLoadReductionAmount(
                                controllingProgramBase.getPaoIdentifier(), gearId, controllingProgramSummary);
                    } catch (EstimatedLoadCalculationException e) {
                        /* There is a problem calculating the partial estimated load values for this currently
                           controlling program.  Rather than throw out everything for this calculation, we'll skip
                           any contribution it may have had to kW Savings Now and continue on. */
                        continue;
                    }
                    
                    //Which devices are in common between the calculating program and the controlling program?
                    Set<Integer> loadGroupsInControllingProgram = groupDeviceMappingDao
                            .getLoadGroupIdsForDrGroup(controllingProgramBase.getPaoIdentifier());
                    Set<Integer> inventoryIdsInControllingProgram = groupDeviceMappingDao
                            .getInventoryAndDeviceIdsForLoadGroups(loadGroupsInControllingProgram).keySet();
                    SetView<Integer> inventoryInCommon = Sets.intersection(inventoryIdsInCalculationProgram,
                            inventoryIdsInControllingProgram);
                    
                    // Determine what fraction of the controlling program's Max kW Savings 
                    // will contribute to the calculation program's kW Savings Now value.
                    double reductionAmount = controllingProgramPartialAmount.getMaxKwSavings() 
                            * (new Double(controllingProgramSummary.getActiveSize()) / 
                                    controllingProgramSummary.getAll().size())
                            * (new Double(inventoryInCommon.size()) / inventoryIdsInControllingProgram.size());
                    reductionFromControllingPrograms += reductionAmount;
                    
                    // Remove devices in common from set of deviceIdsInProgram so they are not counted multiple times.
                    inventoryIdsInCalculationProgram.removeAll(new HashSet<>(inventoryInCommon));
                }
            }
        }
        return maxKwSavings - reductionFromControllingPrograms;
    }

    private LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadCalculationException {
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

    /** This method handles all of the database work involved in reading in the appliance category id and per-appliance
     * average load value, gear id, and the Formula objects associated with the appliance category and gear.
     * @param program The program for which the data will be gathered
     * @return An object that holds all of the necessary information to begin performing the estimated load calculation,
     * or an error message describing what went wrong when attempting to gather the information.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadCalculationInfo gatherCalculationInfo(PaoIdentifier program, int gearId)
            throws EstimatedLoadCalculationException {
        // Gather the appliance category id and average kW load per appliance
        EstimatedLoadApplianceCategoryInfo acIdAndAverageKwLoadForLmProgram = estimatedLoadDao
                .getAcIdAndAverageKwLoadForLmProgram(program.getPaoId());
        
        Integer applianceCategoryId = acIdAndAverageKwLoadForLmProgram.getApplianceCategoryId();
        Double averageKwLoad = acIdAndAverageKwLoadForLmProgram.getAvgKwLoad();
        
        // Read the estimated load formulas from the database for the given appliance category and gear ids.
        Formula applianceCategoryFormula = formulaDao.getFormulaForApplianceCategory(applianceCategoryId);
        if (applianceCategoryFormula == null) {
            log.debug("No formula assigned for appliance category : " + applianceCategoryId);
            throw new EstimatedLoadCalculationException(Type.NO_FORMULA_FOR_APPLIANCE_CATEGORY);
        }
        Formula gearFormula = formulaDao.getFormulaForGear(gearId);
        if (gearFormula == null) {
            log.debug("No formula assigned for gear: " + gearId);
            throw new EstimatedLoadCalculationException(Type.NO_FORMULA_FOR_GEAR);
        }
        
        return new EstimatedLoadCalculationInfo(applianceCategoryId, averageKwLoad, gearId,
                applianceCategoryFormula, gearFormula);
    }

    /**
     * Looks for the current gear id on an LM program as supplied by the load management client.  
     * If none is found, the default gear (gear number 1) is used for the id lookup.
     * @throws EstimatedLoadCalculationException 
     */
    private int findCurrentGearId(PaoIdentifier program) throws EstimatedLoadCalculationException {
        LMProgramBase programBase = getLmProgramBase(program.getPaoId());
        int gearNumber;
        if (((IGearProgram) programBase).getCurrentGear() == null) {
            gearNumber = 1;
        } else {
            gearNumber = ((IGearProgram) programBase).getCurrentGearNumber();
        }
        return estimatedLoadDao.getCurrentGearIdForProgram(program.getPaoId(), gearNumber);
    }

    /**
     * Returns an estimated load reduction amount object with its isError flag set to true that
     * holds a descriptive EstimatedLoadCalculationException.
     *  
     * @param e An exception whose type describes why the error occurred.
     */
    private EstimatedLoadReductionAmount createEstimatedLoadAmountError(EstimatedLoadCalculationException e) {
        return new EstimatedLoadReductionAmount(null, null, null, null, true, e);
    }

    /** Clears the cached values from the load reduction amount cache. */
    public void clearCache() {
        // Clear data from all inner gear to estimated load reduction amount maps.
        for (Integer gearId : loadReductionAmountCache.keySet()) {
            loadReductionAmountCache.get(gearId).clear();
        }
        // Clear program to gear map data.
        loadReductionAmountCache.clear();
    }

}
