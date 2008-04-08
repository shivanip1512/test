package com.cannontech.web.stars.dr.consumer.display;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;

public interface DisplayableProgramDao {

    public List<DisplayableProgram> getDisplayablePrograms(CustomerAccount customerAccount, YukonUserContext yukonUserContext);
    
    public DisplayableOptOut getDisplayableOptOut(CustomerAccount customerAccount, YukonUserContext yukonUserContext);
    
}
