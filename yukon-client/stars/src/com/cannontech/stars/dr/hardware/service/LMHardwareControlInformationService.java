package com.cannontech.stars.dr.hardware.service;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;


public interface LMHardwareControlInformationService {
    
    public boolean startEnrollment(int inventoryId, int loadGroupID, int accountId, int relay, int programId, LiteYukonUser currentUser);
    
    public boolean stopEnrollment(int inventoryId, int loadGroupID, int accountId, int relay, int programId, LiteYukonUser currentUser);
    
    public void startOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, Date startDate);
    
    public void stopOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, Date stopDate);    
    
    public boolean startOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, Date startDate);
    
    public boolean stopOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, Date stopDate);
    
    public List<Integer> getInventoryNotOptedOutForThisProgram(int programId, int accountId);
}
