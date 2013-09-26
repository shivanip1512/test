package com.cannontech.dr.estimatedload.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.LMGearDao;
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
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.estimatedload.service.FormulaService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;

public class EstimatedLoadServiceImpl implements EstimatedLoadService {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadServiceImpl.class);
    
    @Autowired private LoadControlClientConnection clientConnection;
    @Autowired private DRGroupDeviceMappingDao groupDeviceMappingDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private EstimatedLoadDao estimatedLoadDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private FormulaDao formulaDao;
    @Autowired private FormulaService formulaService;
    @Autowired private LMGearDao gearDao;

    // Time in minutes that estimated load values should be cached.
    private static final int CACHE_TIME_TO_LIVE = 1; 

    private final Map<Integer, Map<Integer, DatedObject<EstimatedLoadReductionAmount>>> loadReductionAmountCache = new HashMap<>();

    @Override
    public EstimatedLoadReductionAmount retrieveEstimatedLoadValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException {
        if (paoId.getPaoType() == PaoType.LM_SCENARIO) {
            return retrieveScenarioValue(paoId);
        } else if (paoId.getPaoType() == PaoType.LM_CONTROL_AREA) {
            return retrieveControlAreaValue(paoId);
        } else if (paoId.getPaoType().isDirectProgram()) {
            return retrieveProgramValue(paoId);
        }
        throw new EstimatedLoadCalculationException(Type.INVALID_PAO_TYPE, paoId);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM program.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     */
    private EstimatedLoadReductionAmount retrieveProgramValue(PaoIdentifier paoId) {
        return getProgramReductionAmountFromCache(paoId);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM control area.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     */
    private EstimatedLoadReductionAmount retrieveControlAreaValue(PaoIdentifier paoId) {
        Set<Integer> programIdsForControlArea = controlAreaDao.getProgramIdsForControlArea(paoId.getPaoId());
        Set<EstimatedLoadReductionAmount> programAmounts = new HashSet<>();
        Set<EstimatedLoadReductionAmount> errors = new HashSet<>();
        
        for (Integer programId : programIdsForControlArea) {
            LMProgramBase program = clientConnection.getProgram(programId);
            programAmounts.add(getProgramReductionAmountFromCache(program.getPaoIdentifier()));
        }
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
            } else {
                errors.add(elra);
            }
        }
        return new EstimatedLoadReductionAmount(sumConnectedLoad, sumDiversifiedLoad, sumMaxKwSavings, sumNowKwSavings,
                false, null);
    }

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM scenario.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     */
    private EstimatedLoadReductionAmount retrieveScenarioValue(PaoIdentifier paoId) {
        return null;
    }

    /**
     * This method attempts to retrieve the estimated load reduction amounts for a given program by pao id.
     * If the program is not found in the cache, or the cached values for the program are too old, they are
     * recalculated.  Otherwise, the cached amount is returned.
     * 
     * @param programId The pao id of the program for which values are being requested.
     * @return The estimated load reduction amounts: connected load, diversified load, max kW savings, now kW savings.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadReductionAmount getProgramReductionAmountFromCache(PaoIdentifier programId) {
        // Look for the gear cache within the program cache.  If not found, create the gear cache map.
        Map<Integer, DatedObject<EstimatedLoadReductionAmount>> gearMap = loadReductionAmountCache.get(programId.getPaoId());
        if (gearMap == null) {
            gearMap = new HashMap<>();
        }
        
        int gearId = findCurrentGearId(programId);
        DatedObject<EstimatedLoadReductionAmount> datedObject = gearMap.get(gearId);
        // If the cached object is not in cache or older than its time-to-live, recalculate it.
        if (datedObject == null ||
                new LocalTime(datedObject.getDate()).plusMinutes(CACHE_TIME_TO_LIVE).isBefore(new LocalTime())) {
            try {
                EstimatedLoadReductionAmount amount = calculateProgramLoadReductionAmounts(programId, gearId);;
                gearMap.put(gearId, new DatedObject<>(amount));
                return amount;
            } catch (EstimatedLoadCalculationException e) {
                EstimatedLoadReductionAmount error = createEstimatedLoadAmountError(e); 
                gearMap.put(gearId, new DatedObject<>(error));
                return error;
            }
        }
        
        // The cached object has not exceeded its time-to-live value so return it.
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
        double connectedLoad = calculateConnectedLoad(program, loadCalcInfo.getAverageKwLoad());
        double diversifiedLoad = connectedLoad * applianceCategoryFormulaOutput;
        double maxKwSavings = diversifiedLoad * gearFormulaOutput;
        double kWSavingsNow = calculateKwSavingsNow(program, maxKwSavings);
        
        return new EstimatedLoadReductionAmount(connectedLoad, diversifiedLoad, maxKwSavings, kWSavingsNow, false, null);
    }

    private double calculateKwSavingsNow(PaoIdentifier program, double maxKwSavings) {
        return 0;  // Needs to be implemented.
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
        EstimatedLoadApplianceCategoryInfo acIdAndAverageKwLoadForLmProgram;
        try {
            acIdAndAverageKwLoadForLmProgram = estimatedLoadDao
                    .getAcIdAndAverageKwLoadForLmProgram(program.getPaoId());
        } catch (EstimatedLoadCalculationException e) {
            String programName = clientConnection.getProgramSafe(program.getPaoId()).getYukonName();
            log.debug("Appliance category info not found for LM program: " + program.getPaoId());
            throw new EstimatedLoadCalculationException(Type.APPLIANCE_CATEGORY_INFO_NOT_FOUND, programName);
        }
        Integer applianceCategoryId = acIdAndAverageKwLoadForLmProgram.getApplianceCategoryId();
        Double averageKwLoad = acIdAndAverageKwLoadForLmProgram.getAvgKwLoad();
        
        // Read the estimated load formulas from the database for the given appliance category and gear ids.
        Formula applianceCategoryFormula = formulaDao.getFormulaForApplianceCategory(applianceCategoryId);
        if (applianceCategoryFormula == null) {
            log.debug("No formula assigned for appliance category : " + applianceCategoryId);
            String applianceCategoryName = applianceCategoryDao.getById(applianceCategoryId).getDisplayName();
            throw new EstimatedLoadCalculationException(Type.NO_FORMULA_FOR_APPLIANCE_CATEGORY, applianceCategoryName);
        }
        Formula gearFormula = formulaDao.getFormulaForGear(gearId);
        if (gearFormula == null) {
            log.debug("No formula assigned for gear: " + gearId);
            throw new EstimatedLoadCalculationException(Type.NO_FORMULA_FOR_GEAR, gearDao.getGearName(gearId));
        }
        
        return new EstimatedLoadCalculationInfo(applianceCategoryId, averageKwLoad, gearId,
                applianceCategoryFormula, gearFormula);
    }

    /**
     * Looks for the current gear id on an LM program as supplied by the load management client.  
     * If none is found, the default gear (gear number 1) is used for the id lookup.
     * @throws EstimatedLoadCalculationException 
     */
    private int findCurrentGearId(PaoIdentifier program) {
        LMProgramBase programBase = clientConnection.getProgram(program.getPaoId());
        int gearNumber;
        if (((IGearProgram) programBase).getCurrentGear() == null) {
            gearNumber = 1;
        } else {
            //if (programBase.get)
            gearNumber = ((IGearProgram) programBase).getCurrentGear().getGearNumber();
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

    /** 
     * This method calculates connected load, which is defined as: N * Average Load Per Appliance in kW.
     * N is the number of connected appliances which are available with non-zero run time and are not opted out.
     * 
     * The AssetAvailabilityService is used to find the number of enrolled appliances that are either 
     * connected to one-way DR devices or to two-way DR devices that meet the following requirements: 
     * they have communicated with Yukon within the specified Communication Window time frame,
     * and they have reported non-zero runtime within the specified run-time window time frame. 
     * 
     * @param program The LM program for which the amount is being calculated.
     * @param averageKwLoad The per-appliance average load in kW, specified by the appliance category associated with
     * the LM program.
     * @return The amount of connected load in kW.
     */
    private double calculateConnectedLoad(PaoIdentifier program, double averageKwLoad) {
        ApplianceAssetAvailabilitySummary summary = assetAvailabilityService
                .getApplianceAssetAvailability(program);
        
        return averageKwLoad * summary.getCommunicatingRunningSize();
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
