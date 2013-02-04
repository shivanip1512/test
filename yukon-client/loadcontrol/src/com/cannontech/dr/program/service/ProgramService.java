package com.cannontech.dr.program.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.user.YukonUserContext;

public interface ProgramService {

    public DisplayablePao getProgram(int programId);
    
    public LMProgramBase getProgramForPao(YukonPao from);
    
    /**
     * Finds the LMProgramBase object from a Pao. Will throw if the client
     * connection is not valid or if there is no program found for the given pao.
     */
    public LMProgramBase getProgramForPaoSafe(YukonPao from);
    
    public DatedObject<LMProgramBase> findDatedProgram(int programId);

    public List<DisplayablePao> findProgramsForLoadGroup(int loadGroupId, 
                                                         YukonUserContext userContext);

    public SearchResult<DisplayablePao> filterPrograms(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count, 
                                                       YukonUserContext userContext);

    public List<GearAdjustment> getDefaultAdjustments(Date startDate,
            Date stopDate, Collection<ScenarioProgram> scenarioPrograms,
            YukonUserContext userContext);

    /**
     * 
     * Check to see if any constraints would be violated by starting the program
     * specified by programId using the specified gear and start/end time.
     * 
     * Null safe on gearAdjustments
     * 
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @return Returns null if no constraints will be violated; otherwise
     *         returns the violated constraints.
     */
    public ConstraintViolations getConstraintViolationForStartProgram(
            int programId, int gearNumber, Date startDate,
            Duration startOffset, Date stopDate, Duration stopOffset,
            List<GearAdjustment> gearAdjustments);

    /**
     * Start the given program with the given gear number, start and end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @param overrideConstraints If this is set to true, constraints will be
     *            overridden. If not, they will be observed.
     * @throws TimeoutException 
     */
    public ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate,
            Duration startOffset, boolean stopScheduled, Date stopDate,
            Duration stopOffset, boolean overrideConstraints,
            List<GearAdjustment> gearAdjustments) throws TimeoutException;

    /**
     * Starts control for all programs belonging to a given scenario. Returns a
     * ScenarioStatus object which contains the scenarioName, and a list of
     * ProgramStatus for each program that control start was attempted. Each
     * ProgramStatus will contain the updated program status info if successful,
     * those programs that have constraint violations will contain current
     * program status info and the list of violations in their ProgramStatus.
     * @param scenarioName
     * @param startTime
     * @param stopTime
     * @param overrideConstraints Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control start attempted.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     * @throws BadServerResponseException 
     */
    public ScenarioStatus startScenarioByNameBlocking(String scenarioName, Date startTime,
                                 Date stopTime, boolean overrideConstraints,
                                 boolean observeConstraintsAndExecute,
                                 LiteYukonUser user)
             throws NotFoundException, TimeoutException, NotAuthorizedException,
             BadServerResponseException, ConnectionException;

    /**
     * Start the given program with the given gear number, start and end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @param overrideConstraints If this is set to true, constraints will be
     *            overridden. If not, they will be observed.
     */
    public void startProgram(int programId, int gearNumber, Date startDate,
                             Duration startOffset, boolean stopScheduled, Date stopDate,
                             Duration stopOffset, boolean overrideConstraints,
                             List<GearAdjustment> gearAdjustments);

    /**
     * Starts a program. Blocks on Program Start Scheduled. Looks up program by programName.
     * 
     * Handles constraints based on observeConstraints boolean. If overrideConstraints is set true, constraints
     * will not be checked before starting.
     * 
     * @param overrideConstraints : If true causes constraints to not be checked before starting program
     * @param observeConstraints : If true will start a program if it is ok, modifying time as constraints specify

     * @throws NotAuthorizedException
     * @throws NotFoundException
     * @throws TimeoutException
     * @throws BadServerResponseException
     */
    public ProgramStatus startProgramByName(String programName, Date startTime,
                       Date stopTime, String gearName, boolean overrideConstraints,
                       boolean observeConstraints, LiteYukonUser liteYukonUser)
                       throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException;

    /**
     * Starts control for all programs belonging to a given scenario. 
     * Only checks for valid scenario name and user permission, then starts each program in the scenario as a
     * background process and returns immediately.
     * @param scenarioName
     * @param startTime
     * @param stopTime
     * @param overrideConstraints Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public void startScenarioByNameAsynch(String scenarioName, Date startTime, Date stopTime,
                                   boolean overrideConstraints, boolean observeConstraints,
                                   LiteYukonUser user) throws NotFoundException, TimeoutException,
            NotAuthorizedException, BadServerResponseException, ConnectionException;

    /**
     * Schedules the program stop. Non-blocking
     * @throws BadServerResponseException 

     * @throws TimeoutException 
     */
    public void scheduleProgramStop(int programId, Date stopDate, Duration stopOffset);

    /**
     * Schedules the program stop. Blocks on Program Stop Scheduled
     * @throws BadServerResponseException 
     * @throws TimeoutException 
     */
    public ProgramStatus scheduleProgramStopBlocking(int programId, Date stopDate,
                                    Duration stopOffset) throws TimeoutException;

    /**
     * Schedules a stop program. Blocks on Program Stop Scheduled. Looks up program by programName.
     * 
     * Handles constraints based on observeConstraints boolean. If overrideConstraints is set true, constraints
     * will not be checked before scheduling program stop.
     * 
     * @param programName
     * @param stopTime
     * @param overrideConstraints
     * @param observeConstraints
     * @return
     * @throws TimeoutException
     */
    public ProgramStatus scheduleProgramStopByProgramName(String programName, Date stopTime,
                                                          boolean overrideConstraints, boolean observeConstraints) throws TimeoutException;

    public void stopProgram(int programId);

    /**
     * Stop control for all programs belonging to a given scenario.
     * Only checks for valid scenario name and user permission, then stops each program in the scenario as a
     * background process and returns immediately.
     * @param scenarioName
     * @param stopTime
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public void stopScenarioByNameAsynch(String scenarioName, Date stopTime, boolean forceStop,
                                         boolean observeConstraintsAndExecute, LiteYukonUser user)
                             throws NotFoundException, NotAuthorizedException;

    /**
     * Stop control for all programs belonging to a given scenario. Returns a
     * ScenarioStatus object which contains the scenarioName, and a list of
     * ProgramStatus for each program that control stop was attempted. Each
     * program status will contain the updated program status info if
     * successful, those programs that have constraint violations will contain
     * current program status info and the list of violations in their
     * ProgramStatus.
     * @param scenarioName
     * @param stopTime
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control stop attempted.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     * @throws BadServerResponseException 
     */
    public ScenarioStatus stopScenarioByNameBlocking(String scenarioName,
                        Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user) 
                    throws NotFoundException, TimeoutException, NotAuthorizedException, BadServerResponseException;
    
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber,
                                                                      Date stopDate);

    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate, 
                                    boolean overrideConstraints);
    
    public void changeGear(int programId, int gearNumber);
    
    /**
     * Sends an enable or disable program command to the load management service
     * for the program with the specified id.
     */
    public void setEnabled(int programId, boolean isEnabled);
    
    /**
     * Sends an emergency disable program command to the load management service
     * for the program with the specified id.
     */
    public void disableAndSupressRestoration(int programId);

    /**
     * Calls LoadControlClientConnection.getProgramSafe()
     * 
     * @param programId
     * @return
     * @throws ConnectionException
     * @throws NotFoundException
     */
    public LMProgramBase getProgramSafe(int programId) throws ConnectionException, NotFoundException;
}
