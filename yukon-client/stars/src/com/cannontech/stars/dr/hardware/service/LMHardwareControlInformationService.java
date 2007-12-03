package com.cannontech.stars.dr.hardware.service;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;


public interface LMHardwareControlInformationService {
    
    public boolean startEnrollment(int inventoryId, int loadGroupID, int accountId, int relay, LiteYukonUser currentUser);
    
    public boolean stopEnrollment(int inventoryId, int loadGroupID, int accountId, int relay, LiteYukonUser currentUser);
    
    public boolean startOptOut(int inventoryId, int loadGroupID, int accountId, LiteYukonUser currentUser);
    
    public boolean stopOptOut(int inventoryId, int loadGroupID, int accountId, LiteYukonUser currentUser);
    
    public List<Integer> getInventoryNotOptedOut(int inventoryId, int loadGroupId, int accountId);
    
    public long calculateControlHistory(int loadGroupID, int accountId, Date start, Date stop, LiteYukonUser currentUser);
    
    public long getTotalOptOutHoursForRange(int loadGroupId, int accountId, Date start, Date stop, LiteYukonUser currentUser);
}
