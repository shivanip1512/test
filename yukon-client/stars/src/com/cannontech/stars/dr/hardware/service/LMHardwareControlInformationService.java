package com.cannontech.stars.dr.hardware.service;

import java.util.Date;


public interface LMHardwareControlInformationService {
    
    public boolean startEnrollment(int inventoryId, int loadGroupID, int accountId);
    
    public boolean stopEnrollment(int inventoryId, int loadGroupID, int accountId);
    
    public boolean startOptOut(int inventoryId, int loadGroupID, int accountId);
    
    public boolean stopOptOut(int inventoryId, int loadGroupID, int accountId);
    
    public long calculateControlHistory(int loadGroupID, int accountId, Date start, Date stop);
}
