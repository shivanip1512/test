package com.cannontech.stars.dr.hardware.service;

import java.util.List;

import org.joda.time.ReadableInstant;

import com.cannontech.database.data.lite.LiteYukonUser;


public interface LMHardwareControlInformationService {
    
    public boolean startEnrollment(int inventoryId, int loadGroupID, int accountId,
            int relay, int programId, LiteYukonUser currentUser, boolean useHardwardAddressing);
    
    public boolean stopEnrollment(int inventoryId, int loadGroupID, int accountId, int relay, int programId, LiteYukonUser currentUser);
    
    public void startOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, ReadableInstant startDate);
    
    public void stopOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, ReadableInstant stopDate);    
    
    public boolean startOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, ReadableInstant startDate);
    
    public boolean stopOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, ReadableInstant stopDate);
    
    public List<Integer> getInventoryNotOptedOutForThisProgram(int programId, int accountId);
}
