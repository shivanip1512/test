package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableGroupedControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;

public interface DisplayableProgramDao {

    public DisplayableProgram getDisplayableProgram(Program program, 
                                                    List<ControlHistory> controlHistoryList, 
                                                    ControlPeriod controlPeriod,
                                                    boolean applyFilters,
                                                    boolean past);

    /**
     * This method returns a list of displayablePrograms that contain one control history
     * event for each piece of hardware in a given program.  This allows us to get the
     * control summaries for each device without having to deal with all the excess 
     * control history data.
     * 
     */
    public List<DisplayableProgram> getControlHistorySummary(int accountId, YukonUserContext userContext, ControlPeriod controlPeriod);

    /**
     * This method returns a list of displayablePrograms that contain one control history
     * event for each piece of hardware in a given program.  This allows us to get the
     * control summaries for each device without having to deal with all the excess 
     * control history data.
     * @param past If true, retrieves displayable programs for only past enrollments.
     * If false, retrieves displayable programs for only current enrollments. 
     */
    public List<DisplayableProgram> getAllControlHistorySummary(int accountId, YukonUserContext userContext, ControlPeriod controlPeriod, boolean past);

    public DisplayableProgram getDisplayableProgram(int customerAccountId, YukonUserContext userContext, Program program, ControlPeriod controlPeriod, boolean past);

    /**
     * This method returns a list of DisplayableGroupedControlHistory that contain contiguous control history events.
     * A contiguous event is one or more events with a single start command at the beginning and a single stop command at the end. 
     */
    public List<DisplayableGroupedControlHistory> getDisplayableGroupedControlHistory(List<DisplayableControlHistory> controlHistory);

}