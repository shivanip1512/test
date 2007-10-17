package com.cannontech.core.service.impl;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.core.service.ActivityLoggerService;

public class ActivityLoggerServiceImpl implements ActivityLoggerService{

  
    public void logEvent(int userID, int accountID, int energyCompanyID, int customerID, String action, String description){
        ActivityLogger.logEvent(userID, accountID, energyCompanyID, customerID, action, description);
    }
      
    public void logEvent(String action, String description){
        ActivityLogger.logEvent(action, description);
    }
        
    public void logEvent(int userID, int paoID, String action, String description){
        ActivityLogger.logEvent(userID, paoID, action, description);
    }
        
    public void logEvent(int userID, String action, String description){
        ActivityLogger.logEvent(userID, action, description);
    }
       
    public void logEvent(int userID, int accountID, int energyCompanyID, 
                                int customerID, int paoID, String action, String description){
        ActivityLogger.logEvent(userID, accountID, energyCompanyID, customerID, paoID, action, description);
    }

}