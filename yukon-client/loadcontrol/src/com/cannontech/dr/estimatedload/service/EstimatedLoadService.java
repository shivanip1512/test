package com.cannontech.dr.estimatedload.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;

public interface EstimatedLoadService {

    /** Returns an Estimated Load Reduction Amount object which contains all of the estimated load values:
     * Connected load, Diversified load, Max kW Savings, kW Savings Now. 
     *   
     * @param drPaoIdentifier The pao identifier of the LM object for which connected load data is requested.  
     * Must be a program, control area, or scenario pao identifier.
     * @return An object that contains the numeric amounts of connected load, diversified load, and kW savings.
     * All values are in units of kW.
     * @throws EstimatedLoadCalculationException 
     */
    public EstimatedLoadReductionAmount retrieveEstimatedLoadValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException;

}
