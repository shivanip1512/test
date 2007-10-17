package com.cannontech.core.service;


public interface ActivityLoggerService{

  
    public void logEvent(int userID, int accountID, int energyCompanyID, int customerID, String action, String description);
      
    public void logEvent(String action, String description);
        
    public void logEvent(int userID, int paoID, String action, String description);
        
    public void logEvent(int userID, String action, String description);
       
    public void logEvent(int userID, int accountID, int energyCompanyID, 
                                int customerID, int paoID, String action, String description);

}