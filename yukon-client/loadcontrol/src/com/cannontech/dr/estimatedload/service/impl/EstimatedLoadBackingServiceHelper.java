package com.cannontech.dr.estimatedload.service.impl;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public interface EstimatedLoadBackingServiceHelper {

    /** Retrieves an EstimatedLoadResult object for a given LM program.
     * If the calculation is performed successfully the object returned will be an EstimatedLoadAmount, 
     * which includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * If the calculation results in an error, an EstimatedLoadException will be returned.
     */
    public EstimatedLoadResult getProgramValue(final int paoId);

    /** Retrieves an EstimatedLoadSummary object for a given LM control area.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * The summary object also includes the following information: # of programs in error, # of programs currently
     * being calculated.
     */
    public EstimatedLoadSummary getControlAreaValue(PaoIdentifier paoId);

    /** Retrieves the EstimatedLoadSummary object for a given LM scenario.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * The summary object also includes the following information: # of programs in error, # of programs currently
     * being calculated.
     */
    public EstimatedLoadSummary getScenarioValue(PaoIdentifier paoId);
    
    /**
     * Looks for the current gear id on an LM program as supplied by the load management client.  
     * If none is found, the default gear (gear number 1) is used for the id lookup.
     * @throws EstimatedLoadException 
     */
    public int findCurrentGearId(int programId) throws EstimatedLoadException;

    /**
     * This method attempts to look up an LM program by its programId by querying the load control client connection.
     * If the server cannot be contacted, or the programId requested cannot be found, an exception will be thrown. 
     * @param programId The programId being requested.
     * @return The LMProgramBase object containing information about the state of the LM program.
     * @throws EstimatedLoadException
     */
    public LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadException;

    /**
     * This method receives an EstimatedLoadException and determines which type of estimated load exception it is.
     * It then creates a message to pass back to the backing service which is then resolved and returned for display. 
     * @param e The EstimatedLoadException being resolved.
     * @param userContext The user context of the user viewing the page
     * @return A message describing the problem that caused the exception.
     */
    public MessageSourceResolvable resolveException(EstimatedLoadException e, YukonUserContext userContext);

}
