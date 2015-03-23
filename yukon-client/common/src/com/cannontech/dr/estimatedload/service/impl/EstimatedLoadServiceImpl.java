package com.cannontech.dr.estimatedload.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInputHolder;
import com.cannontech.dr.estimatedload.NoAppCatFormulaException;
import com.cannontech.dr.estimatedload.NoGearFormulaException;
import com.cannontech.dr.estimatedload.PartialEstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.estimatedload.service.FormulaService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;

public class EstimatedLoadServiceImpl implements EstimatedLoadService {
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadServiceImpl.class);
    
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private EstimatedLoadDao estimatedLoadDao;
    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private FormulaService formulaService;
    @Autowired private FormulaDao formulaDao;

    @Override
    public EstimatedLoadResult calculateProgramLoadReductionAmounts(PaoIdentifier program, Integer gearId)
            throws EstimatedLoadException {
        
        ApplianceAssetAvailabilitySummary summary = assetAvailabilityService
                .getApplianceAssetAvailability(program);
        
        PartialEstimatedLoadReductionAmount partialReductionAmount = 
                calculatePartialProgramLoadReductionAmount(program, gearId, summary);
        
        double kWSavingsNow = calculateKwSavingsNow(program, partialReductionAmount.getMaxKwSavings());
        
        return roundValues(partialReductionAmount.getConnectedLoad(),
                partialReductionAmount.getDiversifiedLoad(), 
                partialReductionAmount.getMaxKwSavings(),
                kWSavingsNow);
    }

    /** Rounds the result values to the nearest double representation of integer values. */
    private EstimatedLoadResult roundValues(double connectedLoad, double diversifiedLoad, double maxKwSavings,
            double kwSavingsNow) {
        return new EstimatedLoadAmount(Math.rint(connectedLoad), Math.rint(diversifiedLoad), Math.rint(maxKwSavings),
                Math.rint(kwSavingsNow));
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
            int gearId, ApplianceAssetAvailabilitySummary summary) throws EstimatedLoadException {
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

    /**
     * The purpose of this method is to calculate the current kW Savings Now value. 
     * This value depends on how much the kW Savings Max value is reduced by devices that share enrollments with
     * other programs that are currently controlling, because if a device is in both Program A & Program B, and
     * Program B is currently under control, then starting control on Program A will not obtain any demand reduction
     * from that shared device because it was already under control.
     * To accomplish this, the program being calculated is examined.  Other programs that share devices are identified,
     * then checked to see if they are currently controlling ('active').  If they are, the InventoryId & Relay values
     * for both programs are compared to determine exactly how many enrollments are overlapping and under control.
     * This number is used to determine how much kW Savings Max should be reduced when calculating the resulting 
     * kW Savings Now return value.
     *  
     * @param program The LM program whose values are currently being calculated.
     * @param maxKwSavings The kW Savings Max value is lower than kW Savings Now by an amount that depends
     *  on the number of currently controlling overlapping enrollments.
     * @throws EstimatedLoadException If LM program data can't be retrieved from the load control client connection.
     */
    private double calculateKwSavingsNow(PaoIdentifier program, double maxKwSavings)
            throws EstimatedLoadException {
        double reductionFromControllingPrograms = 0.0;
        List<Integer> previousControllingProgramIds = new ArrayList<>();
        
        // Is the calculating program currently controlling? If so its kw Savings Now will always be zero.
        LMProgramBase calculatingProgramBase = backingServiceHelper.getLmProgramBase(program.getPaoId());
        if (calculatingProgramBase.isActive()) {
            return 0.0;
        }
        // How many other programs are there that share enrollments with the program being calculated?
        List<Integer> programsWithSharedInventory = estimatedLoadDao.findOtherEnrolledProgramsForDevicesInProgram(
                program.getPaoId());
        if (programsWithSharedInventory.size() > 0) {
            Collections.sort(programsWithSharedInventory);  // Ensure deterministic ordering for a given set of ids.
            for (Integer controllingProgramId : programsWithSharedInventory) {
                // Is this shared-enrollment program controlling?
                LMProgramBase controllingProgramBase = backingServiceHelper.getLmProgramBase(controllingProgramId);
                if (controllingProgramBase.isActive()) {
                    // Find which gear the currently controlling program is using.
                    PartialEstimatedLoadReductionAmount controllingProgramPartialAmount;
                    ApplianceAssetAvailabilitySummary controllingProgramSummary;
                    try {
                        // Find asset availability summary for the controlling program.
                        controllingProgramSummary = assetAvailabilityService
                                .getApplianceAssetAvailability(controllingProgramBase.getPaoIdentifier());
                        // Find the controlling program's Max kW Savings.
                        int gearId = backingServiceHelper.findCurrentGearId(controllingProgramId);
                        controllingProgramPartialAmount = calculatePartialProgramLoadReductionAmount(
                                controllingProgramBase.getPaoIdentifier(), gearId, controllingProgramSummary);

                        //Which devices are in common between the calculating program and the controlling program?
                        int inventoryInCommon = estimatedLoadDao.getOverlappingEnrollmentSize(
                                program.getPaoId(), controllingProgramId, previousControllingProgramIds);
                        // Remember previous controlling program ids so their overlaps can be excluded from future sets.
                        previousControllingProgramIds.add(controllingProgramId);
                        
                        // Determine what fraction of the controlling program's Max kW Savings 
                        // will contribute to the calculation program's kW Savings Now value.
                        double reductionAmount = controllingProgramPartialAmount.getMaxKwSavings() 
                                * ((double) inventoryInCommon / controllingProgramSummary.getActiveSize());
                        reductionFromControllingPrograms += reductionAmount;
                    } catch (EstimatedLoadException e) {
                        // There is a problem calculating the partial estimated load values for this currently
                        //   controlling program.  Rather than throw out everything for this calculation, we'll skip
                        //   any contribution it may have had to kW Savings Now and continue on.
                        if (log.isDebugEnabled()) {
                            log.debug("The kW Savings Now calculation for the LM program: " + program + " may contain "
                                    + "inaccuracy because estimated load amounts could not be calculated for a program "
                                    + "that has overlapping enrollments. "
                                    + "The program in error is: " + controllingProgramBase);
                        }
                    }
                }
            }
        }
        return maxKwSavings - reductionFromControllingPrograms;
    }

    /** This method handles all of the database work involved in reading in the appliance category id and per-appliance
     * average load value, gear id, and the Formula objects associated with the appliance category and gear.
     * @param program The program for which the data will be gathered
     * @return An object that holds all of the necessary information to begin performing the estimated load calculation,
     * or an error message describing what went wrong when attempting to gather the information.
     * @throws EstimatedLoadCalculationException 
     */
    private EstimatedLoadCalculationInfo gatherCalculationInfo(PaoIdentifier program, int gearId)
            throws EstimatedLoadException {
        // Gather the appliance category id and average kW load per appliance
        EstimatedLoadApplianceCategoryInfo acIdAndAverageKwLoadForLmProgram = estimatedLoadDao
                .getAcIdAndAverageKwLoadForLmProgram(program.getPaoId());
        
        Integer applianceCategoryId = acIdAndAverageKwLoadForLmProgram.getApplianceCategoryId();
        Double averageKwLoad = acIdAndAverageKwLoadForLmProgram.getAvgKwLoad();
        
        // Read the estimated load formulas from the database for the given appliance category and gear ids.
        Formula applianceCategoryFormula = formulaDao.getFormulaForApplianceCategory(applianceCategoryId);
        if (applianceCategoryFormula == null) {
            log.debug("No formula assigned for appliance category : " + applianceCategoryId);
            throw new NoAppCatFormulaException(applianceCategoryId);
        }
        Formula gearFormula = formulaDao.getFormulaForGear(gearId);
        if (gearFormula == null) {
            log.debug("No formula assigned for gear: " + gearId);
            throw new NoGearFormulaException(gearId);
        }
        
        return new EstimatedLoadCalculationInfo(applianceCategoryId, averageKwLoad, gearId,
                applianceCategoryFormula, gearFormula);
    }

}
