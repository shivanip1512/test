package com.cannontech.stars.dr.hardware.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;


public interface LMHardwareControlInformationService {
    
    public boolean startEnrollment(int inventoryId, int loadGroupID, int accountId,
            int relay, int programId, LiteYukonUser currentUser, boolean useHardwardAddressing);
    
    public boolean stopEnrollment(int inventoryId, int loadGroupID, int accountId, int relay, int programId, LiteYukonUser currentUser);
    
    public void startOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, Instant startDate);
    
    public void stopOptOut(int inventoryId, LiteYukonUser currentUser, Instant stopDate);    
    
    public boolean startOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, Instant startDate);
    
    public boolean stopOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, Instant stopDate);
    
    public List<Integer> getInventoryNotOptedOutForThisProgram(int programId, int accountId);
}
