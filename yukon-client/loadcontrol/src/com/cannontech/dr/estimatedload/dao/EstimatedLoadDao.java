package com.cannontech.dr.estimatedload.dao;

import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;

public interface EstimatedLoadDao {

    /** Returns the appliance category id and average appliance load value in kW for a given LM program 
     * @throws EstimatedLoadCalculationException */
    public EstimatedLoadApplianceCategoryInfo getAcIdAndAverageKwLoadForLmProgram(int lmProgramId)
            throws EstimatedLoadCalculationException;

    /** Returns the gear id of the currently selected gear for the given LM program 
     * @throws EstimatedLoadCalculationException */
    public Integer getCurrentGearIdForProgram(int lmProgramId, int gearNumber);
}
