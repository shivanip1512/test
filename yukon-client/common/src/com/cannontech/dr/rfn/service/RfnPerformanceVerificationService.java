package com.cannontech.dr.rfn.service;

public interface RfnPerformanceVerificationService {
    
    /**
     * Starts a scheduled executor that sends a performance verification message to all 
     * enrolled RFN LCR devices in Yukon.
     */
    public void schedulePerformanceVerificationMessaging();
}
