package com.cannontech.dr.estimatedload.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
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
    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private FormulaService formulaService;
    @Autowired private FormulaDao formulaDao;

    @Override
    public EstimatedLoadReductionAmount calculateProgramLoadReductionAmounts(PaoIdentifier program)
            throws EstimatedLoadCalculationException {
        int gearId = backingServiceHelper.findCurrentGearId(program);
        return calculateProgramLoadReductionAmounts(program, gearId);
    }

    @Override
    public EstimatedLoadReductionAmount calculateProgramLoadReductionAmounts(PaoIdentifier program, Integer gearId)
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
                LMProgramBase controllingProgramBase = backingServiceHelper.getLmProgramBase(controllingProgramId);
                if (controllingProgramBase.isActive()) {
                    // Find which gear the currently controlling program is using.
                    int gearId = backingServiceHelper.findCurrentGearId(controllingProgramBase.getPaoIdentifier());
                    
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
                    		* ((double) controllingProgramSummary.getActiveSize() /
                                    controllingProgramSummary.getAll().size())
                            * ((double) inventoryInCommon.size() / inventoryIdsInControllingProgram.size());
                    reductionFromControllingPrograms += reductionAmount;
                    
                    // Remove devices in common from set of deviceIdsInProgram so they are not counted multiple times.
                    inventoryIdsInCalculationProgram.removeAll(new HashSet<>(inventoryInCommon));
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

}
