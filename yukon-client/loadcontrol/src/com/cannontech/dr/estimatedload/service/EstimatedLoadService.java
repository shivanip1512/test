package com.cannontech.dr.estimatedload.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.loadcontrol.data.LMProgramBase;

public interface EstimatedLoadService {

    /** This method takes a PaoIdentifier of a LM program and calculates its estimated load reduction amounts.
     * 
     * The program must be an assigned program (STARS program) in addition to being an LM program, and it must
     * belong to an appliance category which must have a set per-appliance average load amount (in kW).
     * 
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
    public EstimatedLoadReductionAmount calculateProgramLoadReductionAmounts(PaoIdentifier program)
            throws EstimatedLoadCalculationException;

    /** This method takes a PaoIdentifier of a LM program and calculates its estimated load reduction amounts.
     * 
     * The program must be an assigned program (STARS program) in addition to being an LM program, and it must
     * belong to an appliance category which must have a set per-appliance average load amount (in kW).
     * 
     * The appliance category must also have an estimated load formula assigned to it, and the LM program's current 
     * gear must also have an assigned estimated load formula for the calculation to be able to be performed.
     * 
     * Assuming these appliance category and gear requirements are met, the method will attempt to look up all
     * necessary formulas and input values, check that inputs are valid, then calculated the four estimated load 
     * reduction amounts: Connected Load, Diversified Load, Max kW savings, and kW Savings Now.
     * See YUK-12301 for more info 
     * 
     * @param program The pao identifier of the LM program being calculated
     * @param gearId The gear id (not gear number) of the gear to be used during the estimated load calculation.
     * @return An object which specifies all four estimated load reduction amounts, or else an error message indicating
     * why an error occurred during calculation.
     * @throws EstimatedLoadCalculationException 
     */
    public EstimatedLoadReductionAmount calculateProgramLoadReductionAmounts(PaoIdentifier program, Integer gearId)
            throws EstimatedLoadCalculationException;

}
