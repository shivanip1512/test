package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;

public interface DisplayableProgramDao {

    public DisplayableProgram getDisplayableProgram(Program program, 
                                                    List<ControlHistory> controlHistoryList, 
                                                    ControlPeriod controlPeriod,
                                                    boolean applyFilters);

    public List<DisplayableProgram> getDisplayablePrograms(CustomerAccount customerAccount, 
                                                           YukonUserContext yukonUserContext,
                                                           ControlPeriod controlPeriod);

    public List<DisplayableProgram> getAllDisplayablePrograms(CustomerAccount customerAccount,
                                                              YukonUserContext yukonUserContext, 
                                                              ControlPeriod controlPeriod);

    public DisplayableProgram getDisplayableProgram(CustomerAccount customerAccount, 
                                                    YukonUserContext yukonUserContext,
                                                    Program program, 
                                                    ControlPeriod controlPeriod);

}
