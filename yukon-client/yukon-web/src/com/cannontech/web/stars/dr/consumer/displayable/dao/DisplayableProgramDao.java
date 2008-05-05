package com.cannontech.web.stars.dr.consumer.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableProgram;

public interface DisplayableProgramDao {

    public DisplayableProgram getDisplayableProgram(Program program, 
            List<ControlHistory> controlHistoryList, boolean applyFilters);
    
    public List<DisplayableProgram> getDisplayablePrograms(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext);
    
    public List<DisplayableProgram> getAllDisplayablePrograms(CustomerAccount customerAccount,
            YukonUserContext yukonUserContext);
    
}
