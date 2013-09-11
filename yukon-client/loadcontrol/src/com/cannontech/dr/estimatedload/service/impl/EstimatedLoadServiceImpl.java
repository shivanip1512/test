package com.cannontech.dr.estimatedload.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.Pair;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInputHolder;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.estimatedload.service.FormulaService;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;

public class EstimatedLoadServiceImpl implements EstimatedLoadService {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadServiceImpl.class);
    
    @Autowired LoadControlClientConnection clientConnection;
    @Autowired DRGroupDeviceMappingDao groupDeviceMappingDao;
    @Autowired AssetAvailabilityService assetAvailabilityService;
    @Autowired ApplianceCategoryDao applianceCategoryDao;
    @Autowired EstimatedLoadDao estimatedLoadDao;
    @Autowired ProgramDao programDao;
    @Autowired FormulaDao formulaDao;
    @Autowired FormulaService formulaService;
    
    Map<Integer, DatedObject<EstimatedLoadReductionAmount>> loadReductionAmountByGroupId;

    public static enum EsimatedLoadField {
        CONNECTED_LOAD,
        DIVERSIFIED_LOAD,
        KW_SAVINGS;
    }

    @Override
    public YukonMessageSourceResolvable getConnectedLoad(PaoIdentifier drPaoIdentifier) {
        return retrieveEstimatedLoadValue(drPaoIdentifier, EsimatedLoadField.CONNECTED_LOAD);
    }

    @Override
    public YukonMessageSourceResolvable getDiversifiedLoad(PaoIdentifier drPaoIdentifier) {
        return retrieveEstimatedLoadValue(drPaoIdentifier, EsimatedLoadField.DIVERSIFIED_LOAD);
    }

    @Override
    public YukonMessageSourceResolvable getKwSavings(PaoIdentifier drPaoIdentifier) {
        return retrieveEstimatedLoadValue(drPaoIdentifier, EsimatedLoadField.KW_SAVINGS);
    }

    /** Extracts the appropriate field from the EstimatedLoadReductionAmount object for each of the
     * possible estimated load fields: connected load, diversified load, and kW savings.
     * 
     * @param paoId The PaoIdentifier of the LM object the calculation is operating on. LM_CONTROL_AREA, LM_SCENARIO,
     * and all LM programs are valid pao types that can be specified in the PaoIdentifier.  
     * @param field The estimated load field being requested.
     * @return A YukonMessageSourceResolvable object describing the numeric quantity of the estimated amount in kW.
     */
    private YukonMessageSourceResolvable retrieveEstimatedLoadValue(PaoIdentifier paoId, EsimatedLoadField field) {
        if (paoId.getPaoType() == PaoType.LM_CONTROL_AREA || paoId.getPaoType() == PaoType.LM_SCENARIO) {
            return blankField();
        } else if (paoId.getPaoType().isLmProgram()) {
            EstimatedLoadReductionAmount reductionAmounts = calculateProgramLoadReductionAmounts(paoId);
            
            if (reductionAmounts == null || reductionAmounts.isError()) {
                return blankField();
            } else {
                switch (field) {
                    case CONNECTED_LOAD:
                        return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.loadAmount", 
                                reductionAmounts.getConnectedLoad());
                    case DIVERSIFIED_LOAD:
                        return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.loadAmount", 
                                reductionAmounts.getDiversifiedLoad());
                    case KW_SAVINGS:
                        return new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.kwSavings", 
                                reductionAmounts.getMaxKwSavings(), reductionAmounts.getNowKwSavings());
                    default:
                        return blankField();
                }
            }
        }
        return blankField();
    }

    /** Returns a message with the text 'N/A' signifying calculation could not be performed */
    private YukonMessageSourceResolvable blankField() {
        return new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
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
     */
    private EstimatedLoadReductionAmount calculateProgramLoadReductionAmounts(PaoIdentifier program) {
        
        // Look up all information necessary to begin calculation: appliance category id, app cat per-appliance load,
        // gear id, and both app cat and gear formulas.
        EstimatedLoadCalculationInfo loadCalcInfo = gatherCalculationInfo(program);
        if (loadCalcInfo.isError()) {
            return createEstimatedLoadAmountError(program, loadCalcInfo.getErrorMessage());
        }
        
        // Gather all current input values and build an object which holds the current, max, and min values.
        FormulaInputHolder appCatFormulaInputHolder = formulaService.buildFormulaInputHolder(loadCalcInfo
                .getApplianceCategoryFormula());
        FormulaInputHolder gearFormulaInputHolder = formulaService.buildFormulaInputHolder(loadCalcInfo
                .getGearFormula());
        
        // Check validity of all inputs for both appliance category and gear formulas.
        // If all inputs are valid, checkValidity returns null.  Otherwise it returns an object with an error message.
        YukonMessageSourceResolvable error;
        error = formulaService.checkValidity(loadCalcInfo.getApplianceCategoryFormula(), appCatFormulaInputHolder);
        if (error != null) {
            return createEstimatedLoadAmountError(program, error);
        }
        error = formulaService.checkValidity(loadCalcInfo.getGearFormula(), gearFormulaInputHolder);
        if (error != null) {
            return createEstimatedLoadAmountError(program, error);
        }
        
        // Evaluate formulas and obtain their output values.
        Double applianceCategoryFormulaOutput = formulaService.calculateFormulaOutput(
                    loadCalcInfo.getApplianceCategoryFormula(), appCatFormulaInputHolder);
        Double gearFormulaOutput = formulaService.calculateFormulaOutput(loadCalcInfo.getGearFormula(),
                    gearFormulaInputHolder);
        
        // Calculate actual ELRA values: connected load, diversified load, max kW savings
        // by multiplying connected load amount with the formula output values.
        Double connectedLoad = calculateConnectedLoad(program, loadCalcInfo.getAverageKwLoad());
        Double diversifiedLoad = connectedLoad * applianceCategoryFormulaOutput;
        Double maxKwSavings = diversifiedLoad * gearFormulaOutput;
        
        return new EstimatedLoadReductionAmount(program.getPaoIdentifier().getPaoId(), connectedLoad, diversifiedLoad,
                maxKwSavings, maxKwSavings, false, null); // When nowKwSavings is calculated, this needs to be changed.
    }

    /** This method handles all of the database work involved in reading in the appliance category id and per-appliance
     * average load value, gear id, and the Formula objects associated with the appliance category and gear.
     * @param program The program for which the data will be gathered
     * @return An object that holds all of the necessary information to begin performing the estimated load calculation,
     * or an error message describing what went wrong when attempting to gather the information.
     */
    private EstimatedLoadCalculationInfo gatherCalculationInfo(PaoIdentifier program) {
        // Gather the appliance category id and average kW load per appliance
        Pair<Integer, Double> acIdAndAverageKwLoadForLmProgram = estimatedLoadDao
                .getAcIdAndAverageKwLoadForLmProgram(program.getPaoId());
        
        if (acIdAndAverageKwLoadForLmProgram == null || acIdAndAverageKwLoadForLmProgram.getFirst() == null) {
            log.debug("Appliance category not found for LM program: " + program.getPaoId());
            String programName = clientConnection.getProgramSafe(program.getPaoId()).getYukonName();
            YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.calcErrors.appCatNotFound", programName);
            return new EstimatedLoadCalculationInfo(error); 
        }
        Integer applianceCategoryId = acIdAndAverageKwLoadForLmProgram.getFirst();
        
        if (acIdAndAverageKwLoadForLmProgram.getSecond() == null) {
            log.debug("Per-appliance average kW load not found for appliance category : "
                    + acIdAndAverageKwLoadForLmProgram.getFirst());
            String appCatName = applianceCategoryDao.getById(applianceCategoryId).getName();
            YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.calcErrors.noAverageKwLoad", appCatName);
            return new EstimatedLoadCalculationInfo(error); 
        }
        Double averageKwLoad = acIdAndAverageKwLoadForLmProgram.getSecond();
        
        // Gather the gear id for the LM program.
        if (estimatedLoadDao.getCurrentGearIdForProgram(program.getPaoId()) == null) {
            log.debug("Gear id not found for LM program: " + program.getPaoId());
            String programName = clientConnection.getProgramSafe(program.getPaoId()).getYukonName();
            YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.calcErrors.gearNotFound", programName);
            
            return new EstimatedLoadCalculationInfo(error);
        }
        Integer gearId = estimatedLoadDao.getCurrentGearIdForProgram(program.getPaoId());
        
        // Read the estimated load formulas from the database for the given appliance category and gear ids.
        Formula applianceCategoryFormula = formulaDao.getFormulaForApplianceCategory(applianceCategoryId);
        if (applianceCategoryFormula == null) {
            log.debug("No formula assigned for appliance category : " + applianceCategoryId);
            YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.calcErrors.error");
            return new EstimatedLoadCalculationInfo(error);
        }
        Formula gearFormula = formulaDao.getFormulaForGear(gearId);
        if (gearFormula == null) {
            log.debug("No formula assigned for gear: " + gearId);
            YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.calcErrors.error");
            return new EstimatedLoadCalculationInfo(error);
        }
        
        return new EstimatedLoadCalculationInfo(applianceCategoryId, averageKwLoad, gearId, applianceCategoryFormula, gearFormula);
    }
    /**
     * Returns an estimated load reduction amount object with its isError flag set to true and a 
     * descriptive error message.
     *  
     * @param program The program for which the calculation error occurred.
     * @param error A message describing why the error occurred.
     */
    private EstimatedLoadReductionAmount createEstimatedLoadAmountError(PaoIdentifier program,
            YukonMessageSourceResolvable error) {
        return new EstimatedLoadReductionAmount(program.getPaoId(), null, null, null, null, true, error);
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
    private Double calculateConnectedLoad(PaoIdentifier program, Double averageKwLoad) {
        ApplianceAssetAvailabilitySummary summary = assetAvailabilityService
                .getApplianceAssetAvailability(program);
        
        return averageKwLoad * summary.getCommunicatingRunningSize();
    }



}
