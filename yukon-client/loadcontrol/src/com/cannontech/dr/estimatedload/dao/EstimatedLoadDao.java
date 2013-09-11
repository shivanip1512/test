package com.cannontech.dr.estimatedload.dao;

import com.cannontech.common.util.Pair;

public interface EstimatedLoadDao {

    /** Returns the appliance category id and average appliance load value in kW for a given LM program */
    public Pair<Integer, Double> getAcIdAndAverageKwLoadForLmProgram(int lmProgramId);

    /** Returns the gear id of the currently selected gear for the given LM program */
    public Integer getCurrentGearIdForProgram(int lmProgramId);
}
