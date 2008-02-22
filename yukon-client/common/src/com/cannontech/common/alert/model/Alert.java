package com.cannontech.common.alert.model;

import java.util.Date;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.user.checker.UserChecker;

public interface Alert {

    public ResolvableTemplate getMessage();
    
    public UserChecker getUserCheck();
    
    public Date getDate();
    
    public AlertType getType();
    
}
