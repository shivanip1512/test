package com.cannontech.stars.dr.jms.service;

public interface DrJmsProgramStatusService {
    /**
     * Sending program status in 5 min interval time with initial delay of 5 min
     */
    public void sendProgramStatus();
}
