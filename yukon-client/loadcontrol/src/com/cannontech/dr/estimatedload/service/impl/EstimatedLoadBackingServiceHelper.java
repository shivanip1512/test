package com.cannontech.dr.estimatedload.service.impl;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.google.common.cache.Cache;

public interface EstimatedLoadBackingServiceHelper {

    /**
     * Returns the estimated load amount cache to be queried, such as by isValueAvailableImmediately().
     */
    public Cache<Integer, EstimatedLoadReductionAmount> getCache();

    /**
     * Looks for the current gear id on an LM program as supplied by the load management client.  
     * If none is found, the default gear (gear number 1) is used for the id lookup.
     * @throws EstimatedLoadCalculationException 
     */
    public int findCurrentGearId(PaoIdentifier program) throws EstimatedLoadCalculationException;

    public LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadCalculationException;

    /** Retrieves the EstimatedLoadReductionAmount object for a given LM program.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * @throws EstimatedLoadCalculationException 
     */
    public EstimatedLoadReductionAmount getProgramValue(final PaoIdentifier paoId)
            throws EstimatedLoadCalculationException;

    public EstimatedLoadReductionAmount getControlAreaValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException;

    public EstimatedLoadReductionAmount getScenarioValue(PaoIdentifier paoId)
            throws EstimatedLoadCalculationException;
}
