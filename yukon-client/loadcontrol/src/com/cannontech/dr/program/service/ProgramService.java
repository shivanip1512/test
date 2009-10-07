package com.cannontech.dr.program.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public interface ProgramService {

    public DisplayablePao getProgram(int programId);
    
    public LMProgramBase getProgramForPao(YukonPao from);
    
    public DatedObject<LMProgramBase> findDatedProgram(int programId);

    public List<DisplayablePao> findProgramsForLoadGroup(int loadGroupId, 
                                                         YukonUserContext userContext);

    public SearchResult<DisplayablePao> filterPrograms(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count, 
                                                       YukonUserContext userContext);

    /**
     * Check to see if any constraints would be violated by starting the program
     * specified by programId using the specified gear and start/end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @return Returns null if no constraints will be violated; otherwise
     *         returns the violated constraints.
     */
    public ConstraintViolations getConstraintViolationForStartProgram(
            int programId, int gearNumber, Date startDate, Date stopDate);

    /**
     * Start the given program with the given gear number, start and end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @param overrideConstraints If this is set to true, constraints will be
     *            overridden. If not, they will be observed.
     */
    public void startProgram(int programId, int gearNumber, Date startDate, boolean stopScheduled,
                             Date stopDate, boolean overrideConstraints, String additionalInfo);

    public void scheduleProgramStop(int programId, Date stopDate);

    public void stopProgram(int programId);

    public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber,
                                                                      Date stopDate);

    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate, 
                                    boolean overrideConstraints);
    
    public void changeGear(int programId, int gearNumber);

    public void setEnabled(int programId, boolean isEnabled);

}
