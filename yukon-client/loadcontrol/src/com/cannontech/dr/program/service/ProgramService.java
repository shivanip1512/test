package com.cannontech.dr.program.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
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

    public void stopProgram(int programId);

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
