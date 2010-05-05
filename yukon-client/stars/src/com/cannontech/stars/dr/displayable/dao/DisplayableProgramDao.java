package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;

public interface DisplayableProgramDao {

    public DisplayableProgram getDisplayableProgram(Program program, 
                                                    List<ControlHistory> controlHistoryList, 
                                                    ControlPeriod controlPeriod,
                                                    boolean applyFilters);

    /**
     * This method returns a list of displayablePrograms that contain one control history
     * event for each piece of hardware in a given program.  This allows us to get the
     * control summaries for each device without having to deal with all the excess 
     * control history data.
     * 
     */
    public List<DisplayableProgram> getControlHistorySummaryDisplayablePrograms(
                                         CustomerAccount customerAccount, 
                                         YukonUserContext yukonUserContext,
                                         ControlPeriod controlPeriod);

    /**
     * This method returns a list of displayablePrograms that contain one control history
     * event for each piece of hardware in a given program.  This allows us to get the
     * control summaries for each device without having to deal with all the excess 
     * control history data.
     * 
     */
    public List<DisplayableProgram> getAllControlHistorySummaryDisplayablePrograms(
                                         CustomerAccount customerAccount,
                                         YukonUserContext yukonUserContext, 
                                         ControlPeriod controlPeriod);

    public DisplayableProgram getDisplayableProgram(CustomerAccount customerAccount, 
                                                    YukonUserContext yukonUserContext,
                                                    Program program, 
                                                    ControlPeriod controlPeriod);

}
