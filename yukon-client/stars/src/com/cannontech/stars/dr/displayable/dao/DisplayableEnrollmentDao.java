package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.user.YukonUserContext;

public interface DisplayableEnrollmentDao {

    public List<DisplayableEnrollment> getDisplayableEnrollments(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext);
    
}
