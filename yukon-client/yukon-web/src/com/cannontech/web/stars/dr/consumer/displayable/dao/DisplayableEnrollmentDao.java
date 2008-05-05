package com.cannontech.web.stars.dr.consumer.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment;

public interface DisplayableEnrollmentDao {

    public List<DisplayableEnrollment> getDisplayableEnrollments(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext);
    
}
