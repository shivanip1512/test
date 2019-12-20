package com.cannontech.stars.dr.hardware.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.program.service.HardwareEnrollmentInfo;


public interface LMHardwareControlInformationService {
    
    public boolean startEnrollment(HardwareEnrollmentInfo enrollmentStartInfo, LiteYukonUser currentUser, boolean useHardwardAddressing);
    
    public boolean stopEnrollment(HardwareEnrollmentInfo enrollmentStopInfo, LiteYukonUser currentUser);
    
    public void startOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, OptOutEvent event);
    
    public void stopOptOut(int inventoryId, LiteYukonUser currentUser, Instant stopDate);    
    
    public boolean stopOptOut(int inventoryId, int loadGroupID, int accountId, int programId, LiteYukonUser currentUser, Instant stopDate);
    
    public List<Integer> getInventoryNotOptedOutForThisProgram(int programId, int accountId);
}
