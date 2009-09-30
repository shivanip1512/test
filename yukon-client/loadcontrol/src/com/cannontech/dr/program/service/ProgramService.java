package com.cannontech.dr.program.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public interface ProgramService
    extends ObjectMapper<DisplayablePao, LMProgramBase>, ProgramDao {

    public DatedObject<LMProgramBase> findDatedProgram(int programId);

    public List<DisplayablePao> findProgramsForLoadGroup(YukonUserContext userContext,
                                                         int loadGroupId);

    public SearchResult<DisplayablePao> filterPrograms(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count);

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
            YukonUserContext userContext, int programId, int gearNumber,
            Date startDate, Date stopDate);

    /**
     * Start the given program with the given gear number, start and end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @param overrideConstraints If this is set to true, constraints will be
     *            overridden. If not, they will be observed.
     */
    public void startProgram(YukonUserContext userContext, int programId,
            int gearNumber, Date startDate, boolean stopScheduled,
            Date stopDate, boolean overrideConstraints, String additionalInfo);

    public void scheduleProgramStop(YukonUserContext userContext,
            int programId, Date stopDate);

    public void stopProgram(YukonUserContext userContext, int programId);

    public ConstraintViolations getConstraintViolationsForStopProgram(
            YukonUserContext userContext, int programId, int gearNumber,
            Date stopDate);

    public void stopProgramWithGear(YukonUserContext userContext,
            int programId, int gearNumber, Date stopDate,
            boolean overrideConstraints);

    public void setEnabled(int programId, boolean isEnabled, YukonUserContext userContext);
}
