package com.cannontech.dr.estimatedload.service;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.GearNotFoundException;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public interface EstimatedLoadBackingServiceHelper {

    /** Retrieves an EstimatedLoadResult object for a given LM program.
     * If the calculation is performed successfully the object returned will be an EstimatedLoadAmount, 
     * which includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * If the requested program isn't in the cache, this returns null and the requested program's calculation begins.
     * If the calculation results in an error, an EstimatedLoadException will be returned.
     * @param paoId The pao id of the program to calculate.
     * @param blocking When true, the function will wait for calculation to complete before returning. 
     * If false, the cache will be checked for a result.  If not present, null is returned immediately and 
     * the result is calculated in a new runnable and inserted into cache. */
    EstimatedLoadResult findProgramValue(final int paoId, boolean blocking);

    /** Retrieves an EstimatedLoadResult object for a given LM program.
     * If the calculation is performed successfully the object returned will be an EstimatedLoadAmount, 
     * which includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * If the requested program isn't in the cache, this returns null and the requested program's calculation begins.
     * If the calculation results in an error, an EstimatedLoadException will be returned.
     * @param programId The pao id of the program to calculate.
     * @param scenarioId The scenario id that the load program is in.
     * @param blocking When true, the function will wait for calculation to complete before returning. 
     * If false, the cache will be checked for a result.  If not present, null is returned immediately and 
     * the result is calculated in a new runnable and inserted into cache. 
     * @throws GearNotFoundException */
    EstimatedLoadResult findScenarioProgramValue(int programId, int scenarioId, boolean blocking) throws GearNotFoundException;

    /** Retrieves an EstimatedLoadSummary object for a given LM control area.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * The summary object also includes the following information: # of programs in error, # of programs currently
     * being calculated.
     * @param paoId The pao identifier of the control area to calculate.
     * @param blocking When true, the function will wait for calculation to complete before returning. 
     * If false, the cache will be checked for a result.  If not present, null is returned immediately and 
     * the result is calculated in a new runnable and inserted into cache. */
    EstimatedLoadSummary getControlAreaValue(PaoIdentifier paoId, boolean blocking);

    /** Retrieves the EstimatedLoadSummary object for a given LM scenario.
     * This includes estimated load fields: connected load, diversified load, and kW savings max/now.
     * The summary object also includes the following information: # of programs in error, # of programs currently
     * being calculated.
     * @param paoId The pao id of the scenario to calculate.
     * @param blocking When true, the function will wait for calculation to complete before returning. 
     * If false, the cache will be checked for a result.  If not present, null is returned immediately and 
     * the result is calculated in a new runnable and inserted into cache. */
    EstimatedLoadSummary getScenarioValue(PaoIdentifier paoId, boolean blocking);
    
    /**
     * Looks for the current gear id on an LM program as supplied by the load management client.  
     * If none is found, the default gear (gear number 1) is used for the id lookup.
     * @throws EstimatedLoadException If the load management server can't be reached, or if it can't supply the current
     * gear number used by the requested lm program pao id.
     */
    int findCurrentGearId(int programId) throws EstimatedLoadException;

    /**
     * This method attempts to look up an LM program by its programId by querying the load control client connection.
     * If the server cannot be contacted, or the programId requested cannot be found, an exception will be thrown. 
     * @param programId The programId being requested.
     * @return The LMProgramBase object containing information about the state of the LM program.
     * @throws EstimatedLoadException If the load management server can't be reached, or if it doesn't have information
     * for a requested lm program pao id.
     */
    LMProgramBase getLmProgramBase(int programId) throws EstimatedLoadException;

    /**
     * This method receives an EstimatedLoadException and determines which type of estimated load exception it is.
     * It then creates a message to pass back to the backing service which is then resolved and returned for display. 
     * @param e The EstimatedLoadException being resolved.
     * @param userContext The user context of the user viewing the page
     * @return A message describing the problem that caused the exception.
     */
    MessageSourceResolvable resolveException(EstimatedLoadException e, YukonUserContext userContext);

    /**
     * Receives an EstimatedLoadException and returns the string that corresponds to that error's severity level.
     * Errors are classified as setup errors, validation errors, or runtime errors in order of increasing severity.
     * @param e The exception whose corresponding icon is requested.
     * @return The string that represents the icon.
     */
    String findIconStringForException(EstimatedLoadException e);

    /**
     * Receives an EstimatedLoadException and returns an i18n key corresponding to the message that will be displayed
     * on the error button displayed on estimated load pages.
     * @param e The exception whose corresponding button text is requested.
     * @return The text that will appear on the button.
     */
    String findButtonTextForException(EstimatedLoadException exception, YukonUserContext userContext);

    

}
